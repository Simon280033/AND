package com.example.andproject.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.JoinedFellowshipItemAdapter;
import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipsViewModel;

import java.util.ArrayList;
// This activity acts as a view for the overview of the user's current owned/joined activities
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
                if (!viewModel.getOwnFellowshipAt(position).getPartnerId().equals("null")) {
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
        // We bind the listview for our own Fellowships
        final Observer<ArrayList<Fellowship>> ownFellowshipsObserver = new Observer<ArrayList<Fellowship>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<Fellowship> newValue) {
                JoinedFellowshipItemAdapter madb = new JoinedFellowshipItemAdapter(FellowshipsActivity.this, 0, newValue);

                myFellowshipsList.setAdapter(madb);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getYourFellowshipsList().observe(this, ownFellowshipsObserver);

        // We bind the listview for our joined Fellowships
        final Observer<ArrayList<Fellowship>> joinedFellowshipsObserver = new Observer<ArrayList<Fellowship>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<Fellowship> newValue) {
                JoinedFellowshipItemAdapter madb = new JoinedFellowshipItemAdapter(FellowshipsActivity.this, 0, newValue);
                joinedFellowshipsList.setAdapter(madb);
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