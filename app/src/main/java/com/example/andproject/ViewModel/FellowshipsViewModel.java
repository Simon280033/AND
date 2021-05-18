package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.andproject.Entities.Fellowship;
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

public class FellowshipsViewModel extends AndroidViewModel {
    private final Model model;

    // Bindable attributes to be shown in UI
    private MutableLiveData<ArrayList<Fellowship>> yourFellowshipsList;
    private MutableLiveData<ArrayList<Fellowship>> joinedFellowshipsList;

    // Lists holding the full data of the fellowships
    private ArrayList<Fellowship> myFellowships, joinedFellowships;

    public FellowshipsViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public Fellowship getOwnFellowshipAt(int index) {
        return myFellowships.get(index);
    }

    public Fellowship getJoinedFellowshipAt(int index) {
        return joinedFellowships.get(index);
    }

    public MutableLiveData<ArrayList<Fellowship>> getYourFellowshipsList() {
        if (yourFellowshipsList == null) {
            yourFellowshipsList = new MutableLiveData<ArrayList<Fellowship>>();
            myFellowships = new ArrayList<>();
        }
        return yourFellowshipsList;
    }

    public MutableLiveData<ArrayList<Fellowship>> getJoinedFellowshipsList() {
        if (joinedFellowshipsList == null) {
            joinedFellowshipsList = new MutableLiveData<ArrayList<Fellowship>>();
            joinedFellowships = new ArrayList<>();
        }
        return joinedFellowshipsList;
    }

    public void refreshLists() {
        refreshYourFellowships();
        refreshJoinedFellowships();
    }

    private void refreshYourFellowships() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("fellowships").orderByChild("creatorId").equalTo(model.getCurrentUserData().getValue().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<Fellowship> listItems=new ArrayList<Fellowship>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("id");
                        String ownerId = ((HashMap<String, String>) issue.getValue()).get("creatorId");
                        String webShop = ((HashMap<String, String>) issue.getValue()).get("webshop");
                        String category = ((HashMap<String, String>) issue.getValue()).get("category");
                        Long amountNeeded = ((HashMap<String, Long>) issue.getValue()).get("amountNeeded");
                        String paymentMethod = ((HashMap<String, String>) issue.getValue()).get("paymentMethod");
                        String deadline = ((HashMap<String, String>) issue.getValue()).get("deadline");
                        String pickupCoordinates = ((HashMap<String, String>) issue.getValue()).get("pickupCoordinates");
                        String partnerId = ((HashMap<String, String>) issue.getValue()).get("partnerId");
                        Long partnerPaid = ((HashMap<String, Long>) issue.getValue()).get("partnerPaid");
                        Long paymentApproved = ((HashMap<String, Long>) issue.getValue()).get("paymentApproved");
                        String receiptUrl = ((HashMap<String, String>) issue.getValue()).get("receiptUrl");
                        Long ownerCompleted = ((HashMap<String, Long>) issue.getValue()).get("ownerCompleted");
                        Long partnerCompleted = ((HashMap<String, Long>) issue.getValue()).get("partnerCompleted");
                        Long isCompleted = ((HashMap<String, Long>) issue.getValue()).get("isCompleted");

                        String completionStatus = "NO";
                        if (isCompleted > 0) {
                            completionStatus = "YES";
                        }

                        Fellowship fs = new Fellowship(fellowshipId, ownerId, webShop, category, (int) Integer.parseInt("" + amountNeeded), paymentMethod, deadline, pickupCoordinates, partnerId, (int) Integer.parseInt("" + partnerPaid), (int) Integer.parseInt("" + paymentApproved), receiptUrl, (int) Integer.parseInt("" + ownerCompleted), (int) Integer.parseInt("" + partnerCompleted), (int) Integer.parseInt("" + isCompleted));
                        myFellowships.add(fs);
                        listItems.add(fs);

                    }
                    yourFellowshipsList.setValue(listItems);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshJoinedFellowships() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("fellowships").orderByChild("partnerId").equalTo(model.getCurrentUserData().getValue().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<Fellowship> listItems=new ArrayList<Fellowship>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("id");
                        String ownerId = ((HashMap<String, String>) issue.getValue()).get("creatorId");
                        String webShop = ((HashMap<String, String>) issue.getValue()).get("webshop");
                        String category = ((HashMap<String, String>) issue.getValue()).get("category");
                        Long amountNeeded = ((HashMap<String, Long>) issue.getValue()).get("amountNeeded");
                        String paymentMethod = ((HashMap<String, String>) issue.getValue()).get("paymentMethod");
                        String deadline = ((HashMap<String, String>) issue.getValue()).get("deadline");
                        String pickupCoordinates = ((HashMap<String, String>) issue.getValue()).get("pickupCoordinates");
                        String partnerId = ((HashMap<String, String>) issue.getValue()).get("partnerId");
                        Long partnerPaid = ((HashMap<String, Long>) issue.getValue()).get("partnerPaid");
                        Long paymentApproved = ((HashMap<String, Long>) issue.getValue()).get("paymentApproved");
                        String receiptUrl = ((HashMap<String, String>) issue.getValue()).get("receiptUrl");
                        Long ownerCompleted = ((HashMap<String, Long>) issue.getValue()).get("ownerCompleted");
                        Long partnerCompleted = ((HashMap<String, Long>) issue.getValue()).get("partnerCompleted");
                        Long isCompleted = ((HashMap<String, Long>) issue.getValue()).get("isCompleted");

                        String completionStatus = "NO";
                        if (isCompleted > 0) {
                            completionStatus = "YES";
                        }
                        Fellowship fs = new Fellowship(fellowshipId, ownerId, webShop, category, (int) Integer.parseInt("" + amountNeeded), paymentMethod, deadline, pickupCoordinates, partnerId, (int) Integer.parseInt("" + partnerPaid), (int) Integer.parseInt("" + paymentApproved), receiptUrl, (int) Integer.parseInt("" + ownerCompleted), (int) Integer.parseInt("" + partnerCompleted), (int) Integer.parseInt("" + isCompleted));
                        joinedFellowships.add(fs);
                        listItems.add(fs);
                    }
                    joinedFellowshipsList.setValue(listItems);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setViewFellowshipInfo(Fellowship fs) {
        model.setViewFellowshipInfo(fs);
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
