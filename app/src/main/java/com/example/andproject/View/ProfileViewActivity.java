package com.example.andproject.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.andproject.ViewModel.ProfileEditorViewModel;
import com.example.andproject.ViewModel.ProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProfileViewActivity extends AppCompatActivity {

    private ProfileViewModel viewModel;

    private Button profileActionButton;
    private Button cancelButton;

    private TextView nameTextView;
    private TextView shipsCounterTextView;
    private ImageView imageView;
    private ScrollView ratingsScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        setContentView(R.layout.activity_profile_view);

        // We get the UI components
        profileActionButton = findViewById(R.id.profileActionButton);
        cancelButton = findViewById(R.id.cancelButton);

        nameTextView = findViewById(R.id.nameTextView);
        shipsCounterTextView = findViewById(R.id.shipsCounterTextView);
        imageView = findViewById(R.id.imageView);
        ratingsScrollView = findViewById(R.id.ratingsScrollView);

        cancelButton.setOnClickListener((View v) -> {
            goToMainActivity();
        });

        setButtonAccordingToProfile();

        // We set the avatar
        setAvatar();
    }

    private void setAvatar() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        myRef.child("users").child(viewModel.getCurrentUserData().getValue().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    DataSnapshot ds = task.getResult();

                    Map<String,String> td=(HashMap<String, String>)ds.getValue();

                    User user = new User(td.get("id"), td.get("displayName"), td.get("imageUrl"), td.get("email"));

                    nameTextView.setText(user.displayName);
                    Glide.with(ProfileViewActivity.this).load(Uri.parse(user.imageUrl)).into(imageView);
                }
            }
        });
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