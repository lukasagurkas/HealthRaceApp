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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth mAuth;

    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout, buttonStep, buttonFruit, buttonProfile, buttonVeggie, buttonWater;

    // If permission is granted
    private int PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing firebase authentication object
        mAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()){
            //closing this activity
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        //getting current user
//        FirebaseUser user = mAuth.getCurrentUser();

        //initializing views
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonStep =  findViewById(R.id.buttonStep);
        buttonFruit = findViewById(R.id.buttonFruit);
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonVeggie = findViewById(R.id.buttonVeggie);
        buttonWater = findViewById(R.id.buttonWater);

        //displaying logged in user name
//        textViewUserEmail.setText("Welcome "+ user.getEmail());

        //adding listener to button
        buttonLogout.setOnClickListener(this);
        buttonStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StepActivity.class));
            }
        });
        buttonFruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FruitActivity.class));
            }
        });
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
        buttonVeggie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VegetableActivity.class));
            }
        });
        buttonWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WaterActivity.class));
            }
        });


    }

//    public void askPermission() {
//        if (ContextCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(MainActivity.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();
//        } else {
//            requestActivity();
//        }
//    }

//    // Requests permission to use step counter
//    private void requestActivity() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
//            new AlertDialog.Builder(this)
//                    .setTitle("Permission needed")
//                    .setMessage("This permission is needed to track the amount of steps you take")
//                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_CODE);
//                        }
//                    })
//                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .create().show();
//        } else {
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_CODE);
//        }
//        ;
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        // Gives toast saying if the user granted or denied permission to use step counter
//        if (requestCode == PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            mAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}