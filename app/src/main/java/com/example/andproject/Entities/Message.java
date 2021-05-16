package com.example.andproject.Entities;

import android.text.format.DateFormat;

import java.util.Date;

public class Message {

    public String fellowshipId;
    public String senderId;
    public String senderName;
    public String senderImageUrl;
    public String receiverId;
    public String receiverName;
    public String messageText;
    public long messageTime;

    public Message(String fellowshipId, String senderId, String senderName, String senderImageUrl, String receiverId, String receiverName, String messageText) {
        this.fellowshipId = fellowshipId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderImageUrl = senderImageUrl;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.messageText = messageText;

        this.messageTime = new Date().getTime();
    }

    public void setMessageTime(long time) {
        this.messageTime = time;
    }

    public String getTime() {
        return DateFormat.format("dd-MM-yyyy (HH:mm:ss)", this.messageTime).toString();
    }
}
