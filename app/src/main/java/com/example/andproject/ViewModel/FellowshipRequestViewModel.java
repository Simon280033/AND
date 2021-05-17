package com.example.andproject.ViewModel;

import android.app.Application;
import android.widget.ArrayAdapter;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.example.andproject.View.FellowshipRequestsActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FellowshipRequestViewModel extends AndroidViewModel {
    private final Model model;

    private MutableLiveData<ArrayList<String>> requestsOverviewList;

    // Lists holding the full data of the fellowships
    private ArrayList<Fellowship> fellowshipList;

    private ArrayList<String> userIds;
    private HashMap<String, User> users;
    private HashMap<String, String> requestIdByUserId;

    public FellowshipRequestViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public String getUserId(int index) {
        return userIds.get(index);
    }

    public MutableLiveData<ArrayList<String>> getRequestsOverviewList() {
        if (requestsOverviewList == null) {
            requestsOverviewList = new MutableLiveData<ArrayList<String>>();
            fellowshipList = new ArrayList<>();
        }
        return requestsOverviewList;
    }

    public void refreshList() {
        getUsernamesByIds();
    }

    private void getUsernamesByIds() {
        userIds = new ArrayList<>();
        users = new HashMap<>();
        requestIdByUserId = new HashMap<>();

        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("users");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String userId = ((HashMap<String, String>) issue.getValue()).get("id");
                        String displayName = ((HashMap<String, String>) issue.getValue()).get("displayName");
                        String imageUrl = ((HashMap<String, String>) issue.getValue()).get("imageUrl");
                        String email = ((HashMap<String, String>) issue.getValue()).get("email");

                        users.put(userId, new User(userId, displayName, imageUrl, email));
                    }
                    // After we have gotten the list of ids/names, we get the requests for the fellowship
                    getRequestsForFellowship();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getRequestsForFellowship() {
        System.out.println("læs: getting");
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("fellowshipRequests").orderByChild("fellowshipId").equalTo(model.getViewFellowshipInfo().id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<String> listItems=new ArrayList<String>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String requestId = ((HashMap<String, String>) issue.getValue()).get("requestId");
                        String requesterId = ((HashMap<String, String>) issue.getValue()).get("requesterId");
                        String requesterName = users.get(requesterId).displayName;

                        listItems.add(requesterName);
                        userIds.add(requesterId);
                        requestIdByUserId.put(requesterId, requestId);
                    }

                    getRequestsOverviewList().setValue(listItems);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void acceptRequestFromUser(User user) {
        // We mark the request as accepted
        String requestId = requestIdByUserId.get(user.id);

        model.getViewFellowshipInfo().partnerId = user.id;

        // We set it in the database
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        myRef.child("fellowshipRequests").child(requestId).child("isAccepted").setValue(1);

        // We set the partner ID in the Fellowship table
        myRef.child("fellowships").child(model.getViewFellowshipInfo().id).child("partnerId").setValue(user.id);
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
