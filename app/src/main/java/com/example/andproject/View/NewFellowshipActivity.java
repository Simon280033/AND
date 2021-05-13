package com.example.andproject.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.User;
import com.example.andproject.R;
import com.example.andproject.ViewModel.NewFellowshipViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.android.material.internal.ContextUtils.getActivity;

public class NewFellowshipActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private NewFellowshipViewModel viewModel;

    private Spinner categorySpinner, webshopSpinner, paymentMethodSpinner;
    private Button deadlinePickButton, createFellowshipButton, cancelCreateFellowshipButton, getLocationButton;
    private EditText deadlineEditText, amountNeededEditText, locationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NewFellowshipViewModel.class);
        viewModel.init();
        setContentView(R.layout.activity_new_fellowship);

        webshopSpinner = findViewById(R.id.webshopSpinner);
        categorySpinner = findViewById(R.id.categorySpinner);
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner);

        amountNeededEditText = findViewById(R.id.amountNeededEditText);
        deadlineEditText = findViewById(R.id.deadlineEditText);
        locationEditText = findViewById(R.id.locationEditText);

        deadlinePickButton = findViewById(R.id.deadlinePickButton);
        createFellowshipButton = findViewById(R.id.createFellowshipButton);
        cancelCreateFellowshipButton = findViewById(R.id.cancelCreateFellowshipButton);
        getLocationButton = findViewById(R.id.getLocationButton);

        cancelCreateFellowshipButton.setOnClickListener((View v) -> {
            goToFellowships();
        });

        createFellowshipButton.setOnClickListener((View v) -> {
            createNewFellowShip();
            Toast.makeText(NewFellowshipActivity.this, "Successfully created new fellowship!",
                    Toast.LENGTH_LONG).show();
            goToFellowships();
        });

        getLocationButton.setOnClickListener((View v) -> {
            getUsersLocation();
            createFellowshipButton.setEnabled(allInfoEntered());
        });

        // We set the date/location edit texts uneditable
        deadlineEditText.setEnabled(false);
        locationEditText.setEnabled(false);

        setWebshopSpinner();
        setCategorySpinner();
        setPaymentMethodSpinner();
        setDatePicker();

        bindButtonAvailability();
    }

    // YOU HAVE MANUALLY ENABLED LOCATIONS FOR APP!!!! FIND A WAY TO PROMPT USER TO ENABLE IT
    private void getUsersLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String latAndLong = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("læs: no perms");
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            locationEditText.setEnabled(true);
                            locationEditText.setText(location.getLatitude() + ", " + location.getLongitude());
                            locationEditText.setEnabled(false);
                        }
                    }
                });

    }

    private void createNewFellowShip() {
        // We create the Fellowship object
        String id = UUID.randomUUID().toString(); // We create a random ID
        String creatorId = viewModel.getCurrentUser().getValue().getUid();
        String webshop = (String) webshopSpinner.getSelectedItem();
        String category = (String) categorySpinner.getSelectedItem();
        int amountNeeded = Integer.parseInt(amountNeededEditText.getText().toString());
        String paymentMethod = (String) paymentMethodSpinner.getSelectedItem();;
        String deadline = deadlineEditText.getText().toString();
        String latAndLong = locationEditText.getText().toString();
        String partnerId = "null"; // We have no partner to begin with. Can't store null-values in realtime database, so we store a string that denotes it
        int isCompleted = 0; // We use this as a BIT - 1 = TRUE, 0 = FALSE

        Fellowship fs = new Fellowship(id, creatorId, webshop, category, amountNeeded, paymentMethod, deadline, latAndLong, partnerId, isCompleted);

        // We save it to the database
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("fellowships").child(id);

        myRef.setValue(fs);
    }

    // This method binds the create buttons-availability to whether or not all info is filled
    private void bindButtonAvailability() {
        createFellowshipButton.setEnabled(false); // Not enabled by default

        amountNeededEditText.addTextChangedListener (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2){
                    createFellowshipButton.setEnabled(allInfoEntered());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        deadlineEditText.addTextChangedListener (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createFellowshipButton.setEnabled(allInfoEntered());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // This method checks if all the relevant info has been entered
    private boolean allInfoEntered() {
        boolean allIsEntered = true;

        if(webshopSpinner == null || webshopSpinner.getSelectedItem() == null) {
            allIsEntered = false;
        }
        if(categorySpinner == null || categorySpinner.getSelectedItem() == null) {
            allIsEntered = false;
        }
        if(paymentMethodSpinner == null || paymentMethodSpinner.getSelectedItem() == null) {
            allIsEntered = false;
        }

        if(deadlineEditText.getText().toString().trim().length() == 0) {
            allIsEntered = false;
        }
        if(amountNeededEditText.getText().toString().trim().length() == 0 || Integer.parseInt(amountNeededEditText.getText().toString()) < 1) {
            allIsEntered = false;
        }
        if(locationEditText.getText().toString().trim().length() == 0) {
            allIsEntered = false;
        }

        return allIsEntered;
    }

    private void setDatePicker() {
        LocalDate today = getToday();

        @SuppressLint({"NewApi", "LocalSuppress"}) DatePickerDialog datePickerDialog = new DatePickerDialog(
                NewFellowshipActivity.this, NewFellowshipActivity.this, today.getYear(), today.getMonthValue()-1, today.getDayOfMonth()); // It's one month ahead on my machine?

        deadlinePickButton.setOnClickListener((View v) -> {
            datePickerDialog.show();
        });
    }

    private void setWebshopSpinner() {
        // Spinner click listener
        webshopSpinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        ArrayList<String> webshops = new ArrayList<String>();

        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("webshops");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {

                } else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        webshops.add(ds.getKey());
                    }

                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(NewFellowshipActivity.this, android.R.layout.simple_spinner_item, webshops);

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    webshopSpinner.setAdapter(dataAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.addListenerForSingleValueEvent(eventListener);
    }

    private ArrayList<String> getWebshops(ArrayList<String> webshops) {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("webshops");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {

                } else {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        webshops.add(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.addListenerForSingleValueEvent(eventListener);
        return webshops;
    }

    private void setCategorySpinner() {
        // Spinner click listener
        categorySpinner.setOnItemSelectedListener(this);

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

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        categorySpinner.setAdapter(dataAdapter);
    }

    private void setPaymentMethodSpinner() {
        // Spinner click listener
        paymentMethodSpinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        ArrayList<String> paymentMethods = new ArrayList<String>();
        paymentMethods.add("MobilePay");
        paymentMethods.add("Cash");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentMethods);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        paymentMethodSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        deadlineEditText.setEnabled(true);
        deadlineEditText.setText(dayOfMonth + "/" + (month+1) + "/" + year);
        deadlineEditText.setEnabled(false);
    }

    @SuppressLint("NewApi")
    private LocalDate getToday() {
        Date date = new Date();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void goToFellowships() {
        startActivity(new Intent(this, FellowshipsActivity.class));
        finish();
    }
}