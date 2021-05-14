package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.Message;
import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.google.firebase.auth.FirebaseUser;

public class FellowshipsViewModel extends AndroidViewModel {
    private final Model model;

    public FellowshipsViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public void setViewFellowshipInfo(Fellowship fs) {
        model.setViewFellowshipInfo(fs);
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
