package com.example.andproject.ViewModel;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Mediator.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class ProfileEditorViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public ProfileEditorViewModel(Application app) {
        super(app);
        userRepository = UserRepository.getInstance(app);
    }

    public LiveData<FirebaseUser> getCurrentUserData() {
        return userRepository.getCurrentUserData();
    }

    public void updateUser(String displayName, Uri avatarUri) {
        userRepository.updateCurrentUser(displayName, avatarUri);
    }

    }