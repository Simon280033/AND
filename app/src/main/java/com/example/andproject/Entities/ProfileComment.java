package com.example.andproject.Entities;

import android.text.format.DateFormat;

import java.util.Date;

public class ProfileComment {

    public String senderId;
    public String senderName;
    public String senderImageUrl;
    public String receiverId;
    public String messageText;
    public long messageTime;

    public ProfileComment(String senderId, String senderName, String senderImageUrl, String receiverId, String messageText) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderImageUrl = senderImageUrl;
        this.receiverId = receiverId;
        this.messageText = messageText;

        this.messageTime = new Date().getTime();
    }

    public ProfileComment(String senderId, String senderName, String senderImageUrl, String receiverId,String messageText, long messageTime) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderImageUrl = senderImageUrl;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.messageTime = messageTime;
    }

    public void setMessageTime(long time) {
        this.messageTime = time;
    }

    public String getTime() {
        return DateFormat.format("dd-MM-yyyy (HH:mm:ss)", this.messageTime).toString();
    }
}
