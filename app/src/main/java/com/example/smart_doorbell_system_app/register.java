package com.example.smart_doorbell_system_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtPassword, edtCheckPassword;
    private Button btnSignup, btnSignIn, btnHave;
    private ImageView imgBack, imgHome, imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // 這裡要對應你的 XML 檔名

        // 找到 UI 元件
        edtUsername = findViewById(R.id.edt_setUsername);
        edtEmail = findViewById(R.id.edt_setEmail);
        edtPassword = findViewById(R.id.edt_setPassword);
        edtCheckPassword = findViewById(R.id.edt_checkPassword);

        btnSignup = findViewById(R.id.btn_signup);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnHave = findViewById(R.id.btn_have);

        imgBack = findViewById(R.id.img_back);
        imgHome = findViewById(R.id.img_home);
        imgView = findViewById(R.id.img_view);

        // 註冊按鈕
        btnSignup.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String checkPassword = edtCheckPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || checkPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "請填寫所有必填欄位", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(checkPassword)) {
                Toast.makeText(SignupActivity.this, "兩次輸入的密碼不一致", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignupActivity.this, "註冊成功！", Toast.LENGTH_SHORT).show();
                // 這裡可加上資料存入 DB/Firebase 的程式
                finish(); // 回到登入頁
            }
        });

        // Sign In 按鈕 (回登入頁)
        btnSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // 已有帳號? (回登入頁)
        btnHave.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // 上一頁
        imgBack.setOnClickListener(v -> finish());

        // Home
        imgHome.setOnClickListener(v -> {
            Toast.makeText(SignupActivity.this, "回首頁", Toast.LENGTH_SHORT).show();
        });

        // 其他功能
        imgView.setOnClickListener(v -> {
            Toast.makeText(SignupActivity.this, "其他功能", Toast.LENGTH_SHORT).show();
        });
    }
}
