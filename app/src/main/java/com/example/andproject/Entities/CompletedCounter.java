package com.example.andproject.Entities;

// This class is a POJO that makes it possible to get a user's completed FellowShips counter from the Realtime database
public class CompletedCounter {
    private String userId;
    private int count;

    public CompletedCounter(String userId, int count) {
        this.userId = userId;
        this.count = count;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
