package com.example.andproject.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
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

public class ProfileEditorActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 42;
    private ProfileEditorViewModel viewModel;

    private Button saveButton;
    private Button changeImageButton;
    private EditText nameEditText;
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
        saveButton = findViewById(R.id.signOutButton);
        changeImageButton = findViewById(R.id.changeImageButton);

        nameEditText = findViewById(R.id.nameEditText);
        emailTextView = findViewById(R.id.emailTextView);
        avatarView = findViewById(R.id.avatarView);
        newAvatarView = findViewById(R.id.newAvatarView);
        newAvatarView.setVisibility(View.INVISIBLE); // New avatar view is invisible by default

        // We set the user details
        checkIfUserExists();

        // We set button methods
        saveButton.setOnClickListener((View v) -> {
            saveButtonPressed();
            goToMainActivity();
        });

        changeImageButton.setOnClickListener((View v) -> {
            openGallery();
        });

        avatarExists();
    }

    private void checkIfUserExists() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(viewModel.getCurrentUserData().getValue().getUid());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    //create new user
                    UploadImageToFireBaseStorage(viewModel.getCurrentUserData().getValue().getPhotoUrl());
                    SaveUserInfo();

                    checkIfUserExists();
                } else {
                    Map<String,String> td=(HashMap<String, String>)dataSnapshot.getValue();

                    User user = new User(td.get("id"), td.get("displayName"), td.get("imageUrl"), td.get("email"));
                    setUserDetails(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.addListenerForSingleValueEvent(eventListener);
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

            newAvatarView.setImageURI(imageUri);

            UploadImageToFireBaseStorage(imageUri);
        }
    }

    private void UploadImageToFireBaseStorage(Uri imageUri) {
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference avatarsRef = storageRef.child("images/avatars/" + viewModel.getCurrentUserData().getValue().getUid());
        UploadTask uploadTask = avatarsRef.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return avatarsRef.getDownloadUrl();
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

    private void setUserDetails(User user)  {
        nameEditText.setText(user.displayName);
        emailTextView.setText(user.email);
    }

    private void saveButtonPressed() {
        // If the standard avatar view is visible, the user has not changed pictures
        // We check if the changed usernames
        if (!(nameEditText.getText().equals(viewModel.getCurrentUserData().getValue().getDisplayName()))) {
            SaveUserInfo();
        }
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

    private void SaveUserInfo() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(viewModel.getCurrentUserData().getValue().getUid());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.child("images/avatars/" + viewModel.getCurrentUserData().getValue().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println("test; exists");
                User user = new User(viewModel.getCurrentUserData().getValue().getUid(), nameEditText.getText().toString(), uri.toString(), viewModel.getCurrentUserData().getValue().getEmail());
                myRef.setValue(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("test; exists not");
            }
        });
    }
}
