package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Mediator.UserRepository;
import com.example.andproject.Model.Model;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends AndroidViewModel {
    private final Model model;

    public ProfileViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public boolean isOwnProfile() {
        if (model.getViewProfileOf().id.equals(model.getCurrentUserData().getValue().getUid())) {
            return true;
        } else {
            return false;
        }
    }

    public LiveData<FirebaseUser> getCurrentUserData(){
        return model.getCurrentUserData();
    }
}
