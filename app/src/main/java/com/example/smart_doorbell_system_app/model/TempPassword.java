package com.example.smart_doorbell_system_app.model;

public class TempPassword {
    public String password;
    public String valid_start;
    public String valid_until;

    public TempPassword() {}

    public TempPassword(String password, String valid_start, String valid_until){
        this.password = password;
        this.valid_start = valid_start;
        this.valid_until = valid_until;
    }
}
