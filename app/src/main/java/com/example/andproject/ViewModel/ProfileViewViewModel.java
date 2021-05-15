package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileViewViewModel extends AndroidViewModel {
    private final Model model;

    // Bindable attributes to be shown in UI
    private MutableLiveData<String> displayName;
    private MutableLiveData<String> avatarUrl;
    private MutableLiveData<String> shipsCounter;

    public ProfileViewViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public void refreshUserDetails() {
        updateDisplayNameAndAvatarUrl();
        updateShipsCounter();
    }

    public MutableLiveData<String> getDisplayName() {
        if (displayName == null) {
            displayName = new MutableLiveData<String>();
        }
        return displayName;
    }

    public MutableLiveData<String> getAvatarUrl() {
        if (avatarUrl == null) {
            avatarUrl = new MutableLiveData<String>();
        }
        return avatarUrl;
    }

    public void updateDisplayNameAndAvatarUrl()  {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query ownerQuery = myRef.child("users").orderByChild("id").equalTo(model.getViewProfileOf().id);
        ownerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String dn = ((HashMap<String, String>) issue.getValue()).get("displayName");
                        String iu = ((HashMap<String, String>) issue.getValue()).get("imageUrl");

                        displayName.setValue(dn);
                        avatarUrl.setValue(iu);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public MutableLiveData<String> getShipsCounter() {
        if (shipsCounter == null) {
            shipsCounter = new MutableLiveData<String>();
        }
        return shipsCounter;
    }

    public void updateShipsCounter()  {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query ownerQuery = myRef.child("completedCounter").orderByChild("userId").equalTo(model.getViewProfileOf().id);
        ownerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Long currentAmount = ((HashMap<String, Long>) issue.getValue()).get("count");
                        int newAmount = (int) Integer.parseInt("" + currentAmount);

                        shipsCounter.setValue("" + newAmount);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
