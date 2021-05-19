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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class FindFellowshipsViewModel extends AndroidViewModel {
    private final Model model;

    // Bindable attributes to be shown in UI
    private MutableLiveData<ArrayList<Pair<Fellowship, String>>> fellowshipsList;
    private MutableLiveData<ArrayList<String>> webShopsList;
    private MutableLiveData<ArrayList<String>> categoriesList;

    // Lists holding the full data of the fellowships
    private ArrayList<String> pendingsRequestsFellowships;
    private ArrayList<Fellowship> fellowshipsDetails;

    // Filters
    private String webShop;
    private String category;
    private int amount;
    private int distance;

    public FindFellowshipsViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public void refreshWithFilters(String webShop, String category, int amount, int distance) {
        this.webShop = webShop;
        this.category = category;
        this.amount = amount; // We use -1 as any
        this.distance = distance;

        refreshPendingRequestedFellowships();
    }

    private void setDefaultFilters() {
        webShop = "Any";
        category = "Any";
        amount = -1; // We use -1 as any
        distance = -1;
    }

    private boolean criteriasMet(Fellowship fs) {
        boolean met = true;

        if(!this.webShop.equals("Any")) {
            if (!fs.getWebshop().equals(this.webShop)) {
                met = false;
            }
        }
        if(!this.category.equals("Any")) {
            if (!fs.getCategory().equals(this.category)) {
                met = false;
            }
        }
        if (this.distance != -1) {
            if (distanceBetween(model.getUserLocation(), fs.getPickupCoordinates()) > this.distance) {
                met = false;
            }
        }

        if (this.amount != -1) {
            if (fs.getAmountNeeded() > this.amount) {
                met = false;
            }
        }

        if (met) {
            System.out.println("Criterias met for fellowship " + fs.getId());
        } else {
            System.out.println("Criterias not met for fellowship " + fs.getId());
        }
        return met;
    }

    public Fellowship getFellowshipAt(int index) {
        return fellowshipsDetails.get(index);
    }

    public MutableLiveData<ArrayList<String>> getWebShopsList() {
        if (webShopsList == null) {
            webShopsList = new MutableLiveData<ArrayList<String>>();
        }
        return webShopsList;
    }

    public MutableLiveData<ArrayList<String>> getCategoriesList() {
        if (categoriesList == null) {
            categoriesList = new MutableLiveData<ArrayList<String>>();
        }
        return categoriesList;
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
        setDefaultFilters();
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
                    ArrayList<String> tempWs=new ArrayList<>();
                    tempWs.add("Any");
                    ArrayList<String> tempCg=new ArrayList<>();
                    tempCg.add("Any");

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
                            String deadline = ((HashMap<String, String>) issue.getValue()).get("deadline");
                            String webShop = ((HashMap<String, String>) issue.getValue()).get("webshop");
                            String category = ((HashMap<String, String>) issue.getValue()).get("category");
                            String id = ((HashMap<String, String>) issue.getValue()).get("id");
                            String ownerId = ((HashMap<String, String>) issue.getValue()).get("creatorId");
                            Long amountNeeded = ((HashMap<String, Long>) issue.getValue()).get("amountNeeded");
                            String paymentMethod = ((HashMap<String, String>) issue.getValue()).get("paymentMethod");
                            String pickupCoordinates = ((HashMap<String, String>) issue.getValue()).get("pickupCoordinates");
                            String partnerId = ((HashMap<String, String>) issue.getValue()).get("partnerId");
                            Long partnerPaid = ((HashMap<String, Long>) issue.getValue()).get("partnerPaid");
                            Long paymentApproved = ((HashMap<String, Long>) issue.getValue()).get("paymentApproved");
                            String receiptUrl = ((HashMap<String, String>) issue.getValue()).get("receiptUrl");
                            Long ownerCompleted = ((HashMap<String, Long>) issue.getValue()).get("ownerCompleted");
                            Long partnerCompleted = ((HashMap<String, Long>) issue.getValue()).get("partnerCompleted");
                            Long isCompleted = ((HashMap<String, Long>) issue.getValue()).get("isCompleted");

                            Fellowship fs = new Fellowship(id, ownerId, webShop, category, (int) Integer.parseInt("" + amountNeeded), paymentMethod, deadline, pickupCoordinates, partnerId, (int) Integer.parseInt("" + partnerPaid), (int) Integer.parseInt("" + paymentApproved), receiptUrl, (int) Integer.parseInt("" + ownerCompleted), (int) Integer.parseInt("" + partnerCompleted), (int) Integer.parseInt("" + isCompleted));

                            // We add the category or webshop to the spinnerlist if it isn't already there
                            if (!tempWs.contains(webShop)) {
                                tempWs.add(webShop);
                            }
                            if (!tempCg.contains(category)) {
                                tempCg.add(category);
                            }
                            try {
                                // We filter out those that are either the user's own, or where the deadline has passed
                                if (!pendingsRequestsFellowships.contains(fellowshipId) && calculateDaysLeft(deadline) >= 0 && criteriasMet(fs)) {
                                    joinableFellowships.put(fellowshipId, fs);

                                    Pair<Fellowship, String> pair = new Pair<Fellowship, String>(fs, model.getUserLocation());

                                    listItems.add(pair);
                                    fellowshipsDetails.add(fs);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    webShopsList.setValue(tempWs);
                    categoriesList.setValue(tempCg);
                    fellowshipsList.setValue(listItems);
                    setJoinableFellowships();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int calculateDaysLeft(String deadline) throws ParseException {
        Calendar cal1 = new GregorianCalendar();
        Calendar cal2 = new GregorianCalendar();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date date = new Date(); // Today
        cal1.setTime(date);
        date = sdf.parse(deadline);
        cal2.setTime(date);

        return daysBetween(cal1.getTime(),cal2.getTime());
    }

    private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public void setViewFellowshipInfo(Fellowship fs) {
        model.setViewFellowshipInfo(fs);
    }

    public void setJoinableFellowships() {
        HashMap<String, Fellowship> joinableFellowships = new HashMap<>();
        for (Fellowship fs : fellowshipsDetails) {
            joinableFellowships.put(fs.getId(), fs);
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

    private int distanceBetween(String usersLocation, String pickupLocation) {
        // We convert the strings into doubles
        double lat1, lng1, lat2, lng2;

        String[] parts = usersLocation.split(", ");
        System.out.println(parts[0] + "-" + parts[1]);
        lat1 = Double.parseDouble(parts[0]);
        lng1 = Double.parseDouble(parts[1]);

        parts = pickupLocation.split(", ");
        System.out.println(parts[0] + "-" + parts[1]);
        lat2 = Double.parseDouble(parts[0]);
        lng2 = Double.parseDouble(parts[1]);

        //returns distance in meters
        double a = (lat1 - lat2) * distPerLat(lat1);
        double b = (lng1 - lng2) * distPerLng(lng1);

        return Integer.parseInt(String.format("%.0f", (Math.sqrt(a * a + b * b))));
    }

    private static double distPerLng(double lng){
        return 0.0003121092*Math.pow(lng, 4)
                +0.0101182384*Math.pow(lng, 3)
                -17.2385140059*lng*lng
                +5.5485277537*lng+111301.967182595;
    }

    private static double distPerLat(double lat){
        return -0.000000487305676*Math.pow(lat, 4)
                -0.0033668574*Math.pow(lat, 3)
                +0.4601181791*lat*lat
                -1.4558127346*lat+110579.25662316;
    }
}
