package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Mediator.Message;
import com.example.andproject.Mediator.MessageRepository;
import com.example.andproject.Model.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public MainActivityViewModel(Application app){
        super(app);
        userRepository = UserRepository.getInstance(app);
        messageRepository = MessageRepository.getInstance();
    }

    public void init() {
        String userId = userRepository.getCurrentUserData().getValue().getUid();
        messageRepository.init(userId);
    }

    public LiveData<FirebaseUser> getCurrentUser(){
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
