package com.example.andproject.Model;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.User;
import com.example.andproject.Mediator.UserRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

// This class is the main Model.
// We contain the session data in here, to bind the session together across the different views/viewmodels
public class Model {

    private final Application app;
    private static Model instance;

    // Livedata
    private final UserRepository userRepository;

    // Session data
    // Customer user data (different from default authenticator data)
    private User thisUser;
    private String userLocation;
    private boolean isNewUser;

    // Profile view
    private User viewProfileOf;

    // Fellowship view
    private Fellowship fellowship;
    private User fellowshipPartner;

    // Chat view
    private User chatReceiver;

    // Joinable fellowships
    private HashMap<String, Fellowship> joinableFellowships;

    private Model(Application app) {
        this.app = app;

        userRepository = UserRepository.getInstance(app);
    }

    public void init() {
        String userId = userRepository.getCurrentUserData().getValue().getUid();
    }

    public static synchronized Model getInstance(Application app) {
        if(instance == null)
            instance = new Model(app);
        return instance;
    }

    public void setIsNewUser(boolean isNew) {
        this.isNewUser = isNew;
    }

    public boolean getIsNewUser() {
        return isNewUser;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String location) {
        this.userLocation = location;
    }

    public void setFellowshipPartner(User user) {
        this.fellowshipPartner = user;
    }

    public User getFellowshipPartner() {
        return this.fellowshipPartner;
    }

    public void setThisUser(User user) {
        this.thisUser = user;
    }

    public User getThisUser() {
        return this.thisUser;
    }

    public void setChatReceiver(User user) {
        this.chatReceiver = user;
    }

    public User getChatReceiver() {
        return this.chatReceiver;
    }

    public void incrementCompletionCounterForBothUsers(String ownerId, String partnerId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query ownerQuery = myRef.child("completedCounter").orderByChild("userId").equalTo(ownerId);
        ownerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<String> listItems=new ArrayList<String>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Long currentAmount = ((HashMap<String, Long>) issue.getValue()).get("count");
                        int newAmount = (int) Integer.parseInt("" + currentAmount) + 1;

                        myRef.child("completedCounter").child(ownerId).child("count").setValue(newAmount);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query partnerQuery = myRef.child("completedCounter").orderByChild("userId").equalTo(partnerId);
        partnerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<String> listItems=new ArrayList<String>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Long currentAmount = ((HashMap<String, Long>) issue.getValue()).get("count");
                        int newAmount = (int) Integer.parseInt("" + currentAmount) + 1;

                        myRef.child("completedCounter").child(partnerId).child("count").setValue(newAmount);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Fellowship getFellowshipById(String id) {
        return joinableFellowships.get(id);
    }

    public void setJoinableFellowships(HashMap<String, Fellowship> joinableFellowships) {
        this.joinableFellowships = joinableFellowships;
    }

    public void setViewFellowshipInfo(Fellowship fs) {
        this.fellowship = fs;
    }

    public Fellowship getViewFellowshipInfo() {
        return fellowship;
    }

    public void setViewProfileOf(User user) {
        viewProfileOf = user;
    }

    public User getViewProfileOf() {
        return viewProfileOf;
    }

    public LiveData<FirebaseUser> getCurrentUserData(){
        return userRepository.getCurrentUserData();
    }

    public void signOut() {
        userRepository.signOut();
    }
}
