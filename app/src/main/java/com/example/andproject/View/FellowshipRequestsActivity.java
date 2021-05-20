package com.example.andproject.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
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
// This activity handles the view for when a user wants an overview of the users requesting to join a particular FellowShip
public class FellowshipRequestsActivity extends AppCompatActivity {
    private FellowshipRequestViewModel viewModel;

    private ListView requestingUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FellowshipRequestViewModel.class);
        setContentView(R.layout.activity_fellowship_requests);

        requestingUsersList = findViewById(R.id.requestingUsersList);

        bindUiElements();

        viewModel.refreshList();

        // When we select one of the requests
        requestingUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                showOptionsForSelectedRequest(viewModel.getUsers().get(viewModel.getUserId(position)));
            }
        });
    }

    // This method binds the View's UI elements to the properties in the viewmodel
    private void bindUiElements() {
        // We bind the spinner for the fellowship requests
        final Observer<ArrayList<String>> requestsObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newValue) {

                //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(FellowshipRequestsActivity.this,
                        android.R.layout.simple_list_item_1,
                        newValue);

                requestingUsersList.setAdapter(adapter);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getRequestsOverviewList().observe(this, requestsObserver);
    }

    private void showOptionsForSelectedRequest(User user) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle("Request options");

        alertDialog.setMessage("Actions for Fellowship request by '" + user.getDisplayName() + "':");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept request", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                viewModel.acceptRequestFromUser(user);
                Toast.makeText(FellowshipRequestsActivity.this, "Successfully accepted Fellowship request!",
                        Toast.LENGTH_LONG).show();
                goToFellowshipsView();
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

    private void goToProfileView() {
        startActivity(new Intent(this, ProfileViewActivity.class));
    }

    private void goToFellowshipsView() {
        startActivity(new Intent(this, FellowshipsActivity.class));
        finish();
    }


}