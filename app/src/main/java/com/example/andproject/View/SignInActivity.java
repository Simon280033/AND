package com.example.andproject.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.andproject.R;
import com.example.andproject.ViewModel.SignInViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUserMetadata;

import java.util.Arrays;
import java.util.List;
// This activity acts as a view for the sign-in process
public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 42; // <- figure out what this is
    private SignInViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        viewModel.setUserLocation(this);

        checkIfSignedIn();
        setContentView(R.layout.signin_activity);
    }

    private void checkIfSignedIn() {
        viewModel.getCurrentUser().observe(this, user -> {
            // We check if a user was returned
            if (user != null) {
                if (isNewUser()) {
                    System.out.println("læs: new user");
                    goToProfileEditor();
                } else {
                    System.out.println("læs: returning user");
                    viewModel.setCustomUserData();
                    goToMainActivity();
                }
            }
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void goToProfileEditor() {
        startActivity(new Intent(this, ProfileEditorActivity.class));
        finish();
    }

    private void goToProfileView() {
        startActivity(new Intent(this, ProfileViewActivity.class));
        finish();
    }

    public void signIn(View v) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .build();

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInRequest(resultCode);
        }
    }

    private void handleSignInRequest(int resultCode) {
        if (resultCode == RESULT_OK)
            goToMainActivity();
        else
            Toast.makeText(this, "SIGN IN CANCELLED", Toast.LENGTH_SHORT).show();
    }

    private boolean isNewUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUserMetadata metadata = auth.getCurrentUser().getMetadata();
        if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
            // The user is new
            return true;
        } else {
            // This is an existing user
            return false;
        }
    }
}
