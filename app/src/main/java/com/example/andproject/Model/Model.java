package com.example.andproject.Model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.Message;
import com.example.andproject.Entities.User;
import com.example.andproject.Mediator.MessageRepository;
import com.example.andproject.Mediator.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class Model {

    private final Application app;
    private static Model instance;

    // Livedata
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // Session data
    private User viewProfileOf;

    private Model(Application app) {
        this.app = app;

        userRepository = UserRepository.getInstance(app);
        messageRepository = MessageRepository.getInstance();
    }

    public void init() {
        String userId = userRepository.getCurrentUserData().getValue().getUid();
        messageRepository.init(userId);
    }

    public static synchronized Model getInstance(Application app) {
        if(instance == null)
            instance = new Model(app);
        return instance;
    }

    public void setViewProfileOf(User user) {
        viewProfileOf = user;
    }

    public User getViewProfileOf() {
        return viewProfileOf;
    }

    public LiveData<FirebaseUser> getCurrentUserData(){
        return userRepository.getCurrentUserData();
    }

    public void saveMessage(String message) {
        messageRepository.saveMessage(message);
    }

    public LiveData<Message> getMessage() {
        return messageRepository.getMessage();
    }

    public void signOut() {
        userRepository.signOut();
    }
}
