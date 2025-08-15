package com.example.smart_doorbell_system_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_doorbell_system_app.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class register extends AppCompatActivity {
    private FirebaseAuth mAuth;

    EditText edt_setUsername;
    EditText edt_setEmail;
    EditText edt_setPassword;
    EditText edt_checkPassword;
    Button btn_signup;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        edt_setUsername = (EditText)findViewById(R.id.edt_setUsername);
        edt_setEmail = (EditText)findViewById(R.id.edt_setEmail);
        edt_setPassword = (EditText)findViewById(R.id.edt_setPassword);
        edt_checkPassword = (EditText)findViewById(R.id.edt_checkPassword);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_signup = (Button)findViewById(R.id.btn_signup);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to login Page
                Intent intent = new Intent();
                intent.setClass(register.this, MainActivity.class); // 導向主畫面(暫稱MainPage)
                startActivity(intent);
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = edt_setUsername.getText().toString();
                String user_email = edt_setEmail.getText().toString();
                String user_password = edt_setPassword.getText().toString();
                String user_checkPassword = edt_checkPassword.getText().toString();

                if (user_name.isEmpty()) {
                    edt_setUsername.setError("請輸入用戶名稱");
                    edt_setUsername.requestFocus();
                    return;
                }
                if (user_email.isEmpty()) {
                    edt_setEmail.setError("請輸入電子郵件");
                    edt_setEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {
                    edt_setEmail.setError("請輸入有效的電子郵件地址");
                    edt_setEmail.requestFocus();
                    return;
                }
                if (user_password.isEmpty()) {
                    edt_setPassword.setError("請輸入密碼");
                    edt_setPassword.requestFocus();
                    return;
                }
                if (user_checkPassword.isEmpty()) {
                    edt_checkPassword.setError("請再次輸入密碼");
                    edt_checkPassword.requestFocus();
                    return;
                }
                if (user_checkPassword.equals(user_password) ){
                    mAuth.createUserWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(register.this, "成功註冊", Toast.LENGTH_SHORT).show();
                                        // 取得當前使用者 UID
                                        String uid = mAuth.getCurrentUser().getUid();
                                        // 建立要儲存的使用者資料物件
                                        User user = new User(user_name, user_email);
                                        // create user info in realtime database
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference usersRef = database.getReference("users");
                                        usersRef.child(uid).setValue(user);

                                        // to device Page
                                        Intent intent = new Intent();
                                        intent.setClass(register.this, Devices.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    edt_checkPassword.setError("錯誤：未輸入相同密碼");
                    edt_checkPassword.requestFocus();
                    return;
                }

            }
        });


    }
}