package com.example.smart_doorbell_system_app.model;

public class UnlockLog {
    public String method;
    public String status;
    public Object time;
    public String user;

    public UnlockLog() {}

    // 方便建立物件用
    public UnlockLog(String method, String status, Object time, String user) {
        this.method = method;
        this.status = status;
        this.time = time;
        this.user = user;
    }
}
