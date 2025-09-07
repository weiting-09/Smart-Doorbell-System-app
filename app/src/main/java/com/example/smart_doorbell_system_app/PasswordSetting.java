package com.example.smart_doorbell_system_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PasswordSetting extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        String lockId = getIntent().getStringExtra(Constants.LOCK_ID);

        EditText editTextPassword = findViewById(R.id.edt_first_password);
        EditText editTextPasswordCheck = findViewById(R.id.edt_first_password_check);
        Button btnConfirm = findViewById(R.id.btn_ok);

        btnConfirm.setOnClickListener(v -> {// 按下確認按鈕後的處理
            String password = editTextPassword.getText().toString();
            String password_check = editTextPasswordCheck.getText().toString();
            checkPassword(password, password_check, lockId);
        });
    }

    private void checkPassword(String password, String password_check, String lockId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "尚未登入", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lockId == null || lockId.isEmpty()) {
            Toast.makeText(this, "無效的 lockId", Toast.LENGTH_SHORT).show();
            Log.e("PasswordSetting", "lockId is null or empty");
            return;
        }

        String uid = user.getUid();
        Log.d("Devices", "Click lockId: " + lockId + ", user uid: " + uid);
        if (password.equals(password_check)) {
            if (password.isEmpty()) {
                Toast.makeText(this, "密碼不可為空", Toast.LENGTH_SHORT).show();
                return;
            } else if (password.length() > 16) {
                Toast.makeText(this, "密碼長度不可超過16", Toast.LENGTH_SHORT).show();
                return;
            } else if (!password.matches("\\d+")) {
                Toast.makeText(this, "密碼只能包含數字", Toast.LENGTH_SHORT).show();
                return;
            }

            dbRef.child("locks").child(lockId).child("passwords")
                    .child("password").setValue(password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "密碼設定成功", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(this, HomePage.class);
                            intent.putExtra(Constants.LOCK_ID, lockId);
                            startActivity(intent);

                            finish();// 關閉 Password_setting，避免返回鍵回來
                        } else {
                            Exception e = task.getException();
                            Toast.makeText(this, "密碼設定失敗：" + (e != null ? e.getMessage() : "未知錯誤"), Toast.LENGTH_SHORT).show();
                            Log.e("Firebase", "寫入失敗", e);
                        }
                    });
        } else {
            Toast.makeText(this, "密碼不一致，請重新輸入", Toast.LENGTH_SHORT).show();
        }
    }
}