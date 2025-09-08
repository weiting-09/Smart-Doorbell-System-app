package com.example.smart_doorbell_system_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UnlockKeyList extends AppCompatActivity {
    private String lockId;
    private LinearLayout buttonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unlock_key_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lockId = getIntent().getStringExtra(Constants.LOCK_ID);
        loadKeys();
    }

    private void loadKeys() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("locks")
                .child(lockId)
                .child("passwords")
                .child("temp_passwords");
        buttonContainer = findViewById(R.id.button_container);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                buttonContainer.removeAllViews();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String key = child.getKey();
                    if (key == null) continue;
                    createButton(key, child);
                }
                createButton("新增", null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UnlockKeyList.this, "讀取失敗：" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createButton(String key, DataSnapshot child) {
        Button btn = new Button(UnlockKeyList.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                150
        );
        params.setMargins(30, 20, 30, 0);
        btn.setLayoutParams(params);
        btn.setText(key);
        btn.setTextSize(20);
        btn.setBackgroundResource(R.drawable.button_gray);

        btn.setOnClickListener(v -> {
            if (key.equals("新增")) {

            }else {
                showRenameDialog(key, child);
            }
        });

        buttonContainer.addView(btn);
    }

    private void showRenameDialog(String oldKey, DataSnapshot childSnapshot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("重新命名 " + oldKey);

        final EditText input = new EditText(this);
        input.setHint("輸入新名稱");
        builder.setView(input);

        builder.setPositiveButton("確定", (dialog, which) -> {
            String newKey = input.getText().toString().trim();
            if (!newKey.isEmpty()) {
                DatabaseReference parentRef = FirebaseDatabase.getInstance()
                        .getReference("locks")
                        .child(lockId)
                        .child("passwords")
                        .child("temp_passwords");

                Object data = childSnapshot.getValue();

                parentRef.child(newKey).setValue(data)
                        .addOnSuccessListener(aVoid -> {
                            parentRef.child(oldKey).removeValue();
                            Toast.makeText(this, "已將 " + oldKey + " 改名為 " + newKey, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "更新失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.setNeutralButton("刪除", (dialog, which) -> {
            DatabaseReference parentRef = FirebaseDatabase.getInstance()
                    .getReference("locks")
                    .child(lockId)
                    .child("passwords")
                    .child("temp_passwords");

            parentRef.child(oldKey).removeValue()
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "已刪除 " + oldKey, Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "刪除失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
        builder.show();
    }

}