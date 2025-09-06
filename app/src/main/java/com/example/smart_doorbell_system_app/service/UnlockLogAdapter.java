package com.example.smart_doorbell_system_app.service;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_doorbell_system_app.R;
import com.example.smart_doorbell_system_app.model.UnlockLog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class UnlockLogAdapter extends RecyclerView.Adapter<UnlockLogAdapter.LogViewHolder> {

    private final List<UnlockLog> logList;

    public UnlockLogAdapter(List<UnlockLog> logList) {
        this.logList = logList;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unlock_log, parent, false);
        return new LogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        UnlockLog log = logList.get(position);
        holder.methodText.setText("方式：" + log.method);
        holder.statusText.setText("結果：" + log.status);

        // holder.timeText.setText("時間：" + log.time);
        // 轉換時間
        Date date = new Date((Long)log.time);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault()); // 使用裝置當前時區
        String timeString = sdf.format(date);
        holder.timeText.setText("時間：" + timeString);

        // 預設先顯示 uid，避免閃爍
        holder.userText.setText("使用者：" + log.user);

        // 到 Firebase 查 username
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(log.user)   // log.user 是 uid
                .child("username");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.getValue(String.class);
                    holder.userText.setText("使用者：" + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 發生錯誤就維持顯示 uid
            }
        });

    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView methodText, statusText, timeText, userText;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            methodText = itemView.findViewById(R.id.txt_Method);
            statusText = itemView.findViewById(R.id.txt_Status);
            timeText = itemView.findViewById(R.id.txt_Time);
            userText = itemView.findViewById(R.id.txt_User);
        }
    }
}

