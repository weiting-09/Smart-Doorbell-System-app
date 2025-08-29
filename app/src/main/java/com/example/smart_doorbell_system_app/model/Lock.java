package com.example.smart_doorbell_system_app.model;

import java.util.Map;

public class Lock {
    public String name;
    public String location;
    public String owner;
    public boolean isOpened;

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

}
