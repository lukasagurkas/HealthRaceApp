package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister, buttonSignIn;
    private EditText editTextEmail, editTextPassword;
    private ProgressDialog progressDialog;
    private TextView textForgotPassword;

    // Defining FirebaseAuth object
    private FirebaseAuth mAuth;

    // If permission is granted
    private int PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getting firebase auth object
        mAuth = FirebaseAuth.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in
        if(mAuth.getCurrentUser() != null){
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonRegister  = (Button) findViewById(R.id.buttonRegister);

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

        // Ask for permission to use activity recognition
        Button buttonRequest = findViewById(R.id.buttonSignIn);
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(LoginActivity.this,
                        Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LoginActivity.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
                } else {
                    requestActivity();
                }
            }
        });
    }

    // Requests permission to use step counter
    private void requestActivity() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to track the amount of steps you take")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_CODE);
        }
        ;
    }

    //method for user login
    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();


        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password) || password.length() < 6){
            Toast.makeText(this,"Please enter password longer than 6 characters",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //logging in the user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if(task.isSuccessful()){
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                });

    }

    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();
        }

        if(view == buttonRegister){
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}