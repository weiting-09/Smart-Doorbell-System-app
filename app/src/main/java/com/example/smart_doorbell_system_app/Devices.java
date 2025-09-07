package com.example.smart_doorbell_system_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Devices extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    private LinearLayout devicesContainer; // 用來放 lock 按鈕的容器
    private FloatingActionButton fabAddDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_devices);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化 Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        devicesContainer = findViewById(R.id.devicesContainer);
        fabAddDevices = findViewById(R.id.fab_add_devices);

        loadUserLocks();

        fabAddDevices.setOnClickListener(v -> {
            Intent intent = new Intent(Devices.this, Connect.class);
            startActivity(intent);
        });
    }

//    從 Firebase 讀取使用者擁有的 locks
    private void loadUserLocks() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "尚未登入", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = user.getUid();

        dbRef.child("users").child(uid).child("locks")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        devicesContainer.removeAllViews(); // 清空舊按鈕

                        for (DataSnapshot lockSnapshot : snapshot.getChildren()) {
                            String lockId = lockSnapshot.getKey();
                            Boolean hasAccess = lockSnapshot.getValue(Boolean.class);

                            if (Boolean.TRUE.equals(hasAccess)) {
                                addLockButton(lockId);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("Devices", "loadUserLocks:onCancelled", error.toException());
                    }
                });
    }

//    動態建立按鈕
    private void addLockButton(final String lockId) {
        Button btn = new Button(this);
        btn.setText("Lock: " + lockId);

        // 設定按鈕格式
        btn.setBackgroundResource(R.drawable.button_gray);  //背景
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);    // 設定字體大小
        // 建立 LayoutParams (對應 match_parent, 40dp, margin=10dp)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()
                )
        );
        int margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()
        );
        params.setMargins(margin, margin, margin, margin);
        btn.setLayoutParams(params);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "尚未登入", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = user.getUid();
        Log.d("Devices", "Click lockId: " + lockId + ", user uid: " + uid);

        btn.setOnClickListener(v -> {
            dbRef.child("locks").child(lockId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Intent intent;
                            if (snapshot.hasChild("passwords")) {
                                // 有 passwords 欄位 → 跳 HomePage
                                intent = new Intent(Devices.this, HomePage.class);
                            } else {
                                // 沒有 passwords 欄位 → 跳 Password_setting
                                intent = new Intent(Devices.this, PasswordSetting.class);
                            }
                            intent.putExtra("lock_id", lockId);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Devices", "Firebase read failed: " + error.getMessage());
                            Toast.makeText(Devices.this, "Firebase 讀取失敗", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        devicesContainer.addView(btn);
    }

}