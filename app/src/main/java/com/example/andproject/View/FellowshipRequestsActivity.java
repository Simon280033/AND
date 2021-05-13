package com.example.andproject.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipRequestViewModel;
import com.example.andproject.ViewModel.FellowshipsViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FellowshipRequestsActivity extends AppCompatActivity {
    private FellowshipRequestViewModel viewModel;

    private HashMap<String, String> userIdsAndNames;

    private ListView requestingUsersList;

    private ArrayList<String> requestingUsersIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FellowshipRequestViewModel.class);
        setContentView(R.layout.activity_fellowship_requests);

        requestingUsersIds = new ArrayList<>();
        userIdsAndNames = new HashMap<>();

        requestingUsersList = findViewById(R.id.requestingUsersList);

        getUsernamesByIds();
    }

    private void getUsernamesByIds() {
        System.out.println("læs: getting names");

        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("users");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String userId = ((HashMap<String, String>) issue.getValue()).get("id");
                        String displayName = ((HashMap<String, String>) issue.getValue()).get("displayName");

                        userIdsAndNames.put(userId, displayName);
                    }
                    // After we have gotten the list of ids/names, we get the requests for the fellowship
                    getRequestsForFellowship();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getRequestsForFellowship() {
        System.out.println("læs: getting");
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Query query = myRef.child("fellowships").orderByChild("id").equalTo(viewModel.getViewFellowshipInfo().first);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<String> listItems=new ArrayList<String>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {

                        String ownerId = ((HashMap<String, String>) issue.getValue()).get("creatorId");
                        System.out.println("læs: " + ownerId);

                        String ownerName = userIdsAndNames.get(ownerId);
                        listItems.add(ownerName);
                    }
                    System.out.println("læs: " + listItems.size());

                    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
                    ArrayAdapter<String> adapter =new ArrayAdapter<String>(FellowshipRequestsActivity.this,
                            android.R.layout.simple_list_item_1,
                            listItems);

                    requestingUsersList.setAdapter(adapter);
                } else {
                    System.out.println("læs: not getting lol");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}