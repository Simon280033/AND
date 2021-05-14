package com.example.andproject.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipViewModel;
import com.example.andproject.ViewModel.FindFellowshipsViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FellowshipActivity extends AppCompatActivity {
    private FellowshipViewModel viewModel;

    private boolean ownerOfFellowship;

    private ImageView partnerAvatarView;

    private TextView partnerNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FellowshipViewModel.class);

        setContentView(R.layout.activity_fellowship);

        // We start out by checking whether or not the user is the owner of the fellowship
        if (viewModel.getViewFellowshipInfo().creatorId.equals(viewModel.getCurrentUserData().getValue().getUid())) {
            ownerOfFellowship = true;
        } else {
            ownerOfFellowship = false;
        }

        partnerAvatarView = findViewById(R.id.partnerAvatarView);
        partnerNameTextView = findViewById(R.id.partnerNameTextView);

        setPartnerInfo();
    }

    private void setPartnerInfo() {
        // We get the partner of the fellowship
        String partnerId = viewModel.getViewFellowshipInfo().creatorId;
        if (ownerOfFellowship) {
            partnerId = viewModel.getViewFellowshipInfo().partnerId;
        }
        // We get their displayName and avatar URL
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("users").orderByChild("id").equalTo(partnerId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String displayName = ((HashMap<String, String>) issue.getValue()).get("displayName");
                        String imageUrl = ((HashMap<String, String>) issue.getValue()).get("imageUrl");

                        partnerNameTextView.setText(displayName);
                        Glide.with(FellowshipActivity.this).load(Uri.parse(imageUrl)).into(partnerAvatarView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}