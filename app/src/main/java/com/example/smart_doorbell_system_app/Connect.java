package com.example.smart_doorbell_system_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class Connect extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    private EditText edtConnectCode;
    private Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connect);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化 Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        edtConnectCode = findViewById(R.id.edt_connect_code);
        btnConnect = findViewById(R.id.btn_connect);

        btnConnect.setOnClickListener(v->{
            // 讀取 edtConnectCode 輸入的 connectCode
            String connectCode = edtConnectCode.getText().toString().trim();
            if (connectCode.isEmpty()) {
                edtConnectCode.setError("請輸入辨識碼");
                edtConnectCode.requestFocus();
                return;
            }

            // 讀取 connect 節點
            dbRef.child("connect").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean found = false;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String lockId = child.getKey();      // e.g., lock_001
                        String lockCode = child.getValue(String.class); // e.g., ABC123

                        if (lockCode != null && lockCode.equals(connectCode)) {
                            found = true;   // 找到符合的 lock_id

                            // 在資料庫中建立user-lock
                            String uid = mAuth.getCurrentUser().getUid();
                            // 在 users/(uid)/locks/(lock_id): true
                            dbRef.child("users").child(uid)
                                    .child("locks").child(lockId).setValue(true);
                            // 在 locks/(lock_id)/authorized_users/(uid): "guest"
                            dbRef.child("locks").child(lockId)
                                    .child("authorized_users").child(uid).setValue("guest")
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(Connect.this, "連線成功！Lock ID: " + lockId, Toast.LENGTH_SHORT).show();
                                        finish(); // 成功則返回上一頁
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(Connect.this, " 資料建立失敗: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                            break;
                        }
                    }

                    if (!found) {
                        Toast.makeText(Connect.this, "找不到對應的門鎖", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Connect.this, "讀取失敗: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}