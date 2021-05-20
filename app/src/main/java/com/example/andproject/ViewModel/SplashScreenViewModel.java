package com.example.andproject.ViewModel;

import android.app.Application;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.andproject.Entities.User;
import com.example.andproject.Model.Model;
import com.google.firebase.auth.FirebaseUser;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
// This viewmodel determines what is being shown on the splashscreen view
public class SplashScreenViewModel extends AndroidViewModel {
    private final Model model;

    public SplashScreenViewModel(Application app){
        super(app);
        model = Model.getInstance(app);
    }
}
