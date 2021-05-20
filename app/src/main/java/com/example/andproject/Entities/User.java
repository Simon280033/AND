package com.example.andproject.Entities;

// This class is a POJO that makes it possible to get/set a customized User to/from the Realtime database.
// This differs from the UserLiveData, in that it doesn't have anything to do with the authenticator,
// only the customzied data for the user in the Realtime database
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
