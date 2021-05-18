package com.example.andproject.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.FellowshipItemAdapter;
import com.example.andproject.Entities.JoinedFellowshipItemAdapter;
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

import static java.nio.file.Paths.get;

public class FindFellowshipsActivity extends AppCompatActivity {
    private FindFellowshipsViewModel viewModel;

    private ListView fellowshipsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FindFellowshipsViewModel.class);

        setContentView(R.layout.activity_find_fellowships);

        viewModel.getUsersLocation(this);

        fellowshipsList = findViewById(R.id.fellowshipsList);

        setSpinnerSelectionActions();

        setUi();
    }

    // We refresh the list of joinables upon resumption
    @Override
    public void onResume(){
        super.onResume();
        // We refresh them
        viewModel.refreshFellowships();
    }

    private void setUi() {
        // We bind the UI elements
        bindUiElements();
        // We refresh them
        viewModel.refreshFellowships();
    }

    // This method binds the View's UI elements to the properties in the viewmodel
    private void bindUiElements() {
        // We bind the spinner for our own Fellowships
        final Observer<ArrayList<Pair<Fellowship, String>>> fellowshipsObserver = new Observer<ArrayList<Pair<Fellowship, String>>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<Pair<Fellowship, String>> newValue) {
                FellowshipItemAdapter madb = new FellowshipItemAdapter(FindFellowshipsActivity.this, 0, newValue);

                fellowshipsList.setAdapter(madb);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getFellowshipsList().observe(this, fellowshipsObserver);
    }

    private void setSpinnerSelectionActions() {
        // We get the ID of the fellowship selected
        fellowshipsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // We set the ID of the fellowship we want to view in the model
                System.out.println("læs: " + viewModel.getFellowshipAt(position).amountNeeded);
                viewModel.setViewFellowshipInfo(viewModel.getFellowshipAt(position));
                goToFellowship();
            }
        });
    }

    private void goToFellowship() {
        startActivity(new Intent(this, FellowshipJoinActivity.class));
    }

}