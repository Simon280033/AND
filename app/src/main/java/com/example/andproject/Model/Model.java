package com.example.andproject.Model;

import android.app.Application;
import android.util.Pair;

import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.Message;
import com.example.andproject.Entities.User;
import com.example.andproject.Mediator.MessageRepository;
import com.example.andproject.Mediator.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class Model {

    private final Application app;
    private static Model instance;

    // Livedata
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // Session data
    // Profile view
    private User viewProfileOf;

    // Fellowship view
    private Fellowship fellowship;

    // Joinable fellowships
    private HashMap<String, Fellowship> joinableFellowships;

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

    public Fellowship getFellowshipById(String id) {
        return joinableFellowships.get(id);
    }

    public void setJoinableFellowships(HashMap<String, Fellowship> joinableFellowships) {
        this.joinableFellowships = joinableFellowships;
    }

    public void setViewFellowshipInfo(Fellowship fs) {
        this.fellowship = fs;
    }

    public Fellowship getViewFellowshipInfo() {
        return fellowship;
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
