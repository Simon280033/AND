package com.example.andproject.Entities;

import android.text.format.DateFormat;

import java.util.Date;

public class ProfileComment {

    private String senderId;
    private String senderName;
    private String senderImageUrl;
    private String receiverId;
    private String messageText;
    private long messageTime;

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

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImageUrl() {
        return senderImageUrl;
    }

    public void setSenderImageUrl(String senderImageUrl) {
        this.senderImageUrl = senderImageUrl;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long time) {
        this.messageTime = time;
    }

    public String getTime() {
        return DateFormat.format("dd-MM-yyyy (HH:mm:ss)", this.messageTime).toString();
    }
}
