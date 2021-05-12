package com.example.andproject.Mediator;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserRepository {
    private final UserLiveData currentUserData;
    private final Application app;
    private static UserRepository instance;

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

    public void updateUser(String displayName, Uri avatarUri) {
        FirebaseUser user = currentUserData.getValue();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(avatarUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }
}
