package com.example.smart_doorbell_system_app.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class RFID implements Serializable {
    private long id;
    private String name;
    public RFID() {
        this.id = 0;
        this.name = "";
    }

    @PropertyName("name")
    public String getName(){
        return name;
    }

    @PropertyName("id")
    public long getId(){
        return id;
    }

    @PropertyName("name")
    public void setName(String newName){
        name = newName;
    }

}
