package com.example.healthraceapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonRegister;
    private Button buttonSignIn;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;

    // Defining FirebaseAuth object
    private FirebaseAuth firebaseAuth;

    // Defining FirebaseDatabase object
    private FirebaseDatabase firebaseDatabase;

    // If the date of birth is set
    private boolean dateOfBirthSet;

    // Date of birth
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

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
    }

    private void registerUser() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Checking if username, email and passwords are empty
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show();
            return;
        }

        if (!dateOfBirthSet) {
            Toast.makeText(this, "Please set the date of birth", Toast.LENGTH_LONG).show();
            return;
        }

        boolean checkedMale = ((RadioButton) radioButtonMale).isChecked();
        boolean checkedFemale = ((RadioButton) radioButtonFemale).isChecked();

        if (!checkedMale && !checkedFemale) {
            Toast.makeText(this, "Please select the gender", Toast.LENGTH_LONG).show();
            return;
        }

        // If the email and password are not empty
        // displaying a progress dialog
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        // Creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(username, email, checkedMale, year, month, day);

                            firebaseDatabase = FirebaseDatabase.getInstance("https://health-race-app-default-rtdb.europe-west1.firebasedatabase.app/");

                            firebaseDatabase.getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Successfully registered",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
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
    public void onClick(View view) {
        registerUser();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setCurrentActivity(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}