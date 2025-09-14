package com.example.smart_doorbell_system_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smart_doorbell_system_app.model.ReservePassword;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReservePasswordSetting extends AppCompatActivity {
    private DatabaseReference dbRef;
    private String lockId;
    private String keyId;
    private ReservePassword reserve_password_model;
    String temp_password_name = null;
    String temp_password = null;
    long valid_start = 0;
    long valid_until = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reserve_password_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbRef = FirebaseDatabase.getInstance().getReference();
        lockId = getIntent().getStringExtra(Constants.LOCK_ID);
        keyId = getIntent().getStringExtra(Constants.KEY);
        reserve_password_model = (ReservePassword) getIntent()
                .getSerializableExtra("model");

        EditText edt_reserve_password = findViewById(R.id.edt_reserve_password);
        EditText edt_password_name = findViewById(R.id.edt_password_name);
        TextView tv_valid_start = findViewById(R.id.tv_valid_start);
        TextView tv_valid_until = findViewById(R.id.tv_valid_until);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        Button btn_delete = findViewById(R.id.btn_delete);
        Button btn_reset = findViewById(R.id.btn_reset);

        if (reserve_password_model != null){
            temp_password_name = String.valueOf(reserve_password_model.get_temp_password_name());
            temp_password = String.valueOf(reserve_password_model.get_temp_password());
            valid_start = reserve_password_model.get_valid_start();
            valid_until = reserve_password_model.get_valid_until();
        }

        edt_password_name.setText(temp_password_name);
        edt_reserve_password.setText(temp_password);
        tv_valid_start.setText(formatTimestamp(valid_start));
        tv_valid_until.setText(formatTimestamp(valid_until));

        tv_valid_start.setOnClickListener(v -> {
            pickDateAndTime(timestamp -> {
                valid_start = timestamp;
                tv_valid_start.setText(formatTimestamp(valid_start));
            });
        });

        tv_valid_until.setOnClickListener(v -> {
            pickDateAndTime(timestamp -> {
                valid_until = timestamp;
                tv_valid_until.setText(formatTimestamp(valid_until));
            });
        });

        btn_cancel.setOnClickListener(v -> {
            finish();
        });

        btn_delete.setOnClickListener(v -> {
            deleteTempPassword();
            finish();
        });

        btn_reset.setOnClickListener(v -> {
            temp_password_name = edt_password_name.getText().toString();
            temp_password = edt_reserve_password.getText().toString();
            if(!isValidTimeRange()) {
                Toast.makeText(this, "時間區段不符合規定", Toast.LENGTH_SHORT).show();
            }else if (temp_password_name.isEmpty() || temp_password.isEmpty()) {
                Toast.makeText(this, "名稱及密碼不得為空" , Toast.LENGTH_SHORT).show();
            }else{
                saveReservationPassword();
                finish();
            }
        });
    }

    private void deleteTempPassword() {
        DatabaseReference ref = dbRef.child("locks")
                .child(lockId)
                .child("passwords")
                .child("temp_passwords")
                .child(keyId);

        ref.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                Toast.makeText(this, "取消新增", Toast.LENGTH_SHORT).show();
            } else {
                ref.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "預約密碼刪除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Exception e = task.getException();
                        Toast.makeText(this, "刪除失敗：" + (e != null ? e.getMessage() : "未知錯誤"), Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "刪除失敗", e);
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "讀取失敗：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Firebase", "讀取 snapshot 失敗", e);
        });
    }

    private void saveReservationPassword() {
        Map<String, Object> data = new HashMap<>();
        data.put("temp_password", temp_password);
        data.put("temp_password_name", temp_password_name);
        data.put("valid_start", valid_start);
        data.put("valid_until", valid_until);

        DatabaseReference tempPasswordsRef = dbRef.child("locks")
                .child(lockId)
                .child("passwords")
                .child("temp_passwords");

        DatabaseReference targetRef;
        if ("新增".equals(keyId)) {
            targetRef = tempPasswordsRef.push();
        } else {
            targetRef = tempPasswordsRef.child(keyId);
        }

        targetRef.setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "預約密碼設定成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Exception e = task.getException();
                        Toast.makeText(this, "預約密碼設定失敗：" + (e != null ? e.getMessage() : "未知錯誤"), Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "寫入失敗", e);
                    }
                });
    }


    private boolean isValidTimeRange(){
        return valid_start < valid_until;
    }

    public interface OnDateTimePickedListener {
        void onDateTimePicked(long timestampSeconds);
    }

    private void pickDateAndTime(OnDateTimePickedListener listener){
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                long timestampSeconds = calendar.getTimeInMillis() / 1000L;
                                listener.onDateTimePicked(timestampSeconds); // 回傳 timestamp
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    ).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    public static String formatTimestamp(long timestampInSeconds) {
        Date date = new Date(timestampInSeconds * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日  HH 時 mm 分", Locale.getDefault());
        return sdf.format(date);
    }
}