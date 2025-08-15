package com.example.smart_doorbell_system_app;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePage extends AppCompatActivity {
    private TextView txtLockName;
    private Button btnUnlock;

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

        // 從 Intent 拿 lock_id
        lockId = getIntent().getStringExtra("lock_id");
        if (lockId == null) {
            Toast.makeText(this, "沒有收到 lock ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 設定 Firebase 參考位置
        lockRef = FirebaseDatabase.getInstance().getReference("locks").child(lockId);

        // 載入鎖的基本資料
        loadLockInfo();

        // 按鈕點擊事件：解鎖 5 秒後自動上鎖
        btnUnlock.setOnClickListener(v -> {
            lockRef.child("status").setValue(true) // 解鎖
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(HomePage.this, "已解鎖", Toast.LENGTH_SHORT).show();
                        Log.d("HomePage", "Lock unlocked");

                        // 5 秒後自動鎖回
                        new Handler().postDelayed(() -> {
                            lockRef.child("status").setValue(false)
                                    .addOnSuccessListener(aVoid1 ->
                                            Log.d("HomePage", "Lock locked"))
                                    .addOnFailureListener(e ->
                                            Log.w("HomePage", "Failed to lock", e));
                        }, 5000);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HomePage.this, "解鎖失敗", Toast.LENGTH_SHORT).show();
                        Log.w("HomePage", "Unlock failed", e);
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
}