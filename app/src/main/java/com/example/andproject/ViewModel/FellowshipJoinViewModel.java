package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.FellowshipRequest;
import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class FellowshipJoinViewModel extends AndroidViewModel {
    private final Model model;

    // Bindable attributes to be shown in UI
    private MutableLiveData<String> ownerName;
    private MutableLiveData<String> avatarUrl;
    private MutableLiveData<String> webshop;
    private MutableLiveData<String> amountNeeded;
    private MutableLiveData<String> paymentMethod;
    private MutableLiveData<String> deadline;
    private MutableLiveData<String> distance;

    public FellowshipJoinViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public MutableLiveData<String> getOwnerName() {
        if (ownerName == null) {
            ownerName = new MutableLiveData<String>();
        }
        return ownerName;
    }

    public MutableLiveData<String> getAvatarUrl() {
        if (avatarUrl == null) {
            avatarUrl = new MutableLiveData<String>();
        }
        return avatarUrl;
    }

    public MutableLiveData<String> getWebshop() {
        if (webshop == null) {
            webshop = new MutableLiveData<String>();
        }
        return webshop;
    }

    public MutableLiveData<String> getAmountNeeded() {
        if (amountNeeded == null) {
            amountNeeded = new MutableLiveData<String>();
        }
        return amountNeeded;
    }

    public MutableLiveData<String> getPaymentMethod() {
        if (paymentMethod == null) {
            paymentMethod = new MutableLiveData<String>();
        }
        return paymentMethod;
    }

    public MutableLiveData<String> getDeadline() {
        if (deadline == null) {
            deadline = new MutableLiveData<String>();
        }
        return deadline;
    }

    public MutableLiveData<String> getDistancee() {
        if (distance == null) {
            distance = new MutableLiveData<String>();
        }
        return distance;
    }

    public void refreshOwnerDetails()  {
        // We get the owner of the fellowship
        String ownerId = model.getViewFellowshipInfo().creatorId;
        // We get their displayName and avatar URL
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("users").orderByChild("id").equalTo(ownerId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String displayName = ((HashMap<String, String>) issue.getValue()).get("displayName");
                        String imageUrl = ((HashMap<String, String>) issue.getValue()).get("imageUrl");

                        ownerName.setValue(displayName);
                        avatarUrl.setValue(imageUrl);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshFellowshipDetails() {
        // We get the fellowship details
        Fellowship fs = model.getFellowshipById(model.getViewFellowshipInfo().id);

        webshop.setValue(fs.webshop);
        amountNeeded.setValue(fs.amountNeeded + " DKK");
        paymentMethod.setValue(fs.paymentMethod);
        deadline.setValue(fs.deadline);
        // FIND A WAY TO CALCULATE DISTANCE BETWEEN TWO LATLONG POINTS!!!
        distance.setValue("X Meters");
    }

    public void refreshDetails() {
        refreshOwnerDetails();
        refreshFellowshipDetails();
    }

    public void requestJoin() {
        // We create the Fellowship request object
        String requestId = UUID.randomUUID().toString(); // We create a random ID;
        String fellowshipId = model.getViewFellowshipInfo().id;
        String requesterId = model.getCurrentUserData().getValue().getUid();
        String requestDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        int isAccepted = 0;

        FellowshipRequest fsr =  new FellowshipRequest(requestId, fellowshipId, requesterId, requestDate, isAccepted);
        // We save it to the database
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowshipRequests").child(requestId);

        myRef.setValue(fsr);
    }

    public Fellowship getViewFellowshipInfo() {
        return model.getViewFellowshipInfo();
    }

    public Fellowship getFellowshipById(String id) {
        return model.getFellowshipById(id);
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
