package com.example.andproject.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.andproject.R;
import com.example.andproject.ViewModel.FellowshipViewModel;
import com.example.andproject.ViewModel.FellowshipsViewModel;

public class FellowshipView extends AppCompatActivity {

    private FellowshipViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FellowshipViewModel.class);

        setContentView(R.layout.activity_fellowship_view);
    }
}