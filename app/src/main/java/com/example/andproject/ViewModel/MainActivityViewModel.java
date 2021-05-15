package com.example.andproject.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.Message;
import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.example.andproject.View.FellowshipsActivity;
import com.google.firebase.auth.FirebaseUser;

import static androidx.core.content.ContextCompat.startActivity;

public class MainActivityViewModel extends AndroidViewModel {
    private final Model model;

    public MainActivityViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public void setViewProfileOf(User user) {
        model.setViewProfileOf(user);
    }

    public void init() {
        model.init();
    }

    public LiveData<FirebaseUser> getCurrentUser(){
        return model.getCurrentUserData();
    }

    public void saveMessage(String message) {
        model.saveMessage(message);
    }

    public LiveData<Message> getMessage() {
        return model.getMessage();
    }

    public void signOut() {
        model.signOut();
    }
}
