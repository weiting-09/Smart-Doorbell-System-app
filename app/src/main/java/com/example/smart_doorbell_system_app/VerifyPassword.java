package com.example.smart_doorbell_system_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerifyPassword extends AppCompatActivity {
    private String lockId;
    private DatabaseReference lockRef;
    private Constants.FunctionType function_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText edtPassword = findViewById(R.id.edt_verify_password);
        edtPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnVerify = findViewById(R.id.btn_verify_ok);

        lockId = getIntent().getStringExtra(Constants.LOCK_ID);
        function_type = (Constants.FunctionType) getIntent().getSerializableExtra(Constants.FUNCTION_TYPE_NAME);

        if (lockId == null) {
            Toast.makeText(this, "沒有收到 lock ID", Toast.LENGTH_SHORT).show();
            finish();
        }
        lockRef = FirebaseDatabase.getInstance().getReference("locks").child(lockId);

        btnVerify.setOnClickListener(v -> {
            String password = edtPassword.getText().toString();
            checkPassword(password);
        });
        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }

    private void checkPassword(String password) {
        lockRef.child("passwords").child("password")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        showToast("無法取得密碼");
                        return;
                    }

                    String correctPassword = String.valueOf(task.getResult().getValue());
                    if (!correctPassword.equals(password)) {
                        showToast("密碼錯誤");
                        return;
                    }

                    showToast("密碼正確");
                    startActivity(createManagerIntent(function_type, lockId));
                    finish();
                });
    }

    // 建立 Intent
    private Intent createManagerIntent(Constants.FunctionType type, String lockId) {
        Class<?> targetClass = (type == Constants.FunctionType.PASSWORD)
                ? PasswordManager.class
                : UnlockKeyList.class;

        Intent intent = new Intent(this, targetClass);
        intent.putExtra(Constants.LOCK_ID, lockId);
        intent.putExtra(Constants.FUNCTION_TYPE_NAME, type);
        return intent;
    }

    // 提示訊息
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}