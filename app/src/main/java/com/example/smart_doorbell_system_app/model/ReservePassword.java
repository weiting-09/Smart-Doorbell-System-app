package com.example.smart_doorbell_system_app.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class ReservePassword implements Serializable {
    private String temp_password_name;
    private String temp_password;
    private long valid_start;
    private long valid_until;

    public ReservePassword() {
        this.temp_password_name = "";
        this.temp_password = "";
        this.valid_start = 0;
        this.valid_until = 0;
    }

    public ReservePassword(String temp_password_name, String temp_password, long valid_start, long valid_until) {
        this.temp_password_name = temp_password_name;
        this.temp_password = temp_password;
        this.valid_start = valid_start;
        this.valid_until = valid_until;
    }

    @PropertyName("temp_password_name")
    public String get_temp_password_name(){
        return temp_password_name;
    }

    @PropertyName("temp_password")
    public String get_temp_password(){
        return temp_password;
    }

    @PropertyName("valid_start")
    public long get_valid_start(){
        return valid_start;
    }

    @PropertyName("valid_until")
    public long get_valid_until(){
        return valid_until;
    }
}
