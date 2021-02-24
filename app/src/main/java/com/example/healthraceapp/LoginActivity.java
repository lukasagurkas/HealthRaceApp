package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
        textForgotPassword = (TextView) findViewById(R.id.textForgotPassword);

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PopupForgotPassword.class));
            }
        });
        buttonSignIn.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);

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

        progressDialog.setMessage("Signing In Please Wait...");
        progressDialog.show();

        //logging in the user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if(task.isSuccessful()){
//                            if(mAuth.getCurrentUser().isEmailVerified()){
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                            }else{
//                                Toast.makeText(LoginActivity.this,
//                                        "Please verify your email before signing in",
//                                        Toast.LENGTH_LONG);
//                            }
                        }else{
                            Toast.makeText(LoginActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG);
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