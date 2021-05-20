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
// This viewmodel determines what is being shown on the join fellowship view
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
        String ownerId = model.getViewFellowshipInfo().getCreatorId();
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
        Fellowship fs = model.getFellowshipById(model.getViewFellowshipInfo().getId());

        webshop.setValue(fs.getWebshop());
        amountNeeded.setValue(fs.getAmountNeeded() + " DKK");
        paymentMethod.setValue(fs.getPaymentMethod());
        deadline.setValue(fs.getDeadline());
        // FIND A WAY TO CALCULATE DISTANCE BETWEEN TWO LATLONG POINTS!!!
        distance.setValue(distanceBetween(model.getUserLocation(), fs.getPickupCoordinates()));
    }

    public void refreshDetails() {
        refreshOwnerDetails();
        refreshFellowshipDetails();
    }

    public void requestJoin() {
        // We create the Fellowship request object
        String requestId = UUID.randomUUID().toString(); // We create a random ID;
        String fellowshipId = model.getViewFellowshipInfo().getId();
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

    private String distanceBetween(String usersLocation, String pickupLocation) {
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

        return String.format("%.2f", (Math.sqrt(a * a + b * b))) + " meters";
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
