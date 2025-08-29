package com.example.smart_doorbell_system_app.adapter;

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

import java.util.List;

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
        holder.tvMethod.setText("方式：" + log.method);
        holder.tvStatus.setText("結果：" + log.status);
        holder.tvTime.setText("時間：" + log.time);
        // 預設先顯示 uid，避免閃爍
        holder.tvUser.setText("使用者：" + log.user);

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
                    holder.tvUser.setText("使用者：" + username);
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
        TextView tvMethod, tvStatus, tvTime, tvUser;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMethod = itemView.findViewById(R.id.tvMethod);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUser = itemView.findViewById(R.id.tvUser);
        }
    }
}

