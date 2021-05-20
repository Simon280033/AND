package com.example.andproject.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.andproject.Entities.CompletedCounter;
import com.example.andproject.Entities.User;
import com.example.andproject.R;
import com.example.andproject.ViewModel.ProfileEditorViewModel;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
// This activity acts as a view for when the user wants to edit their profile
public class ProfileEditorActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 42;
    private ProfileEditorViewModel viewModel;

    private Button saveButton, changeImageButton;
    private EditText nameEditText;
    private TextView emailTextView;
    private ImageView avatarView;

    private static final int PICK_IMAGE = 100;

    private Uri imageUri;

    private int imageUpdateCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileEditorViewModel.class);
        setContentView(R.layout.profile_editor_activity);

        // We set the image update counter for the session to 0
        imageUpdateCount = 0;

        // We get the UI components
        findViews();

        setUi();

        // We set button methods
        setButtonMethods();
    }

    private void setUi() {
        // We bind the UI elements
        bindUiElements();
        viewModel.getOrCreateUser();
        // We refresh them
        viewModel.refreshUserDetails();
    }

    // This method binds the View's UI elements to the properties in the viewmodel
    private void bindUiElements() {
        // We bind the name text edit
        final Observer<String> displayNameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                // Update the UI, in this case, a TextView.
                nameEditText.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getDisplayName().observe(this, displayNameObserver);

        // We bind the email text view
        final Observer<String> emailObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                // Update the UI, in this case, a TextView.
                emailTextView.setText(newValue);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getEmail().observe(this, emailObserver);

        // We bind the avatar
        final Observer<String> avatarObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newValue) {
                Glide.with(ProfileEditorActivity.this).load(Uri.parse(newValue)).into(avatarView);
                imageUpdateCount++;
                if (imageUpdateCount > 1) {
                    Toast.makeText(ProfileEditorActivity.this, "Successfully updated avatar!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getAvatarUrl().observe(this, avatarObserver);
    }

    private void setButtonMethods() {
        saveButton.setOnClickListener((View v) -> {
            saveButtonPressed();
            goToMainActivity();
        });

        changeImageButton.setOnClickListener((View v) -> {
            openGallery();
        });
    }

    private void findViews() {
        saveButton = findViewById(R.id.signOutButton);
        changeImageButton = findViewById(R.id.changeImageButton);

        nameEditText = findViewById(R.id.nameEditText);
        emailTextView = findViewById(R.id.emailTextView);
        avatarView = findViewById(R.id.avatarView);
    }

    // When user wants to upload new avatar, he can choose from gallery
    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    // This method is called when a user picks a new avatar from the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();

            viewModel.uploadImageToFireBaseStorage(imageUri);
        }
    }

    private void saveButtonPressed() {
        // If the standard avatar view is visible, the user has not changed pictures
        // We check if the changed usernames
        if (!(nameEditText.getText().equals(viewModel.getCurrentUserData().getValue().getDisplayName()))) {
            viewModel.saveUserInfo(nameEditText.getText().toString());
            Toast.makeText(ProfileEditorActivity.this, "Successfully updated display name!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
