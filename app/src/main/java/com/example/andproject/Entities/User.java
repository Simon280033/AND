package com.example.andproject.Entities;

public class User {

    public String id;
    public String displayName;
    public String imageUrl;
    public String email;

    public User(String id, String displayName, String imageUrl, String email) {
        this.id = id;
        this.displayName = displayName;
        this.imageUrl = imageUrl;
        this.email = email;
    }
}
