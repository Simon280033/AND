package com.example.andproject.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

    private Button profileActionButton, cancelButton, reportButton;

    private TextView nameTextView, shipsCounterTextView;
    private ImageView imageView;
    private ScrollView ratingsScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewViewModel.class);
        setContentView(R.layout.activity_profile_view);

        // We get the UI components
        findViews();

        // We set button's on-click methods
        setButtonMethods();

        setButtonAccordingToProfile();

        // We set the UI
        setUi();
    }

    private void reportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Report abuse");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.report(input.getText().toString());
                Toast.makeText(ProfileViewActivity.this, "User reported! An admin will review your ticket.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void setButtonMethods() {
        cancelButton.setOnClickListener((View v) -> {
            onBackPressed();
        });

        profileActionButton.setOnClickListener((View v) -> {
            goToProfileEdit();
        });

        reportButton.setOnClickListener((View v) -> {
            reportDialog();
        });
    }

    private void findViews() {
        profileActionButton = findViewById(R.id.profileActionButton);
        cancelButton = findViewById(R.id.cancelButton);
        reportButton = findViewById(R.id.reportButton);
        nameTextView = findViewById(R.id.nameTextView);
        shipsCounterTextView = findViewById(R.id.shipsCounterTextView);
        imageView = findViewById(R.id.imageView);
        ratingsScrollView = findViewById(R.id.ratingsScrollView);
    }

    private void setUi() {
        // We bind the UI elements
        bindUiElements();
        // We refresh them
        viewModel.refreshUserDetails();
        // We make the edit/report buttons visible if it is our own profile
        setButtonAccordingToProfile();
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
                Glide.with(ProfileViewActivity.this).load(Uri.parse(newUrl)).apply(RequestOptions.circleCropTransform()).into(imageView);
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
        if (!viewModel.isOwnProfile()) {
            profileActionButton.setVisibility(View.INVISIBLE);
        } else {
            reportButton.setVisibility(View.INVISIBLE);
        }
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