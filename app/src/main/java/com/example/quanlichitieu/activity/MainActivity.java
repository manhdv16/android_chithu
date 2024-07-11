package com.example.quanlichitieu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.databinding.ActivityMainBinding;
import com.example.quanlichitieu.fragment.CustomDialog;
import com.example.quanlichitieu.fragment.FragmentChart;
import com.example.quanlichitieu.fragment.FragmentHome;
import com.example.quanlichitieu.fragment.FragmentSetting;
import com.example.quanlichitieu.fragment.FragmentSearch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FloatingActionButton fab;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        fab = findViewById(R.id.fab);
        setContentView(binding.getRoot());
        replaceFragment(new FragmentHome());
        binding.bottomNavigationView.setBackground(null);
        sharedPreferences = getSharedPreferences("MyPrefs", this.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            replaceFragment(new FragmentHome());
        } else {
            goToSignInActivity();
        }
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mHome:
                    replaceFragment(new FragmentHome());
                    break;
                case R.id.mChart:
                    replaceFragment(new FragmentChart());
                    break;
                case R.id.mStatistic:
                    replaceFragment(new FragmentSearch());
                    break;
                case R.id.mSetting:
                    replaceFragment(new FragmentSetting());
                    break;
            }
            return true;
        });
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog();
                customDialog.show(getSupportFragmentManager(), "CustomDialog");

            }
        });

    }
    private void goToSignInActivity() {
        finishAffinity();
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}