package com.example.andproject.ViewModel;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Pair;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FindFellowshipsViewModel extends AndroidViewModel {
    private final Model model;

    // Bindable attributes to be shown in UI
    private MutableLiveData<ArrayList<Pair<Fellowship, String>>> fellowshipsList;

    // Lists holding the full data of the fellowships
    private ArrayList<String> pendingsRequestsFellowships;
    private ArrayList<Fellowship> fellowshipsDetails;

    // Users location, to calculate distance to pickups
    private String currentLocation;

    public FindFellowshipsViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    // YOU HAVE MANUALLY ENABLED LOCATIONS FOR APP!!!! FIND A WAY TO PROMPT USER TO ENABLE IT
    public void getUsersLocation(Context context) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        String latAndLong = null;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            currentLocation = location.getLatitude() + ", " + location.getLongitude();
                            System.out.println("læs: location: " + currentLocation);
                        }
                    }
                });
    }

    public Fellowship getFellowshipAt(int index) {
        return fellowshipsDetails.get(index);
    }

    public MutableLiveData<ArrayList<Pair<Fellowship, String>>> getFellowshipsList() {
        if (fellowshipsList == null) {
            fellowshipsList = new MutableLiveData<ArrayList<Pair<Fellowship, String>>>();
            pendingsRequestsFellowships = new ArrayList<>();
            fellowshipsDetails = new ArrayList<>();
        }
        return fellowshipsList;
    }

    public Fellowship getFellowshipAtPosition(int index) {
        return fellowshipsDetails.get(index);
    }

    public void refreshFellowships() {
        // Running the filter method automatically runs the refresher method. This method is to avoid confusion on what to do on the view
        refreshPendingRequestedFellowships();
    }

    private void refreshPendingRequestedFellowships() {
        pendingsRequestsFellowships.clear();

        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("fellowshipRequests").orderByChild("requesterId").equalTo(model.getCurrentUserData().getValue().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        if (((HashMap<String, String>) issue.getValue()).get("requesterId").equals(model.getCurrentUserData().getValue().getUid())) {
                            String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("fellowshipId");
                            pendingsRequestsFellowships.add(fellowshipId);
                        }
                    }
                }
                // After we have gotten the list of Fellowships we have already applied for, we get those we haven't
                refreshFellowshipsWithCriterias();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshFellowshipsWithCriterias() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("fellowships");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<Pair<Fellowship, String>> listItems=new ArrayList<Pair<Fellowship, String>>();
                    // We make a hashmap of joinable fellowships for other activities
                    HashMap<String, Fellowship> joinableFellowships = new HashMap<>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        System.out.println("læs: fellowships exists");
                        // We check if it is our own fellowship, if it is, we don't add it to the list (Unless we have already applied for it)
                        if (!((HashMap<String, String>) issue.getValue()).get("creatorId").equals(model.getCurrentUserData().getValue().getUid())) {
                            String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("id");
                            System.out.println("læs: fellowship id: " + fellowshipId);
                            if (!pendingsRequestsFellowships.contains(fellowshipId)) {
                                String id = ((HashMap<String, String>) issue.getValue()).get("id");
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

                                Fellowship fs = new Fellowship(id, ownerId, webShop, category, (int) Integer.parseInt("" + amountNeeded), paymentMethod, deadline, pickupCoordinates, partnerId, (int) Integer.parseInt("" + partnerPaid), (int) Integer.parseInt("" + paymentApproved), receiptUrl, (int) Integer.parseInt("" + ownerCompleted), (int) Integer.parseInt("" + partnerCompleted), (int) Integer.parseInt("" + isCompleted));
                                joinableFellowships.put(fellowshipId, fs);

                                Pair<Fellowship, String> pair = new Pair<Fellowship, String>(fs, currentLocation);

                                listItems.add(pair);
                                fellowshipsDetails.add(fs);
                            }
                        }
                    }
                    fellowshipsList.setValue(listItems);
                    setJoinableFellowships();
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

    public void setJoinableFellowships() {
        HashMap<String, Fellowship> joinableFellowships = new HashMap<>();
        for (Fellowship fs : fellowshipsDetails) {
            joinableFellowships.put(fs.id, fs);
        }
        model.setJoinableFellowships(joinableFellowships);
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
