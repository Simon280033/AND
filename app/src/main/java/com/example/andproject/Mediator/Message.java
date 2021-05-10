package com.example.andproject.Mediator;

public class Message {
    private String message;

    public Message(String message) {
        this.message = message;
    }

    public Message() {}

    public String getBody() {
        return message;
    }

    public void setBody(String name) {
        this.message = name;
    }
}
