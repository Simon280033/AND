package com.example.andproject.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.andproject.R;
import com.example.andproject.ViewModel.SpashScreenViewModel;

public class SplashScreenActivity extends AppCompatActivity {
    private SpashScreenViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SpashScreenViewModel.class);

        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                goToSignIn();
            }
        }, 2500);
    }

    private void goToSignIn() {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }
}