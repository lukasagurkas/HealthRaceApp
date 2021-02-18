package com.example.healthraceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        buttonRegister.setOnClickListener(this);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        radioButtonMale = (RadioButton) findViewById(R.id.radioButtonMale);
        radioButtonFemale = (RadioButton) findViewById(R.id.radioButtonFemale);
    }

    private void registerUser() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

//        Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users")
//                .orderByChild("username").equalTo("username");
//        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getChildrenCount() > 0){
//                    Toast.makeText(RegisterActivity.this,
//                            "Username not available, please choose another username",
//                            Toast.LENGTH_SHORT).show();
//                }else{
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


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
                            User user = new User(username, email, checkedMale, year, month, day);

                            firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
                                    "race-app-default-rtdb.europe-west1.firebasedatabase.app/");

                            firebaseDatabase.getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Successfully registered",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    public void setDateOfBirth(int year, int month, int day) {
        dateOfBirthSet = true;
        this.year = year;
        this.month = month;
        this.day = day;
    }


    @Override
    public void onClick(View v) {
        registerUser();
    }


    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setCurrentActivity(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}