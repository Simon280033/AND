package com.example.andproject.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.FellowshipRequest;
import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipJoinViewModel;
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
// This activity handles the view for when a user wants to join a FellowShip
public class FellowshipJoinActivity extends AppCompatActivity {

    private FellowshipJoinViewModel viewModel;

    private TextView ownerNameTextView, webShopTextView, minimumAmountNeededTextView, paymentMethodTextView, deadlineTextView, distanceTextView;
    private ImageView ownerAvatarView;
    private Button cancelJoinButton, requestToJoinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FellowshipJoinViewModel.class);

        setContentView(R.layout.activity_fellowship_join);

        findViews();

        setButtonActions();

        // We set the UI
        setUi();
    }

    private void setUi() {
        // We bind the UI elements
        bindUiElements();
        // We refresh them
        viewModel.refreshDetails();
    }

    // This method binds the View's UI elements to the properties in the viewmodel
    private void bindUiElements() {
        // We bind the name text view
        final Observer<String> displayNameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                // Update the UI, in this case, a TextView.
                ownerNameTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getOwnerName().observe(this, displayNameObserver);

        // We bind the avatar
        final Observer<String> avatarObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                Glide.with(FellowshipJoinActivity.this).load(Uri.parse(newValue)).apply(RequestOptions.circleCropTransform()).into(ownerAvatarView);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getAvatarUrl().observe(this, avatarObserver);

        // We bind the webshop text view
        final Observer<String> webshopObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                // Update the UI, in this case, a TextView.
                webShopTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getWebshop().observe(this, webshopObserver);

        // We bind the amount needed text view
        final Observer<String> amountObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                // Update the UI, in this case, a TextView.
                minimumAmountNeededTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getAmountNeeded().observe(this, amountObserver);

        // We bind the payment method text view
        final Observer<String> paymentObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                // Update the UI, in this case, a TextView.
                paymentMethodTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getPaymentMethod().observe(this, paymentObserver);

        // We bind the deadline text view
        final Observer<String> deadlineObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                // Update the UI, in this case, a TextView.
                deadlineTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getDeadline().observe(this, deadlineObserver);

        // We bind the distance text view
        final Observer<String> distanceObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                // Update the UI, in this case, a TextView.
                distanceTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getDistancee().observe(this, distanceObserver);
    }

    private void findViews() {
        ownerNameTextView = findViewById(R.id.ownerNameTextView);
        webShopTextView = findViewById(R.id.webShopTextView);
        minimumAmountNeededTextView = findViewById(R.id.minimumAmountNeededTextView);
        paymentMethodTextView = findViewById(R.id.paymentMethodTextView);
        deadlineTextView = findViewById(R.id.deadlineTextView);
        distanceTextView = findViewById(R.id.distanceTextView);

        ownerAvatarView = findViewById(R.id.ownerAvatarView);

        requestToJoinButton = findViewById(R.id.requestToJoinButton);
        cancelJoinButton = findViewById(R.id.cancelJoinButton);
    }

    private void setButtonActions() {
        requestToJoinButton.setOnClickListener((View v) -> {
            viewModel.requestJoin();
            Toast.makeText(FellowshipJoinActivity.this, "Requested to join Fellowship! Pending owner approval.",
                    Toast.LENGTH_LONG).show();
            onBackPressed();
        });

        cancelJoinButton.setOnClickListener((View v) -> {
            onBackPressed();
        });
    }
}