package com.example.andproject.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.andproject.Entities.Fellowship;
import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FellowshipView extends AppCompatActivity {

    private FellowshipViewModel viewModel;

    private TextView ownerNameTextView, webShopTextView, minimumAmountNeededTextView, paymentMethodTextView, deadlineTextView, distanceTextView;
    private ImageView ownerAvatarView;
    private Button cancelJoinButton;

    private String fellowshipId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FellowshipViewModel.class);

        setContentView(R.layout.activity_fellowship_view);

        ownerNameTextView = findViewById(R.id.ownerNameTextView);
        webShopTextView = findViewById(R.id.webShopTextView);
        minimumAmountNeededTextView = findViewById(R.id.minimumAmountNeededTextView);
        paymentMethodTextView = findViewById(R.id.paymentMethodTextView);
        deadlineTextView = findViewById(R.id.deadlineTextView);
        distanceTextView = findViewById(R.id.distanceTextView);

        ownerAvatarView = findViewById(R.id.ownerAvatarView);

        cancelJoinButton = findViewById(R.id.cancelJoinButton);

        fellowshipId = viewModel.getViewFellowshipInfo().first;

        cancelJoinButton.setOnClickListener((View v) -> {
            onBackPressed();
        });

        setOwnerDetails();

        setDetails();
    }

    private void setDetails() {
        // We get the fellowship details
        Fellowship fs = viewModel.getFellowshipById(fellowshipId);

        webShopTextView.setText(fs.webshop);
        minimumAmountNeededTextView.setText(fs.amountNeeded + " DKK");
        paymentMethodTextView.setText(fs.paymentMethod);
        deadlineTextView.setText(fs.deadline);

        // FIND A WAY TO CALCULATE DISTANCE BETWEEN TWO LATLONG POINTS!!!
        distanceTextView.setText("X Meters");
    }

    private void setOwnerDetails() {
        // We get the owner of the fellowship
        String ownerId = viewModel.getViewFellowshipInfo().second;
        // We get their displayName and avatar URL
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("users").orderByChild("id").equalTo(ownerId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String displayName = ((HashMap<String, String>) issue.getValue()).get("displayName");
                        String imageUrl = ((HashMap<String, String>) issue.getValue()).get("imageUrl");

                        ownerNameTextView.setText(displayName);
                        Glide.with(FellowshipView.this).load(Uri.parse(imageUrl)).into(ownerAvatarView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}