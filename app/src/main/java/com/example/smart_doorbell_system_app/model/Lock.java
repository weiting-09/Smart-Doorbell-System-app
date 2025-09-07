package com.example.smart_doorbell_system_app.model;

import java.util.HashMap;
import java.util.Map;

public class Lock {
    public String name;
    public String location;
    public String owner;
    public boolean allow_to_enter;

    public Map<String, String> authorized_users;
    public Map<String, String> passwords;
    public Map<String, String> NFCs;
    public Map<String, String> RFIDs;

    public Map<String, TempPassword> temp_passwords;
    public Map<String, Alert> alerts;
    public Map<String, UnlockLog> unlock_logs;
    public Map<String, VoiceMessage> voice_messages;

    public Lock() {
    }

    public Lock(String name, String owner) {
        this.name = name;
        this.owner = owner;
        this.allow_to_enter = false; // 預設鎖是關的

        this.authorized_users = new HashMap<>();
        this.passwords = new HashMap<>();
        this.NFCs = new HashMap<>();
        this.RFIDs = new HashMap<>();
        this.temp_passwords = new HashMap<>();
        this.alerts = new HashMap<>();
        this.unlock_logs = new HashMap<>();
        this.voice_messages = new HashMap<>();
    }

}
