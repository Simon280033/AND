package com.example.andproject.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.andproject.R;
import com.example.andproject.ViewModel.ProfileEditorViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileEditorActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 42;
    private ProfileEditorViewModel viewModel;

    private Button saveButton;
    private Button changeImageButton;
    private TextView displayNameTextView;
    private TextView emailTextView;
    private ImageView avatarView;
    private ImageView newAvatarView;

    private static final int PICK_IMAGE = 100;
    private static final int SET_DEFAULT_IMAGE = 101;

    private Uri imageUri;
    private Uri downloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileEditorViewModel.class);
        setContentView(R.layout.profile_editor_activity);

        // We get the UI components
        saveButton = findViewById(R.id.saveButton);
        changeImageButton = findViewById(R.id.changeImageButton);

        displayNameTextView = findViewById(R.id.displayNameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        avatarView = findViewById(R.id.avatarView);
        newAvatarView = findViewById(R.id.newAvatarView);
        newAvatarView.setVisibility(View.INVISIBLE); // New avatar view is invisible by default

        // We set the user details
        setUserDetails();

        // We set button methods
        saveButton.setOnClickListener((View v) -> {
            updateUser();
            goToMainActivity();
        });

        changeImageButton.setOnClickListener((View v) -> {
            openGallery();
        });

        avatarExists();
    }

    private void avatarExists() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        boolean exists = false;
        storageRef.child("images/avatars/" + viewModel.getCurrentUserData().getValue().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // If the avatar exists, we load it into glide
                System.out.println("test; exists");
                Glide.with(ProfileEditorActivity.this).load(uri).into(avatarView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // If it doesn't, we create one and load it into glide
                System.out.println("test; exists not");
                Uri theImage = viewModel.getCurrentUserData().getValue().getPhotoUrl();
                if (theImage == null) {
                    theImage = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.logo);
                }
                UploadImageToFireBaseStorage(theImage);
                Glide.with(ProfileEditorActivity.this).load(theImage).into(avatarView);
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            saveButton.setEnabled(false);
            avatarView.setVisibility(View.INVISIBLE);
            newAvatarView.setVisibility(View.VISIBLE);

                imageUri = data.getData();
                imageUri = viewModel.getCurrentUserData().getValue().getPhotoUrl();

            newAvatarView.setImageURI(imageUri);

            UploadImageToFireBaseStorage(imageUri);
        }
    }

    private void UploadImageToFireBaseStorage(Uri imageUri) {
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference riversRef = storageRef.child("images/avatars/" + viewModel.getCurrentUserData().getValue().getUid());
        UploadTask uploadTask = riversRef.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                System.out.println("test; complete");
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    saveButton.setEnabled(true);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void setUserDetails()  {
        viewModel.getCurrentUserData().observe(this, user -> {
            // We check if a user was returned
            if (user != null) {
                displayNameTextView.setText(user.getDisplayName());
                emailTextView.setText(user.getEmail());
            }
        });
    }

    private void updateUser() {
        if (downloadUri != null) {
            viewModel.updateUser("username", downloadUri);
        }
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
