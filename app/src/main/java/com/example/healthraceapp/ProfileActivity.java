package com.example.healthraceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;

import java.awt.font.NumericShaper;

public class ProfileActivity extends AppCompatActivity {

    private TextView email, usernamle, day, month, year, gender;
    private Button buttonChangePassword, buttonDeleteAccount, buttonLogout;
    private ImageView userProfileImage;
    private  String userID;

    private DatabaseReference mDatabase;
    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    FirebaseUser user;

    private String currentUserId;
    private TextView username;
    private String emailProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://health-race-app-default-rtdb.europe-west1.firebasedatabase.app/");
        user = mAuth.getCurrentUser();

        buttonLogout = findViewById(R.id.buttonLogout);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText changePassword = new EditText(v.getContext());
                final AlertDialog.Builder changePasswordDialog = new AlertDialog.Builder(v.getContext());
                changePasswordDialog.setTitle("Reset Password");
                changePasswordDialog.setMessage("Enter your new password");
                changePasswordDialog.setView(changePassword);

                changePasswordDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = changePassword.getText().toString().trim();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileActivity.this, "Password reset successful", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, "Password reset failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                changePasswordDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                changePasswordDialog.create().show();
            }
        });

        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                    startActivity(new Intent(ProfileActivity.this,
                                            RegisterActivity.class));
                                }
                            }
                        });

            }
        });

        username = (TextView) findViewById(R.id.textUsernameProfile);
        email = (TextView) findViewById(R.id.textEmailProfile);
        day = (TextView) findViewById(R.id.textDay);
        month = (TextView) findViewById(R.id.textMonth);
        year = (TextView) findViewById(R.id.textYear);
        gender = (TextView) findViewById(R.id.textGender);


//        profileUserRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange( DataSnapshot snapshot) {
//                    String myUsername = snapshot.child("username").getValue(String.class);
//                    Log.d("check", myUsername);
//                    String myEmail = snapshot.child("email").getValue(String.class);
//                    Log.d("check", myEmail);
//
//                    username.setText(myUsername);
//                    email.setText(myEmail);
//            }
//
//            @Override
//            public void onCancelled(@NotNull DatabaseError error) {
////                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//
//        });
        String uID = mAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://health-" +
                "race-app-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(uID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String myUsername = snapshot.child("username").getValue(String.class);
                String myEmail = snapshot.child("email").getValue(String.class);
                Boolean myGender = (Boolean) snapshot.child("male").getValue();
                String myDay = String.valueOf(snapshot.child("day").getValue());
                String myMonth = String.valueOf(snapshot.child("month").getValue());
                String myYear = String.valueOf(snapshot.child("year").getValue());

                username.setText("@" + myUsername);
                email.setText("Email: " + myEmail);
                day.setText(myDay);
                month.setText("/" + myMonth);
                year.setText("/" + myYear);
                if (myGender){
                    gender.setText("Gender: male");
                }else{
                    gender.setText("Gender: female");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", error.toException());
            }
        });


    }

    private static final String TAG = "username";



}