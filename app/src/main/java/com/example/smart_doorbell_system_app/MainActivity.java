package com.example.smart_doorbell_system_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText edt_email;
    EditText edt_password;
    Button btn_login;
    Button btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 先檢查是否登錄
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){   // 尚未登錄
            setContentView(R.layout.activity_main);
            edt_email = (EditText)findViewById(R.id.edt_email);
            edt_password = (EditText)findViewById(R.id.edt_password);
            btn_login = (Button)findViewById(R.id.btn_sign_in);
            btn_signup = (Button)findViewById(R.id.btn_signup);

            // 點擊sign_in後做的動作
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user_email = edt_email.getText().toString();
                    String user_password = edt_password.getText().toString();

                    if (user_email.isEmpty()) {
                        edt_email.setError("請輸入電子郵件");
                        edt_email.requestFocus();
                        return;
                    }
                    if (user_password.isEmpty()) {
                        edt_password.setError("請輸入密碼");
                        edt_password.requestFocus();
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                                        // to MainPage
                                        Intent intent = new Intent();
                                        intent.setClass(MainActivity.this, Devices.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            });
            // 點擊註冊後做的動作
            btn_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // to signup Page
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, Register.class);
                    startActivity(intent);
                }
            });
        } else{ // 已登錄，直接跳到主畫面
            // to MainPage
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Devices.class); // 導向主畫面(暫稱MainPage)
            startActivity(intent);
            finish();
        }

    }
}