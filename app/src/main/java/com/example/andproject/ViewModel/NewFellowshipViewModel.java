package com.example.andproject.ViewModel;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.ArrayAdapter;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.Message;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class NewFellowshipViewModel extends AndroidViewModel {
    private final Model model;

    // Bindable attributes to be shown in UI
    private MutableLiveData<ArrayList<String>> webShopsList;
    private MutableLiveData<ArrayList<String>> categoriesList;
    private MutableLiveData<ArrayList<String>> paymentMethodsList;

    private MutableLiveData<String> pickupLocation;

    public NewFellowshipViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
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

    public MutableLiveData<ArrayList<String>> getPaymentMethodsList() {
        if (paymentMethodsList == null) {
            paymentMethodsList = new MutableLiveData<ArrayList<String>>();
        }
        return paymentMethodsList;
    }

    public MutableLiveData<String> getPickupLocation() {
        if (pickupLocation == null) {
            pickupLocation = new MutableLiveData<String>();
        }
        return pickupLocation;
    }

    public void refreshSpinnerLists() {
        setWebshopSpinner();
        setCategorySpinner();
        setPaymentMethodSpinner();
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
                            pickupLocation.setValue(location.getLatitude() + ", " + location.getLongitude());
                        }
                    }
                });
    }

    public void createNewFellowShip(Fellowship fs) {
        // We save it to the database
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowships").child(fs.id);

        myRef.setValue(fs);
    }

    private void setWebshopSpinner() {
        // Spinner Drop down elements
        ArrayList<String> webshops = new ArrayList<String>();

        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("webshops");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        webshops.add(ds.getKey());
                    }
                    webShopsList.setValue(webshops);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.addListenerForSingleValueEvent(eventListener);
    }

    private void setCategorySpinner() {
        // Spinner Drop down elements
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Other");
        categories.add("Clothing");
        categories.add("Electronics");
        categories.add("Furniture");
        categories.add("Toys");
        categories.add("Outdoor");
        categories.add("Tools");
        categories.add("Edible");

        categoriesList.setValue(categories);
    }

    private void setPaymentMethodSpinner() {
        // Spinner Drop down elements
        ArrayList<String> paymentMethods = new ArrayList<String>();
        paymentMethods.add("MobilePay");
        paymentMethods.add("Cash");

        paymentMethodsList.setValue(paymentMethods);
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

    public void saveMessage(String message) {
        model.saveMessage(message);
    }

    public LiveData<Message> getMessage() {
        return model.getMessage();
    }

    public void signOut() {
        model.signOut();
    }
}
