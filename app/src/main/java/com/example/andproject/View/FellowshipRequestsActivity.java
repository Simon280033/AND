package com.example.andproject.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.andproject.Entities.User;
import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipRequestViewModel;
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

    private ArrayList<String> userIds;
    private HashMap<String, User> users;
    private HashMap<String, String> requestIdByUserId;

    private ListView requestingUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FellowshipRequestViewModel.class);
        setContentView(R.layout.activity_fellowship_requests);

        userIds = new ArrayList<>();
        users = new HashMap<>();
        requestIdByUserId = new HashMap<>();

        requestingUsersList = findViewById(R.id.requestingUsersList);

        getUsernamesByIds();

        // When we select one of the requests
        requestingUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                showOptionsForSelectedRequest(users.get(userIds.get(position)));
            }
        });
    }

    private void showOptionsForSelectedRequest(User user) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Request options");

        alertDialog.setMessage("Actions for Fellowship request by '" + user.displayName + "':");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept request", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                acceptRequestFromUser(user);
                Toast.makeText(FellowshipRequestsActivity.this, "Successfully accepted Fellowship request!",
                        Toast.LENGTH_LONG).show();
                onBackPressed();
            } });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "View user's profile", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // We set the info of the profile we are about to view
                viewModel.setViewProfileOf(user);
                // Then we change to it
                goToProfileView();
            }});

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //...
            }});

        alertDialog.show();
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
                        String imageUrl = ((HashMap<String, String>) issue.getValue()).get("imageUrl");
                        String email = ((HashMap<String, String>) issue.getValue()).get("email");

                        users.put(userId, new User(userId, displayName, imageUrl, email));
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

        Query query = myRef.child("fellowshipRequests").orderByChild("fellowshipId").equalTo(viewModel.getViewFellowshipInfo().id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
                    ArrayList<String> listItems=new ArrayList<String>();
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        String requestId = ((HashMap<String, String>) issue.getValue()).get("requestId");
                        String requesterId = ((HashMap<String, String>) issue.getValue()).get("requesterId");
                        String requesterName = users.get(requesterId).displayName;

                        listItems.add(requesterName);
                        userIds.add(requesterId);
                        requestIdByUserId.put(requesterId, requestId);
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

    private void acceptRequestFromUser(User user) {
        // We mark the request as accepted
        String requestId = requestIdByUserId.get(user.id);

        DatabaseReference myRef = FirebaseDatabase.getInstance("https://fellowshippers-aec83-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        myRef.child("fellowshipRequests").child(requestId).child("isAccepted").setValue(1);

        // We set the partner ID in the Fellowship table
        myRef.child("fellowships").child(viewModel.getViewFellowshipInfo().id).child("partnerId").setValue(user.id);
    }

    private void goToProfileView() {
        startActivity(new Intent(this, ProfileViewActivity.class));
    }
}