package com.example.andproject.Mediator;

import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.Message;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageRepository {
    private static MessageRepository instance;
    private DatabaseReference myRef;
    private MessageLiveData message;

    private MessageRepository(){}

    public static synchronized MessageRepository getInstance() {
        if(instance == null)
            instance = new MessageRepository();
        return instance;
    }

    public void init(String userId) {
        myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("messages").child(userId);
        message = new MessageLiveData(myRef);
    }

    public void saveMessage(String message) {
System.out.println("test3");
        myRef.setValue(new Message(message));
    }

    public LiveData<Message> getMessage() {
        return message;
    }
}
