package com.example.smart_doorbell_system_app.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String username;
    public String email;
    public Map<String, Boolean> locks;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.locks = new HashMap<>();
    }
}
