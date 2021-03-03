package com.example.healthraceapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
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

public class ProfileActivity extends AppCompatActivity {

    private TextView email, usernamle, dob, gender;
    private ImageView userProfileImage;
    private  String userID;

    private DatabaseReference mDatabase;
    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;

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
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        username = (TextView) findViewById(R.id.textUsernameProfile);
        email = (TextView) findViewById(R.id.textEmailProfile);
        dob = (TextView) findViewById(R.id.textDoB);
        gender = (TextView) findViewById(R.id.textGender);


        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                    String myUsername = snapshot.child("username").getValue(String.class);
                    Log.d("check", myUsername);
                    String myEmail = snapshot.child("email").getValue(String.class);
                    Log.d("check", myEmail);

                    username.setText(myUsername);
                    email.setText(myEmail);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
//                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
    }

    private static final String TAG = "username";



}