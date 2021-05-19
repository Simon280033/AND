package com.example.andproject.ViewModel;

import android.app.Application;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.andproject.Entities.CompletedCounter;
import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.example.andproject.View.NewFellowshipActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileEditorViewModel extends AndroidViewModel {
    private final Model model;

    // Bindable attributes to be shown in UI
    private MutableLiveData<String> displayName;
    private MutableLiveData<String> avatarUrl;
    private MutableLiveData<String> email;
    private MutableLiveData<Boolean> dataChanged;

    // Attributes needed for methods
    private boolean newUser;
    private Uri downloadUri; // Where to download the user avatar

    public ProfileEditorViewModel(Application app) {
        super(app);
        model = Model.getInstance(app);
    }

    private void setUserDetails(User user)  {
    }

    public MutableLiveData<String> getDisplayName() {
        if (displayName == null) {
            displayName = new MutableLiveData<String>();
        }
        return displayName;
    }

    public MutableLiveData<String> getEmail() {
        if (email == null) {
            email = new MutableLiveData<String>();
        }
        return email;
    }

    public MutableLiveData<String> getAvatarUrl() {
        if (avatarUrl == null) {
            avatarUrl = new MutableLiveData<String>();
        }
        return avatarUrl;
    }

    public void refreshUserDetails()  {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query ownerQuery = myRef.child("users").orderByChild("id").equalTo(model.getCurrentUserData().getValue().getUid());
        ownerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String dn = ((HashMap<String, String>) issue.getValue()).get("displayName");
                        String em = ((HashMap<String, String>) issue.getValue()).get("email");
                        String iu = ((HashMap<String, String>) issue.getValue()).get("imageUrl");

                        displayName.setValue(dn);
                        avatarUrl.setValue(iu);
                        email.setValue(em);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getOrCreateUser() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(model.getCurrentUserData().getValue().getUid());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) { // If the user doesn't already exist in the Realtime Database, it means that this is the first login
                    // Create new user
                    newUser = true;
                    uploadImageToFireBaseStorage(model.getCurrentUserData().getValue().getPhotoUrl());
                    saveUserInfo(model.getCurrentUserData().getValue().getDisplayName());

                    getOrCreateUser();
                } else {
                    newUser = false;
                    Map<String,String> td=(HashMap<String, String>)dataSnapshot.getValue();

                    User user = new User(td.get("id"), td.get("displayName"), td.get("imageUrl"), td.get("email"));
                    setUserDetails(user);
                    model.setThisUser(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.addListenerForSingleValueEvent(eventListener);
    }

    public void uploadImageToFireBaseStorage(Uri imageUri) {
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference avatarsRef = storageRef.child("images/avatars/" + model.getCurrentUserData().getValue().getUid());
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
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    avatarUrl.setValue(task.getResult().toString());
                    //dataChanged.setValue(true); // Enable the save button on the view!!
                }
            }
        });
    }

    public void saveUserInfo(String displayName) {
        User user = new User(model.getCurrentUserData().getValue().getUid(), displayName, avatarUrl.getValue(), model.getCurrentUserData().getValue().getEmail());
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(model.getCurrentUserData().getValue().getUid());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // We check if there is an avatar for this user in the storage
        storageRef.child("images/avatars/" + model.getCurrentUserData().getValue().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                User updatedUser = user;
                updatedUser.setImageUrl(uri.toString());
                myRef.setValue(updatedUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });

        if (newUser) {
            // We also create a table for the Fellowships-completed counter
            DatabaseReference counterRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("completedCounter").child(model.getCurrentUserData().getValue().getUid());
            counterRef.setValue(new CompletedCounter(model.getCurrentUserData().getValue().getUid(), 0));
        }
    }

    public LiveData<FirebaseUser> getCurrentUserData() {
        return model.getCurrentUserData();
    }

    public void updateUser(String displayName, Uri avatarUri) {
       model.updateCurrentUser(displayName, avatarUri);
    }

    }