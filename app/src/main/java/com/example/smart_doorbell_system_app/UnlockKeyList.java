package com.example.smart_doorbell_system_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_doorbell_system_app.model.RFID;
import com.example.smart_doorbell_system_app.model.ReservePassword;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UnlockKeyList extends AppCompatActivity {
    private String lockId;
    private Constants.FunctionType functionType;
    private LinearLayout buttonContainer;
    private DatabaseReference rfidRef;
    private DatabaseReference passwordRef;

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

        buttonContainer = findViewById(R.id.button_container);

        lockId = getIntent().getStringExtra(Constants.LOCK_ID);
        functionType = (Constants.FunctionType) getIntent().getSerializableExtra(Constants.FUNCTION_TYPE_NAME);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("locks")
                .child(lockId);
        passwordRef = ref.child("passwords").child("temp_passwords");
        rfidRef = ref.child("RFIDs");

        Log.d("UnlockKeyListActivity", functionType.name());
        if(functionType == Constants.FunctionType.PASSWORD)
            loadKeys(passwordRef, ReservePassword.class);
        else if (functionType == Constants.FunctionType.RFID)
            loadKeys(rfidRef.child("cards"), RFID.class);
    }

    private <T> void loadKeys(DatabaseReference ref, Class<T> modelClass) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                buttonContainer.removeAllViews();

                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String key = child.getKey();
                        T model = child.getValue(modelClass);
                        if (key == null || model == null) continue;

                        createButton(key, model);
                    }

                    createButton("新增", modelClass.getConstructor().newInstance());
                } catch (Exception e) {
                    Toast.makeText(UnlockKeyList.this, "初始化失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("UnlockKeyListActivity", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UnlockKeyList.this, "讀取失敗：" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createButton(String key, Object model) {
        String keyName = "新增";
        Button btn = new Button(UnlockKeyList.this);

        if (functionType == Constants.FunctionType.PASSWORD) {
            ReservePassword passwordModel = (ReservePassword) model;

            if (!passwordModel.get_temp_password_name().isEmpty()) {
                keyName = passwordModel.get_temp_password_name();
            }

            btn.setOnClickListener(v -> {
                Intent intent = new Intent(UnlockKeyList.this, ReservePasswordSetting.class);
                intent.putExtra(Constants.LOCK_ID, lockId);
                intent.putExtra(Constants.KEY, key);
                intent.putExtra("model", passwordModel);
                startActivity(intent);
            });

        } else if (functionType == Constants.FunctionType.RFID) {
            RFID rfidModel = (RFID) model;

            if (!rfidModel.getName().isEmpty()) {
                keyName = rfidModel.getName();
            }

            btn.setOnClickListener(v -> {
                if(key.equals("新增")){
                    rfidRef.child("add_new_RFID").setValue(true);
                    Toast.makeText(this, "請感應卡片", Toast.LENGTH_SHORT).show();
                }else {
                    showRFIDRenameDialog(key, rfidModel);
                }
            });

        } else {
            btn.setOnClickListener(v ->
                    Toast.makeText(this, "未知的 model 類型", Toast.LENGTH_SHORT).show()
            );
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                150
        );
        params.setMargins(30, 20, 30, 0);
        btn.setLayoutParams(params);
        btn.setText(keyName);
        btn.setTextSize(20);
        btn.setBackgroundResource(R.drawable.button_gray);

        buttonContainer.addView(btn);
    }


    private void showRFIDRenameDialog(String key, RFID rfidModel) {
        String oldKey = rfidModel.getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 載入自訂 layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rfid_rename, null);
        builder.setView(dialogView);

        TextView rename = dialogView.findViewById(R.id.txt_rename_RFID);
        EditText edtInput = dialogView.findViewById(R.id.edt_input);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        rename.setText("重新命名 " + oldKey);

        // 建立對話框
        AlertDialog dialog = builder.show();
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_round_dialog));

        // 按鈕事件
        btnOk.setOnClickListener(v -> {
            String newKey = edtInput.getText().toString().trim();
            if (!newKey.isEmpty()) {
                rfidModel.setName(newKey);
                rfidRef.child("cards").child(key).setValue(rfidModel)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "已將 " + oldKey + " 改名為 " + newKey, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "更新失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "名稱不能為空", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            rfidRef.child("cards").child(key).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "已刪除 " + oldKey, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "刪除失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }

}