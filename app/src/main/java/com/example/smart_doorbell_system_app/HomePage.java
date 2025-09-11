package com.example.smart_doorbell_system_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_doorbell_system_app.model.UnlockLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class HomePage extends AppCompatActivity {
    private TextView txtLockName;
    private Button btnPassword;
    private Button btnUnlock;
    private Button btnLog;
    private Button btnSecurityLock;

    private String lockId;
    private DatabaseReference lockRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 取得 UI 元件
        txtLockName = findViewById(R.id.txt_LockName);
        btnUnlock = findViewById(R.id.btn_unlock);
        btnLog = findViewById(R.id.btn_unlock_log);
        btnPassword = findViewById(R.id.btn_password_manage);
        btnSecurityLock = findViewById(R.id.btn_security_lock);

        // 從 Intent 拿 lock_id
        lockId = getIntent().getStringExtra(Constants.LOCK_ID);
        if (lockId == null) {
            Toast.makeText(this, "沒有收到 lock ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 設定 Firebase 參考位置
        lockRef = FirebaseDatabase.getInstance().getReference("locks").child(lockId);

        // 載入鎖的基本資料
        loadLockInfo();

        // 讀取security_mode狀態，決定按鈕顏色
        lockRef.child("security_mode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean securityMode = snapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(securityMode)) {
                    // security_mode = true → 紅色
                    btnSecurityLock.setBackgroundResource(R.drawable.button_red);
                    btnSecurityLock.setTextColor(ContextCompat.getColor(HomePage.this, R.color.white));
                    btnSecurityLock.setText("保全上鎖");
                } else {
                    // security_mode = false → 灰色
                    btnSecurityLock.setBackgroundResource(R.drawable.button_gray);
                    btnSecurityLock.setTextColor(ContextCompat.getColor(HomePage.this, R.color.white));
                    btnSecurityLock.setText("保全上鎖");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("HomePage", "Failed to read security_mode", error.toException());
            }
        });

    // 按鈕點擊事件：解鎖 5 秒後自動上鎖
        btnUnlock.setOnClickListener(v -> {
            lockRef.child("allow_to_enter").setValue(true) // 解鎖
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(HomePage.this, "已解鎖", Toast.LENGTH_SHORT).show();
                        Log.d("HomePage", "Lock unlocked");

                        // 新增成功解鎖紀錄
                        addUnlockLog(lockId, "APP", true);

                        // 5 秒後自動鎖回
                        new Handler().postDelayed(() -> {
                            lockRef.child("allow_to_enter").setValue(false)
                                    .addOnSuccessListener(aVoid1 ->
                                            Log.d("HomePage", "Lock locked"))
                                    .addOnFailureListener(e ->
                                            Log.w("HomePage", "Failed to lock", e));
                        }, 5000);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HomePage.this, "解鎖失敗", Toast.LENGTH_SHORT).show();
                        // 新增失敗紀錄
                        addUnlockLog(lockId, "APP", false);

                        Log.w("HomePage", "Unlock failed", e);
                    });
        });

    // 查看解鎖紀錄
        btnLog.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, Unlock_Log.class);
            intent.putExtra(Constants.LOCK_ID, lockId);
            startActivity(intent);
        });
    // 密碼管理
        btnPassword.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, VerifyPassword.class);
            intent.putExtra(Constants.LOCK_ID, lockId);
            intent.putExtra(Constants.FUNCTION_TYPE_NAME, Constants.FunctionType.PASSWORD);
            startActivity(intent);
        });
    // TODO:保全系統
        btnSecurityLock.setOnClickListener(v->{
            lockRef.child("security_mode").get().addOnSuccessListener(snapshot -> {
                Boolean currentValue = snapshot.getValue(Boolean.class);
                boolean newValue = (currentValue == null) ? false : !currentValue; // 預設 false
                lockRef.child("security_mode").setValue(newValue);
            }).addOnFailureListener(e -> {
                Log.e("HomePage", "Failed to toggle security_mode", e);
            });
        });

    }

    /**
     * 從 Firebase 讀取鎖的名稱，顯示在畫面上
     */
    private void loadLockInfo() {
        lockRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String lockName = snapshot.child("name").getValue(String.class);
                    if (lockName != null) {
                        txtLockName.setText(lockName);
                    } else {
                        txtLockName.setText("Lock: " + lockId);
                    }
                } else {
                    Toast.makeText(HomePage.this, "找不到鎖資料", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("HomePage", "loadLockInfo:onCancelled", error.toException());
            }
        });
    }
    /**
     * 新增解鎖紀錄到 lock 的 unlock_logs
     */
    private void addUnlockLog(String lockId, String method, boolean success) {
        // 取得 unlock_logs 路徑
        DatabaseReference logsRef = FirebaseDatabase.getInstance()
                .getReference("locks")
                .child(lockId)
                .child("unlock_logs");

        // 自動產生唯一 key
        String logId = logsRef.push().getKey(); // Firebase 產生唯一 key
//        String logId = "log_" + rawKey;          // 在前面加上 "log_"

        // 產生時間
        long currentTime = System.currentTimeMillis(); // 直接存時間毫秒數(裝置時間)

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 狀態文字，可用 "successed" / "failed"
        String statusText = success ? "successed" : "failed";

        // 建立 UnlockLog 物件
        UnlockLog log = new UnlockLog(method, statusText, ServerValue.TIMESTAMP, uid);  // ServerValue.TIMESTAMP：伺服器時間
        if (logId != null) {
            logsRef.child(logId).setValue(log)
                    .addOnSuccessListener(aVoid -> Log.d("HomePage", "Unlock log added"))
                    .addOnFailureListener(e -> Log.w("HomePage", "Failed to add unlock log", e));
        }
    }

}