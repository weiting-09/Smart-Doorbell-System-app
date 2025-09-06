package com.example.smart_doorbell_system_app;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_doorbell_system_app.adapter.UnlockLogAdapter;
import com.example.smart_doorbell_system_app.model.UnlockLog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Unlock_Log extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UnlockLogAdapter adapter;
    private List<UnlockLog> logList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unlock_log);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewLogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UnlockLogAdapter(logList);
        recyclerView.setAdapter(adapter);

        // 從 Intent 拿到 lockId
        String lockId = getIntent().getStringExtra(Constants.LOCK_ID);

        if (lockId != null) {
            loadLogs(lockId);
        }

    }

    private void loadLogs(String lockId) {
        DatabaseReference logsRef = FirebaseDatabase.getInstance()
                .getReference("locks")
                .child(lockId)
                .child("unlock_logs");

        logsRef.orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logList.clear();
                for (DataSnapshot logSnap : snapshot.getChildren()) {
                    UnlockLog log = logSnap.getValue(UnlockLog.class);
                    if (log != null) {
                        logList.add(log);
                    }
                }
                Collections.reverse(logList);  // 反轉，讓最新在最上方
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("UnlockLogActivity", "Failed to read unlock logs", error.toException());
            }
        });
    }

}