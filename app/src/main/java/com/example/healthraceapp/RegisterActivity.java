package com.example.healthraceapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonRegister, buttonSignIn;
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private ProgressDialog progressDialog;
    private RadioButton radioButtonMale, radioButtonFemale;

    // Defining FirebaseAuth object
    private FirebaseAuth mAuth;

    // Defining FirebaseDatabase object
    private FirebaseDatabase firebaseDatabase;

    // If the date of birth is set
    private boolean dateOfBirthSet;

    // Date of birth
    private int year, month, day;

    private static final String TAG = "RegisterActivity";

    // Used for checking username uniqueness
    private final ArrayList<String> allUsernames = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Registration");

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        buttonRegister.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);

        radioButtonMale = (RadioButton) findViewById(R.id.radioButtonMale);
        radioButtonFemale = (RadioButton) findViewById(R.id.radioButtonFemale);

        //gets current user state and whether they are logged in or not
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            // User is signed in
//            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(i);
//        }

    }

    private void retrieveAllUsernames() {
        // Defining DatabaseReference object
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://health-" +
                "race-app-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String temporaryUsername = dataSnapshot.child("username").getValue(String.class);
                    assert temporaryUsername != null;
                    if (!allUsernames.contains(temporaryUsername)) {
                        // Adding all unique usernames to the ArrayList allUsernames
                        allUsernames.add(temporaryUsername);
                    }
                }
                // After retrieving all the usernames the user can now be registerd
                registerUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting data was canceled
                Log.w(TAG, "onCancelled", error.toException());
            }
        });
    }

    private void registerUser() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if (allUsernames.contains(username)) {
            Toast.makeText(this, "Username is already in use", Toast.LENGTH_LONG).show();
            return;
        }

        // Checking if username, email and passwords are empty
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, "Please enter your password longer than 6 characters", Toast.LENGTH_LONG).show();
            return;
        }

        // Checking if a date of birth was set
        if (!dateOfBirthSet) {
            Toast.makeText(this, "Please set the date of birth", Toast.LENGTH_LONG).show();
            return;
        }

        boolean checkedMale = ((RadioButton) radioButtonMale).isChecked();
        boolean checkedFemale = ((RadioButton) radioButtonFemale).isChecked();

        //Check if a gender was selected
        if (!checkedMale && !checkedFemale) {
            Toast.makeText(this, "Please select the gender", Toast.LENGTH_LONG).show();
            return;
        }

        if (username.length() > 16){
            Toast.makeText(this, "Username has to be less than 16 characters", Toast.LENGTH_LONG).show();
            return;
        }

        // If the none of the fields are empty displaying a progress dialog
        {
            progressDialog.setMessage("Registering Please Wait...");
        }
        progressDialog.show();



        // Creating a new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // If the creating a Firebase user has been successful, then create
                            // a User object
                            User user = new User(username, email, checkedMale, year, month, day);

                            firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
                                    "race-app-default-rtdb.europe-west1.firebasedatabase.app/");

                            // Add the additional attributes to the Firebase user
                            firebaseDatabase.getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // If the attributes have been added then send a email
                                        // address verification email
                                        Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(RegisterActivity.this,
                                                            "Registered successfully." +
                                                                    " Please check your email and verify the account",
                                                            Toast.LENGTH_LONG).show();
                                                } else {
                                                    // If the email has not been sent - show error
                                                    Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        // If the additional attributes were not added to the user -
                                        // show error
                                        Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            // If the Firebase user could not be created - show error
                            Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        // Ending progress dialog
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSignIn){
            // Signing out the user so he could sign in again
            mAuth.signOut();
            // Finishing registration activity
            finish();
            // Starting a login activity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }

        if(v == buttonRegister){
            // Retrieving all usernames from the database to check if the username is unique
            // then calling the method registerUser() if username is unique
            registerUser();
        }
    }

    public void setDateOfBirth(int year, int month, int day) {
        dateOfBirthSet = true;
        this.year = year;
        this.month = month + 1;
        this.day = day;
    }

    // Date picker dialog is shown when the button 'set' is pressed in the register activity
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setCurrentActivity(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}