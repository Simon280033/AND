package com.example.andproject.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.R;
import com.example.andproject.ViewModel.FindFellowshipsViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FindFellowshipsActivity extends AppCompatActivity {
    private FindFellowshipsViewModel viewModel;

    private ListView fellowshipsList;

    private ArrayList<Pair<String, String>> fellowshipIdsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FindFellowshipsViewModel.class);

        fellowshipIdsList = new ArrayList<>();

        setContentView(R.layout.activity_find_fellowships);

        fellowshipsList = findViewById(R.id.fellowshipsList);

        // We get the ID of the fellowship selected
        fellowshipsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // We set the ID of the fellowship we want to view in the model
                viewModel.setViewFellowshipInfo(fellowshipIdsList.get(position).first, fellowshipIdsList.get(position).second);
                goToFellowship();
            }
        });

        getFellowships();
    }

    private void getFellowships() {
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("fellowships");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<String> listItems=new ArrayList<String>();
                    // We make a hashmap of joinable fellowships for other activities
                    HashMap<String, Fellowship> joinableFellowships = new HashMap<>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // We check if it is our own fellowship, if it is, we don't add it to the list
                        if (!(((HashMap<String, String>) issue.getValue()).get("creatorId").equals(viewModel.getCurrentUser().getValue().getUid()))) {
                            String fellowshipId = ((HashMap<String, String>) issue.getValue()).get("id");
                            String ownerId = ((HashMap<String, String>) issue.getValue()).get("creatorId");
                            String webShop = ((HashMap<String, String>) issue.getValue()).get("webshop");
                            String category = ((HashMap<String, String>) issue.getValue()).get("category");
                            String deadline = ((HashMap<String, String>) issue.getValue()).get("deadline");
                            Long isCompleted = ((HashMap<String, Long>) issue.getValue()).get("isCompleted");
                            String paymentMethod = ((HashMap<String, String>) issue.getValue()).get("paymentMethod");
                            String pickupCoordinates = ((HashMap<String, String>) issue.getValue()).get("pickupCoordinates");
                            Long amountNeeded = ((HashMap<String, Long>) issue.getValue()).get("amountNeeded");

                            Fellowship fs = new Fellowship(fellowshipId, ownerId, webShop, category, Integer.parseInt("" + amountNeeded), paymentMethod, deadline, pickupCoordinates, Integer.parseInt("" + isCompleted));
                            joinableFellowships.put(fellowshipId, fs);

                            Pair<String, String> ids = new Pair<String, String>(fellowshipId, ownerId);
                            fellowshipIdsList.add(ids);
                            listItems.add("Web shop:" + webShop + ", amount needed: " + amountNeeded + " DKK");
                        }
                    }
                    // We set the fellowship hashmap in the model
                    viewModel.setJoinableFellowships(joinableFellowships);
                    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
                    ArrayAdapter<String> adapter =new ArrayAdapter<String>(FindFellowshipsActivity.this,
                            android.R.layout.simple_list_item_1,
                            listItems);

                    fellowshipsList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToFellowship() {
        startActivity(new Intent(this, FellowshipView.class));
    }

}