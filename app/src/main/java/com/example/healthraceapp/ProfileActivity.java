package com.example.healthraceapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class ProfileActivity extends AppCompatActivity {

    private TextView textEmail, textGender, textUsernameProfile, textDoB;
    private ImageView userProfileImage;

    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseStorage = FirebaseStorage.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = firebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        textUsernameProfile = (TextView) findViewById(R.id.textUsernameProfile);
        textEmail = (TextView) findViewById(R.id.textEmailProfile);
        textDoB = (TextView) findViewById(R.id.textDoB);
        textGender = (TextView) findViewById(R.id.textGender);


        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                textUsernameProfile.setText(user.getUserName());
                textEmail.setText(user.getEmail());
                textDoB.setText(user.getDoB());
            }

            @Override
            public void onCancelled( DatabaseError error) {
//                Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}