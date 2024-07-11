package com.example.quanlichitieu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quanlichitieu.R;
import com.example.quanlichitieu.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edMail,edPassword, edRePassword;
    private Button btCancel,btSignup;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        edMail = findViewById(R.id.edMail);
        edPassword = findViewById(R.id.edPassword);
        edRePassword = findViewById(R.id.edRePassword);
        btCancel = findViewById(R.id.btHuy);
        btSignup = findViewById(R.id.btSignup);
        btCancel.setOnClickListener(this);
        btSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btCancel) {
            finish();
        }else if(view==btSignup){
            loading(true);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String email = edMail.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
            checkSignup(email,password);
        }
    }

    private void checkSignup(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(registerTask -> {
                    if (registerTask.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(getApplicationContext(), "Registration successfully", Toast.LENGTH_SHORT).show();
                            saveUser(email);
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
        loading(false);
    }

    private void saveUser(String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://expensemanagement-8f058-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference userRef = database.getReference().child("Users");
        String transactionKey = userRef.push().getKey();
        User user = new User();
        user.setId(transactionKey);
        user.setEmail(email);
        int length = email.length();
        String [] list = email.split("@");
        user.setFullName(list[0]);
        userRef.child(transactionKey).setValue(user)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            btCancel.setVisibility(View.INVISIBLE);
            btSignup.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            btCancel.setVisibility(View.VISIBLE);
            btSignup.setVisibility(View.VISIBLE);
        }
    }
}