package com.example.smart_doorbell_system_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PasswordManager extends AppCompatActivity {
    private String lockId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lockId = getIntent().getStringExtra(Constants.LOCK_ID);

        Button btn_change_password = findViewById(R.id.btn_change_password);
        Button btn_reserve_password = findViewById(R.id.btn_reserve_password);

        btn_change_password.setOnClickListener(v -> {
            Intent intent = new Intent(this, PasswordSetting.class);
            intent.putExtra(Constants.LOCK_ID, lockId);
            startActivity(intent);
        });

        btn_reserve_password.setOnClickListener(v -> {
            Intent intent = new Intent(this, UnlockKeyList.class);
            intent.putExtra(Constants.LOCK_ID, lockId);
            startActivity(intent);
        });
    }
}