package com.example.andproject.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.User;
import com.example.andproject.Mediator.UserRepository;
import com.example.andproject.Model.Model;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignInViewModel extends AndroidViewModel {
    private final Model model;

    public SignInViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }

    public void setCustomerUserData() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query ownerQuery = myRef.child("users").orderByChild("id").equalTo(model.getCurrentUserData().getValue().getUid());
        ownerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String id = ((HashMap<String, String>) issue.getValue()).get("id");
                        String dn = ((HashMap<String, String>) issue.getValue()).get("displayName");
                        String iu = ((HashMap<String, String>) issue.getValue()).get("imageUrl");
                        String em = ((HashMap<String, String>) issue.getValue()).get("email");

                        model.setThisUser(new User(id, dn, iu, em));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setViewProfileOf(User user) {
        model.setViewProfileOf(user);
    }

    public User getViewProfileOf() {
        return model.getViewProfileOf();
    }

    public LiveData<FirebaseUser> getCurrentUser(){
        return model.getCurrentUserData();
    }
}
