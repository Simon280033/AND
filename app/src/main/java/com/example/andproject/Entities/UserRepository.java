package com.example.andproject.Entities;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;

// This class enables us to get info about the user which is currently logged in through the authenticator
// It differs from the User class, as the User class is concerned with the customized data for the user in the Database,
// while this one is only relevant for the authentication
public class UserRepository {
    private final UserLiveData currentUserData;
    private final Application app;
    private static UserRepository instance;

    // User whose profile is to be viewed
    private MutableLiveData<User> viewUser;

    private UserRepository(Application app) {
        this.app = app;
        currentUserData = new UserLiveData();
    }

    public static synchronized UserRepository getInstance(Application app) {
        if(instance == null)
            instance = new UserRepository(app);
        return instance;
    }

    public LiveData<FirebaseUser> getCurrentUserData() {
        return currentUserData;
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(app.getApplicationContext());
    }
}
