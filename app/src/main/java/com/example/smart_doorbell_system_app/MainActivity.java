package com.example.smart_doorbell_system_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnSignIn, btnSignup, btnForgot;
    private ImageView imgHome, imgView, uploadImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 對應你的 XML 檔名

        // 取得元件
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignup = findViewById(R.id.btn_signup);
        btnForgot = findViewById(R.id.btn_forgot);
        imgHome = findViewById(R.id.img_home);
        imgView = findViewById(R.id.img_view);
        uploadImg = findViewById(R.id.upload_img);

        // Sign in 按鈕事件
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "請輸入 Email 和 Password", Toast.LENGTH_SHORT).show();
                } else {
                    // 這裡可以放登入驗證邏輯
                    Toast.makeText(MainActivity.this, "登入成功！", Toast.LENGTH_SHORT).show();

                    // 如果要跳轉頁面
                    // Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    // startActivity(intent);
                }
            }
        });

        // 註冊按鈕事件
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "前往註冊頁面", Toast.LENGTH_SHORT).show();

                // 註冊按鈕事件
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);

            }
        });

        // 忘記密碼事件
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "忘記密碼", Toast.LENGTH_SHORT).show();

                // Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                // startActivity(intent);
            }
        });

        // 頭像上傳點擊
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "上傳頭像", Toast.LENGTH_SHORT).show();
                // 可開啟圖片選擇器 Intent
            }
        });

        // Home 圖示
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "回首頁", Toast.LENGTH_SHORT).show();
            }
        });

        // 其他圖示
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "其他功能", Toast.LENGTH_SHORT).show();
            }
        });
    }
}