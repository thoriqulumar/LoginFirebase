package com.example.loginfirebase;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Admin {
    public String username;
    public String email;

    public Admin() {
    }

    public Admin(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
