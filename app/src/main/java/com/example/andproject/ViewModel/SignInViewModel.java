package com.example.andproject.ViewModel;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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

import java.util.HashMap;
// This viewmodel determines what is being shown on the sign-in view
public class SignInViewModel extends AndroidViewModel {
    private final Model model;

    public SignInViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public void setUserLocation(Context context) {
        getUsersLocation(context);
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
                            model.setUserLocation(location.getLatitude() + ", " + location.getLongitude());
                        }
                    }
                });
    }

    public void setCustomUserData() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query ownerQuery = myRef.child("users").orderByChild("id").equalTo(model.getCurrentUserData().getValue().getUid());
        ownerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String id = ((HashMap<String, String>) issue.getValue()).get("id");
                        String dn = ((HashMap<String, String>) issue.getValue()).get("displayName");
                        String iu = ((HashMap<String, String>) issue.getValue()).get("imageUrl");
                        String em = ((HashMap<String, String>) issue.getValue()).get("email");

                        model.setThisUser(new User(id, dn, iu, em));
                        model.setIsNewUser(false);
                    }
                } else {
                    model.setIsNewUser(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setViewProfileOf(User user) {
        model.setViewProfileOf(user);
    }

    public User getViewProfileOf() {
        return model.getViewProfileOf();
    }

    public LiveData<FirebaseUser> getCurrentUser(){
        return model.getCurrentUserData();
    }
}