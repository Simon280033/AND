package com.example.andproject.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
// This activity acts as a view for the main menu. This is where a logged in user starts, and where they can navigate to the other views
public class MainActivity extends AppCompatActivity {
    private MainActivityViewModel viewModel;

    private Button viewProfileButton, fellowshipsButton, signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.init();
        checkIfSignedIn();
        setContentView(R.layout.activity_main);

        createLocationRequest();
        createStorageRequest();

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

    public void createStorageRequest() {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public void createLocationRequest() {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
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
