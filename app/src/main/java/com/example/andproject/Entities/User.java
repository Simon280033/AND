package com.example.andproject.Entities;

public class User {

    private String id;
    private String displayName;
    private String imageUrl;
    private String email;

    public User(String id, String displayName, String imageUrl, String email) {
        this.id = id;
        this.displayName = displayName;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
