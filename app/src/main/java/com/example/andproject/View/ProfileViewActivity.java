package com.example.andproject.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.andproject.Entities.User;
import com.example.andproject.R;
import com.example.andproject.ViewModel.ProfileViewViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileViewActivity extends AppCompatActivity {

    private ProfileViewViewModel viewModel;

    private Button profileActionButton;
    private Button cancelButton;

    private TextView nameTextView;
    private TextView shipsCounterTextView;
    private ImageView imageView;
    private ScrollView ratingsScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewViewModel.class);
        setContentView(R.layout.activity_profile_view);

        // We get the UI components
        profileActionButton = findViewById(R.id.profileActionButton);
        cancelButton = findViewById(R.id.cancelButton);

        nameTextView = findViewById(R.id.nameTextView);
        shipsCounterTextView = findViewById(R.id.shipsCounterTextView);
        imageView = findViewById(R.id.imageView);
        ratingsScrollView = findViewById(R.id.ratingsScrollView);

        cancelButton.setOnClickListener((View v) -> {
            onBackPressed();
        });

        setButtonAccordingToProfile();

        setUi();
    }

    private void setUi() {
        // We bind the UI elements
        bindUiElements();
        // We refresh them
        viewModel.refreshUserDetails();
    }

    // This method binds the View's UI elements to the properties in the viewmodel
    private void bindUiElements() {
        // We bind the name text view
        final Observer<String> displayNameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newName) {
                // Update the UI, in this case, a TextView.
                nameTextView.setText(newName);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getDisplayName().observe(this, displayNameObserver);

        // We bind the avatar
        final Observer<String> avatarUrlObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newUrl) {
                // We use glide to set the image
                Glide.with(ProfileViewActivity.this).load(Uri.parse(newUrl)).into(imageView);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getAvatarUrl().observe(this, avatarUrlObserver);

        // We bind the shipscounter.
        final Observer<String> shipsCounterObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newCount) {
                // Update the UI, in this case, a TextView.
                shipsCounterTextView.setText(newCount);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getShipsCounter().observe(this, shipsCounterObserver);
    }

    private void setButtonAccordingToProfile() {
        // We figure out whether or not this is our own profile
        if (viewModel.isOwnProfile()) {
            // If it is
            profileActionButton.setText("Edit profile");
            profileActionButton.setOnClickListener((View v) -> {
                goToProfileEdit();
            });
        } else {
            profileActionButton.setVisibility(View.INVISIBLE);
        }
    }

    private void onActionButtonClicked() {
        // We figure out whether or not this is our own profile
        goToMainActivity();
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goToProfileEdit() {
        startActivity(new Intent(this, ProfileEditorActivity.class));
        finish();
    }
}