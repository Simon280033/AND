package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.google.firebase.auth.FirebaseUser;

public class FellowshipViewModel extends AndroidViewModel {
    private final Model model;

    public FellowshipViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public void incrementCompletionCounterForBothUsers(String ownerId, String partnerId) {
        model.incrementCompletionCounterForBothUsers(ownerId, partnerId);
    }

    public void setChatReceiver(User user) {
        model.setChatReceiver(user);
    }

    public Fellowship getViewFellowshipInfo() {
        return model.getViewFellowshipInfo();
    }

    public User getViewProfileOf() {
        return model.getViewProfileOf();
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
