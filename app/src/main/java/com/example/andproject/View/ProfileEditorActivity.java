package com.example.andproject.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.andproject.R;
import com.example.andproject.ViewModel.ProfileEditorViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class ProfileEditorActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 42;
    private ProfileEditorViewModel viewModel;

    private Button saveButton;
    private TextInputEditText textInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("profile editor activity");
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileEditorViewModel.class);
        setContentView(R.layout.profile_editor_activity);

        // We set the UI components
        saveButton = findViewById(R.id.saveButton);
        textInput = findViewById(R.id.textInput);

        setUserDetails();

        // We set button methods
        saveButton.setOnClickListener((View v) -> {
            goToMainActivity();
        });
    }

    private void setUserDetails()  {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        textInput.setText(user.getEmail());
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
