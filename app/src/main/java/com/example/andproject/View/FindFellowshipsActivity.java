package com.example.andproject.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipViewModel;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FindFellowshipsViewModel.class);

        setContentView(R.layout.activity_find_fellowships);

        fellowshipsList = findViewById(R.id.fellowshipsList);

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
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // We check if it is our own fellowship, if it is, we don't add it to the list
                        if (!(((HashMap<String, String>) issue.getValue()).get("creatorId").equals(viewModel.getCurrentUser().getValue().getUid()))) {
                            String webShop = ((HashMap<String, String>) issue.getValue()).get("webshop");
                            Long amountNeeded = ((HashMap<String, Long>) issue.getValue()).get("amountNeeded");

                            listItems.add("Web shop:" + webShop + ", amount needed: " + amountNeeded + " DKK");
                        }
                    }
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
}