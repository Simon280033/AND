package com.example.andproject.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.bumptech.glide.Glide;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FellowshipsViewModel.class);

        setContentView(R.layout.activity_fellowships);

        findViews();

        setButtonMethods();

        setSpinnerSelectionActions();

        setUi();
    }

    private void setButtonMethods() {
        newFellowshipButton.setOnClickListener((View v) -> {
            goToNewFellowship();
        });

        findFellowshipsButton.setOnClickListener((View v) -> {
            goToFindFellowships();
        });
    }

    private void findViews() {
        newFellowshipButton = findViewById(R.id.newFellowshipButton);
        findFellowshipsButton = findViewById(R.id.findFellowshipsButton);
        myFellowshipsList = findViewById(R.id.myFellowshipsList);
        joinedFellowshipsList = findViewById(R.id.joinedFellowshipsList);
    }

    private void setSpinnerSelectionActions() {
        // When we select one of our joined fellowships
        joinedFellowshipsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // We set the ID of the fellowship we want to view in the model
                viewModel.setViewFellowshipInfo(viewModel.getJoinedFellowshipAt(position));
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
                viewModel.setViewFellowshipInfo(viewModel.getOwnFellowshipAt(position));
                // We check if a partner has already been accepted
                if (!viewModel.getOwnFellowshipAt(position).partnerId.equals("null")) {
                    // We go to the view page for the fellowship...
                    goToFellowship();
                } else {
                    // We go to the fellowship requests activity
                    goToFellowshipRequests();
                }
            }
        });
    }

    private void setUi() {
        // We bind the UI elements
        bindUiElements();
        // We refresh them
        viewModel.refreshLists();
    }

    // This method binds the View's UI elements to the properties in the viewmodel
    private void bindUiElements() {
        // We bind the spinner for our own Fellowships
        final Observer<ArrayList<String>> ownFellowshipsObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newValue) {

                //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(FellowshipsActivity.this,
                        android.R.layout.simple_list_item_1,
                        newValue);

                myFellowshipsList.setAdapter(adapter);

            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getYourFellowshipsList().observe(this, ownFellowshipsObserver);

        // We bind the spinner for our joined Fellowships
        final Observer<ArrayList<String>> joinedFellowshipsObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newValue) {

                //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(FellowshipsActivity.this,
                        android.R.layout.simple_list_item_1,
                        newValue);

                joinedFellowshipsList.setAdapter(adapter);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getJoinedFellowshipsList().observe(this, joinedFellowshipsObserver);
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