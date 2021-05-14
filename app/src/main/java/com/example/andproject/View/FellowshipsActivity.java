package com.example.andproject.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipsViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FellowshipsActivity extends AppCompatActivity {
    private FellowshipsViewModel viewModel;

    private Button newFellowshipButton, findFellowshipsButton;

    private ListView myFellowshipsList, joinedFellowshipsList;

    private ArrayList<Fellowship> myFellowships, joinedFellowships;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FellowshipsViewModel.class);

        setContentView(R.layout.activity_fellow_ships);

        myFellowships = new ArrayList<>();
        joinedFellowships = new ArrayList<>();

        newFellowshipButton = findViewById(R.id.newFellowshipButton);
        findFellowshipsButton = findViewById(R.id.findFellowshipsButton);

        myFellowshipsList = findViewById(R.id.myFellowshipsList);

        joinedFellowshipsList = findViewById(R.id.joinedFellowshipsList);

        newFellowshipButton.setOnClickListener((View v) -> {
            goToNewFellowship();
        });

        findFellowshipsButton.setOnClickListener((View v) -> {
            goToFindFellowships();
        });

        // When we select one of our joined fellowships
        joinedFellowshipsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // We set the ID of the fellowship we want to view in the model
                viewModel.setViewFellowshipInfo(joinedFellowships.get(position));
                // We check if a partner has already been accepted
                goToFellowship();
            }
        });

        // When we select one of our own fellowships
        myFellowshipsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // We set the ID of the fellowship we want to view in the model
                viewModel.setViewFellowshipInfo(myFellowships.get(position));
                // We check if a partner has already been accepted
                if (!myFellowships.get(position).partnerId.equals("null")) {
                    // We go to the view page for the fellowship...
                    goToFellowship();
                } else {
                    // We go to the fellowship requests activity
                    goToFellowshipRequests();
                }
            }
        });

        getYourFellowships();
        getJoinedFellowships();
    }

    private void getYourFellowships() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("fellowships").orderByChild("creatorId").equalTo(viewModel.getCurrentUser().getValue().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<String> listItems=new ArrayList<String>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("id");
                        String ownerId = ((HashMap<String, String>) issue.getValue()).get("creatorId");
                        String webShop = ((HashMap<String, String>) issue.getValue()).get("webshop");
                        String category = ((HashMap<String, String>) issue.getValue()).get("category");
                        Long amountNeeded = ((HashMap<String, Long>) issue.getValue()).get("amountNeeded");
                        String paymentMethod = ((HashMap<String, String>) issue.getValue()).get("paymentMethod");
                        String deadline = ((HashMap<String, String>) issue.getValue()).get("deadline");
                        String pickupCoordinates = ((HashMap<String, String>) issue.getValue()).get("pickupCoordinates");
                        String partnerId = ((HashMap<String, String>) issue.getValue()).get("partnerId");
                        Long partnerPaid = ((HashMap<String, Long>) issue.getValue()).get("partnerPaid");
                        Long paymentApproved = ((HashMap<String, Long>) issue.getValue()).get("paymentApproved");
                        String receiptUrl = ((HashMap<String, String>) issue.getValue()).get("receiptUrl");
                        Long ownerCompleted = ((HashMap<String, Long>) issue.getValue()).get("ownerCompleted");
                        Long partnerCompleted = ((HashMap<String, Long>) issue.getValue()).get("partnerCompleted");
                        Long isCompleted = ((HashMap<String, Long>) issue.getValue()).get("isCompleted");

                        listItems.add("Web shop:" + webShop + ", amount needed: " + amountNeeded + " DKK");

                        Fellowship fs = new Fellowship(fellowshipId, ownerId, webShop, category, (int) Integer.parseInt("" + amountNeeded), paymentMethod, deadline, pickupCoordinates, partnerId, (int) Integer.parseInt("" + partnerPaid), (int) Integer.parseInt("" + paymentApproved), receiptUrl, (int) Integer.parseInt("" + ownerCompleted), (int) Integer.parseInt("" + partnerCompleted), (int) Integer.parseInt("" + isCompleted));
                        myFellowships.add(fs);
                    }
                    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
                    ArrayAdapter<String> adapter =new ArrayAdapter<String>(FellowshipsActivity.this,
                            android.R.layout.simple_list_item_1,
                            listItems);

                    myFellowshipsList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getJoinedFellowships() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("fellowships").orderByChild("partnerId").equalTo(viewModel.getCurrentUser().getValue().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<String> listItems=new ArrayList<String>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("id");
                        String ownerId = ((HashMap<String, String>) issue.getValue()).get("creatorId");
                        String webShop = ((HashMap<String, String>) issue.getValue()).get("webshop");
                        String category = ((HashMap<String, String>) issue.getValue()).get("category");
                        Long amountNeeded = ((HashMap<String, Long>) issue.getValue()).get("amountNeeded");
                        String paymentMethod = ((HashMap<String, String>) issue.getValue()).get("paymentMethod");
                        String deadline = ((HashMap<String, String>) issue.getValue()).get("deadline");
                        String pickupCoordinates = ((HashMap<String, String>) issue.getValue()).get("pickupCoordinates");
                        String partnerId = ((HashMap<String, String>) issue.getValue()).get("partnerId");
                        Long partnerPaid = ((HashMap<String, Long>) issue.getValue()).get("partnerPaid");
                        Long paymentApproved = ((HashMap<String, Long>) issue.getValue()).get("paymentApproved");
                        String receiptUrl = ((HashMap<String, String>) issue.getValue()).get("receiptUrl");
                        Long ownerCompleted = ((HashMap<String, Long>) issue.getValue()).get("ownerCompleted");
                        Long partnerCompleted = ((HashMap<String, Long>) issue.getValue()).get("partnerCompleted");
                        Long isCompleted = ((HashMap<String, Long>) issue.getValue()).get("isCompleted");

                        listItems.add("Web shop:" + webShop + ", amount needed: " + amountNeeded + " DKK");

                        Fellowship fs = new Fellowship(fellowshipId, ownerId, webShop, category, (int) Integer.parseInt("" + amountNeeded), paymentMethod, deadline, pickupCoordinates, partnerId, (int) Integer.parseInt("" + partnerPaid), (int) Integer.parseInt("" + paymentApproved), receiptUrl, (int) Integer.parseInt("" + ownerCompleted), (int) Integer.parseInt("" + partnerCompleted), (int) Integer.parseInt("" + isCompleted));
                        joinedFellowships.add(fs);
                    }
                    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
                    ArrayAdapter<String> adapter =new ArrayAdapter<String>(FellowshipsActivity.this,
                            android.R.layout.simple_list_item_1,
                            listItems);

                    joinedFellowshipsList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToNewFellowship() {
        startActivity(new Intent(this, NewFellowshipActivity.class));
    }

    private void goToFindFellowships() {
        startActivity(new Intent(this, FindFellowshipsActivity.class));
    }

    private void goToFellowshipRequests() {
        startActivity(new Intent(this, FellowshipRequestsActivity.class));
    }

    private void goToFellowship() {
        startActivity(new Intent(this, FellowshipActivity.class));
    }
}