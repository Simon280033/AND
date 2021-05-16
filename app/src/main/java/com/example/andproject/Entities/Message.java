package com.example.andproject.Entities;

import java.util.Date;

public class Message {

    public String fellowshipId;
    public String senderId;
    public String senderName;
    public String receiverId;
    public String receiverName;
    public String messageText;
    public long messageTime;

    public Message(String fellowshipId, String senderId, String senderName, String receiverId, String receiverName, String messageText) {
        this.fellowshipId = fellowshipId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.messageText = messageText;

        this.messageTime = new Date().getTime();
    }
}
