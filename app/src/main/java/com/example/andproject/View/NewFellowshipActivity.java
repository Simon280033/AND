package com.example.andproject.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.example.andproject.R;
import com.example.andproject.ViewModel.NewFellowshipViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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

        findViews();

        setButtonActions();

        // We set the date/location edit texts uneditable
        deadlineEditText.setEnabled(false);
        locationEditText.setEnabled(false);

        setUi();

        setDatePicker();

        bindButtonAvailability();
    }

    private void setUi() {
        // We bind the UI elements
        bindUiElements();
        // We refresh them
        viewModel.refreshSpinnerLists();
    }

    private void setButtonActions() {
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
            viewModel.getPickupLocation();
            viewModel.getUsersLocation(this);
            createFellowshipButton.setEnabled(allInfoEntered());
            if (allInfoEntered()) {
                createFellowshipButton.setAlpha(1f);
            } else {
                createFellowshipButton.setAlpha(.5f);
            }
        });
    }

    private void findViews() {
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
        int partnerPaid = 0;
        int paymentApproved = 0;
        String receiptUrl = "null";
        int ownerCompleted = 0;
        int partnerCompleted = 0;
        int isCompleted = 0; // We use this as a BIT - 1 = TRUE, 0 = FALSE

        Fellowship fs = new Fellowship(id, creatorId, webshop, category, amountNeeded, paymentMethod, deadline, latAndLong, partnerId, partnerPaid, paymentApproved, receiptUrl, ownerCompleted, partnerCompleted, isCompleted);

        viewModel.createNewFellowShip(fs);
    }

    // This method binds the create buttons-availability to whether or not all info is filled
    private void bindButtonAvailability() {
        createFellowshipButton.setEnabled(false); // Not enabled by default
        createFellowshipButton.setAlpha(.5f);

        amountNeededEditText.addTextChangedListener (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2){
                    createFellowshipButton.setEnabled(allInfoEntered());
                    if (allInfoEntered()) {
                        createFellowshipButton.setAlpha(1f);
                    } else {
                        createFellowshipButton.setAlpha(.5f);
                    }
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
                if (allInfoEntered()) {
                    createFellowshipButton.setAlpha(1f);
                } else {
                    createFellowshipButton.setAlpha(.5f);
                }
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

    // This method binds the View's UI elements to the properties in the viewmodel
    private void bindUiElements() {
        // We bind the webshop spinner
        final Observer<ArrayList<String>> webShopObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newValue) {
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(NewFellowshipActivity.this, android.R.layout.simple_spinner_item, newValue);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                webshopSpinner.setAdapter(dataAdapter);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getWebShopsList().observe(this, webShopObserver);

        // We bind the categories spinner
        final Observer<ArrayList<String>> categoriesObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newValue) {
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(NewFellowshipActivity.this, android.R.layout.simple_spinner_item, newValue);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                categorySpinner.setAdapter(dataAdapter);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getCategoriesList().observe(this, categoriesObserver);

        // We bind the payment methods spinner
        final Observer<ArrayList<String>> paymentMethodsObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newValue) {
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(NewFellowshipActivity.this, android.R.layout.simple_spinner_item, newValue);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                paymentMethodSpinner.setAdapter(dataAdapter);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPaymentMethodsList().observe(this, paymentMethodsObserver);

        // We bind the location edit text
        final Observer<String> locationObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                locationEditText.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPickupLocation().observe(this, locationObserver);
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