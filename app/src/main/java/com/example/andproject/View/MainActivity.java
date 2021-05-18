package com.example.andproject.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.andproject.Entities.User;
import com.example.andproject.R;
import com.example.andproject.ViewModel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {
    private MainActivityViewModel viewModel;

    private Button viewProfileButton, fellowshipsButton, signOutButton;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.init();
        checkIfSignedIn();
        setContentView(R.layout.activity_main);

        viewProfileButton = findViewById(R.id.profileButton);
        fellowshipsButton = findViewById(R.id.fellowshipsButton);
        signOutButton = findViewById(R.id.signOutButton);

        fellowshipsButton.setOnClickListener((View v) -> {
            goToFellowships();
        });

        viewProfileButton.setOnClickListener((View v) -> {
            goToProfileView();
        });

        signOutButton.setOnClickListener((View v) -> {
            signOut();
        });
    }

    private void checkIfSignedIn() {
        viewModel.getCurrentUser().observe(this, user -> {
            if (user == null) {
                startLoginActivity();
            }
        });
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    private void goToProfileView() {
        viewModel.setViewProfileOf(new User(viewModel.getCurrentUser().getValue().getUid(), null, null, null));
        startActivity(new Intent(this, ProfileViewActivity.class));
    }

    private void goToFellowships() {
        startActivity(new Intent(this, FellowshipsActivity.class));
    }

    public void signOut() {
        viewModel.signOut();
    }
}
