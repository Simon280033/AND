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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.andproject.Entities.Fellowship;
import com.example.andproject.Entities.FellowshipItemAdapter;
import com.example.andproject.Entities.JoinedFellowshipItemAdapter;
import com.example.andproject.R;
import com.example.andproject.ViewModel.FindFellowshipsViewModel;
import com.google.android.material.textfield.TextInputEditText;
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

    private Spinner webShopFilterSpinner, categoryFilterSpinner;
    private TextInputEditText amountFilterTextEdit, distanceFilterTextEdit;
    private Button refreshButton;

    private ListView fellowshipsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FindFellowshipsViewModel.class);

        setContentView(R.layout.activity_find_fellowships);

        findViews();

        setSpinnerSelectionActions();

        setUi();

        refreshButton.setOnClickListener((View v) -> {
            applyFilters();
        });
    }

    private void applyFilters() {
        // We get the filters
        String webShop = webShopFilterSpinner.getSelectedItem().toString();
        String category = categoryFilterSpinner.getSelectedItem().toString();
        int amount = -1;
        if(amountFilterTextEdit.getText().toString().trim().length() != 0) {
            amount = Integer.parseInt(amountFilterTextEdit.getText().toString());
        }
        int distance = -1;
        if(distanceFilterTextEdit.getText().toString().trim().length() != 0) {
            distance = Integer.parseInt(distanceFilterTextEdit.getText().toString());
        }

        viewModel.refreshWithFilters(webShop, category, amount, distance);
    }

    private void findViews() {
        fellowshipsList = findViewById(R.id.fellowshipsList);
        webShopFilterSpinner = findViewById(R.id.webShopFilterSpinner);
        categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner);
        amountFilterTextEdit = findViewById(R.id.amountFilterTextEdit);
        distanceFilterTextEdit = findViewById(R.id.distanceFilterTextEdit);
        refreshButton = findViewById(R.id.refreshButton);
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
        // We bind the listview for our own Fellowships
        final Observer<ArrayList<Pair<Fellowship, String>>> fellowshipsObserver = new Observer<ArrayList<Pair<Fellowship, String>>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<Pair<Fellowship, String>> newValue) {
                FellowshipItemAdapter madb = new FellowshipItemAdapter(FindFellowshipsActivity.this, 0, newValue);

                fellowshipsList.setAdapter(madb);
            }
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getFellowshipsList().observe(this, fellowshipsObserver);

        // We bind the categories spinner
        final Observer<ArrayList<String>> categoriesObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newValue) {
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(FindFellowshipsActivity.this, android.R.layout.simple_spinner_item, newValue);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                categoryFilterSpinner.setAdapter(dataAdapter);
            }
        };

        viewModel.getCategoriesList().observe(this, categoriesObserver);

        // We bind the webshops spinner
        final Observer<ArrayList<String>> webShopsObserver = new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<String> newValue) {
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(FindFellowshipsActivity.this, android.R.layout.simple_spinner_item, newValue);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                webShopFilterSpinner.setAdapter(dataAdapter);
            }
        };

        viewModel.getWebShopsList().observe(this, webShopsObserver);
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