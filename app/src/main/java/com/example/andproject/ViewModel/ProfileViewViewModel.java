package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.Message;
import com.example.andproject.Entities.ProfileComment;
import com.example.andproject.Entities.Report;
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
    private MutableLiveData<ArrayList<ProfileComment>> profileCommentList;

    // Lists holding the full data of the fellowships
    private ArrayList<ProfileComment> profileComments;

    public ProfileViewViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public void refreshUserDetails() {
        updateDisplayNameAndAvatarUrl();
        updateShipsCounter();
        refreshProfileComments();
    }

    private void refreshProfileComments() {
        System.out.println("læs: refreshing comments");
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("profileComments").orderByChild("receiverId").equalTo(model.getViewProfileOf().id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("læs: got here");
                if (dataSnapshot.exists()) {
                    System.out.println("læs: got here2");
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<ProfileComment> listItems=new ArrayList<ProfileComment>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String messageText = ((HashMap<String, String>) issue.getValue()).get("messageText");
                        String receiverId = ((HashMap<String, String>) issue.getValue()).get("receiverId");
                        String senderId = ((HashMap<String, String>) issue.getValue()).get("senderId");
                        String senderName = ((HashMap<String, String>) issue.getValue()).get("senderName");
                        String senderImageUrl = ((HashMap<String, String>) issue.getValue()).get("senderImageUrl");
                        Long messageTime = ((HashMap<String, Long>) issue.getValue()).get("messageTime");

                        ProfileComment pc = new ProfileComment(senderId, senderName, senderImageUrl, receiverId, messageText, messageTime);

                        profileComments.add(pc);
                        listItems.add(pc);
                    }
                    System.out.println("læs: " + listItems.size());
                    profileCommentList.setValue(listItems);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public MutableLiveData<ArrayList<ProfileComment>> getProfileCommentList() {
        if (profileCommentList == null) {
            profileCommentList = new MutableLiveData<ArrayList<ProfileComment>>();
            profileComments = new ArrayList<>();
        }
        return profileCommentList;
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

    public void report(String message) {
        Report report = new Report(model.getViewProfileOf().id, model.getCurrentUserData().getValue().getUid(), message);
        FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference().child("reports").child(model.getViewProfileOf().id)
                .push()
                .setValue(report);
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
