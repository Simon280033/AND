package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.google.firebase.auth.FirebaseUser;

public class FellowshipRequestViewModel extends AndroidViewModel {
    private final Model model;

    public FellowshipRequestViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public Fellowship getViewFellowshipInfo() {
        return model.getViewFellowshipInfo();
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

    public void signOut() {
        model.signOut();
    }
}
