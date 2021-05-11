package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Model.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class ProfileEditorViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public ProfileEditorViewModel(Application app){
        super(app);
        userRepository = UserRepository.getInstance(app);
    }

    public LiveData<FirebaseUser> getCurrentUser(){
        return userRepository.getCurrentUser();
    }
}
