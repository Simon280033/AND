package com.example.andproject.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipsViewModel;
import com.example.andproject.ViewModel.MainActivityViewModel;

public class FellowshipsActivity extends AppCompatActivity {
    private FellowshipsViewModel viewModel;

    private Button newFellowshipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FellowshipsViewModel.class);
        //viewModel.init();
        setContentView(R.layout.activity_fellow_ships);

        newFellowshipButton = findViewById(R.id.newFellowshipButton);

        newFellowshipButton.setOnClickListener((View v) -> {
            goToNewFellowship();
        });
    }

    private void goToNewFellowship() {
        startActivity(new Intent(this, NewFellowshipActivity.class));
    }
}