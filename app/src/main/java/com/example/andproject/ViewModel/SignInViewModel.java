package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.User;
import com.example.andproject.Mediator.UserRepository;
import com.example.andproject.Model.Model;
import com.google.firebase.auth.FirebaseUser;

public class SignInViewModel extends AndroidViewModel {
    private final Model model;

    public SignInViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public void setViewProfileOf(User user) {
        model.setViewProfileOf(user);
    }

    public User getViewProfileOf() {
        return model.getViewProfileOf();
    }

    public LiveData<FirebaseUser> getCurrentUser(){
        return model.getCurrentUserData();
    }
}
