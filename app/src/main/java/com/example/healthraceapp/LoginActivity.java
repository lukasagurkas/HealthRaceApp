package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister, buttonSignIn;
    private EditText editTextEmail, editTextPassword;
    private ProgressDialog progressDialog;
    private TextView textForgotPassword;

    // Defining FirebaseAuth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Login");

        // Defining FirebaseAuth object
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // If the FirebaseAuth object getCurrentUser() method is not null then the user is already
        // logged in
        if (mAuth.getCurrentUser() != null) {
            // Close this activity
            finish();
            // Opening profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        // Initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        TextView textForgotPassword = (TextView) findViewById(R.id.textForgotPassword);

        progressDialog = new ProgressDialog(this);

        // Attaching click listener
        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PopupForgotPassword.class));
            }
        });
        buttonSignIn.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

    }

    // Method for user login
    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        // Checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, "Please enter password longer than 6 characters",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // If the email and password are not empty and the password length is of 6 or more
        // characters then display a progress dialog
        progressDialog.setMessage("Signing In Please Wait...");
        progressDialog.show();

        //logging in the user
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // If the user has been signed in
                            checkIfEmailVerified();
                        } else {
                            // If the user has not been signed in show exception
                            Toast.makeText(LoginActivity.this,
                                    Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (user.isEmailVerified()) {
            // If the users email has been verified start the main page
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            Toast.makeText(LoginActivity.this,
                    "Please verify your email address",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void onClick(View view) {
        if (view == buttonSignIn) {
            userLogin();
        }

        if (view == buttonRegister) {
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}