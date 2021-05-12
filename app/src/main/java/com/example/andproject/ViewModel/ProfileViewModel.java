package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Mediator.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public ProfileViewModel(Application app){
        super(app);
        userRepository = UserRepository.getInstance(app);
    }

    public LiveData<FirebaseUser> getCurrentUser(){
        return userRepository.getCurrentUserData();
    }
}
