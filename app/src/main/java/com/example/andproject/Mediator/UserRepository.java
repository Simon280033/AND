package com.example.andproject.Mediator;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.andproject.Entities.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

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
