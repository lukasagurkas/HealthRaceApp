package com.example.healthraceapp;

import android.Manifest;
import android.app.Activity;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.security.acl.Group;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth mAuth;

    //view objects
    private TextView textViewUserEmail;
//    private TextView stepText;
    private Button buttonLogout, buttonStep, buttonFruit, buttonProfile, buttonVeggie, buttonWater, buttonGroup;

    // If permission is granted
    private int PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Home Page");

        //initializing firebase authentication object
        mAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(mAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
//        FirebaseUser user = mAuth.getCurrentUser();

        //initializing views
//        buttonLogout = findViewById(R.id.buttonLogout);

        buttonStep =  findViewById(R.id.buttonStep);
//        stepText = findViewById(R.id.stepText);

        buttonFruit = findViewById(R.id.buttonFruit);

        buttonProfile = findViewById(R.id.buttonProfile);

        buttonVeggie = findViewById(R.id.buttonVeggie);

        buttonWater = findViewById(R.id.buttonWater);

        buttonGroup = findViewById(R.id.buttonGroup);

        //displaying logged in user name
//        textViewUserEmail.setText("Welcome "+ user.getEmail());

        //adding listener to button
//        buttonLogout.setOnClickListener(this);
        buttonStep.setOnClickListener(this);
        buttonFruit.setOnClickListener(this);
        buttonProfile.setOnClickListener(this);
        buttonVeggie.setOnClickListener(this);
        buttonWater.setOnClickListener(this);
        buttonGroup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonFruit){
            startActivity(new Intent(MainActivity.this, FruitActivity.class));
        }
        if (v == buttonVeggie) {
            startActivity(new Intent(MainActivity.this, VegetableActivity.class));
        }
        if (v == buttonStep){
            startActivity(new Intent(MainActivity.this, StepActivity.class));
        }
        if (v == buttonWater) {
            startActivity(new Intent(MainActivity.this, WaterActivity.class));
        }
        if (v == buttonProfile){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }
        if (v == buttonGroup) {
            startActivity(new Intent(MainActivity.this, GroupActivity.class));
        }
    }

}