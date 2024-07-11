package com.example.quanlichitieu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quanlichitieu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edMail,edPassword;
    private Button btSignin,btSignup;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        edMail = findViewById(R.id.edMail);
        edPassword = findViewById(R.id.edPassword);
        btSignin = findViewById(R.id.btSignin);
        btSignup = findViewById(R.id.btSignup);
        btSignin.setOnClickListener(this);
        btSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==btSignin) {
            if (isValidSignInDetails()) {
                signIn();
            }
        }else if(view==btSignup){
            Intent intent = new Intent(this,SignupActivity.class);
            startActivity(intent);
        }
    }

    private boolean isValidSignInDetails() {
        if (edMail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (edPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        } else {
            return true;
        }
    }
    private void signIn() {
        loading(true);
        String email = edMail.getText().toString();
        String password = edPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("email", email);
                            editor.putString("password",password);
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        loading(false);
                    }
                });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void loading(boolean isLoading) {
        if (isLoading) {
            btSignin.setVisibility(View.INVISIBLE);
            btSignup.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            btSignin.setVisibility(View.VISIBLE);
            btSignup.setVisibility(View.VISIBLE);
        }
    }
}