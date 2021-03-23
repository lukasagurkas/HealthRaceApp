package com.example.healthraceapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.awt.font.NumericShaper;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    // Instances of all UI elements
    private TextView email, username, day, month, year, gender;
    private Button buttonChangePassword, buttonDeleteAccount, buttonLogout;
    private ImageView userProfileImage;
//    private ImageButton buttonAddGroup;

    private String userID;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    FirebaseUser user;

    // Tag  value for debugging
    private static final String TAG = "ProfileActivity";

    //Instances for profile image
    String PROFILE_IMAGE_URL = null;
    int TAKE_IMAGE_CODE = 10001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Create title for the page
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        // Firebase instantiations
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance("https://health-race-app-default-rtdb." +
                "europe-west1.firebasedatabase.app/");
        user = mAuth.getCurrentUser();
        userID = user.getUid();
        String uID = mAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://health-" +
                "race-app-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference("Users").child(uID);

        // Buttons and image reference to the UI
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        userProfileImage = findViewById(R.id.userProfileImage);
//        buttonAddGroup = findViewById(R.id.imageButtonAddGroup);

        // If there is a user a profile image should be fetched from storage
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(userProfileImage);
            }
        }

        // OnClick listener for the log out button
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder logoutDialog = new AlertDialog.Builder(v.getContext());
                logoutDialog.setTitle("Log out");
                logoutDialog.setMessage("Are you sure you want to log out?");

                logoutDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(ProfileActivity.this,
                                LoginActivity.class));
                    }
                });

                logoutDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                logoutDialog.create().show();
            }
        });

        // OnClick listener for the change password button
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user.getEmail();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfileActivity.this,
                                "Password reset email sent", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this,
                                "Password reset failed", Toast.LENGTH_LONG).show();
                    }
                });
            }

        });

        // OnClick listener for the delete account button
        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder deleteAccountDialog =
                        new AlertDialog.Builder(v.getContext());
                deleteAccountDialog.setTitle("Delete Account");
                deleteAccountDialog.setMessage("Are you sure you want to delete your account?");

                deleteAccountDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUserAuth();


                            }
                        });

                deleteAccountDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                deleteAccountDialog.create().show();
            }
        });

        // OnClick listener for the add group button
//        buttonAddGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO: create new group
//            }
//        });

        //TextView references from the UI
        username = (TextView) findViewById(R.id.textUsernameProfile);
        email = (TextView) findViewById(R.id.textEmailProfile);
        day = (TextView) findViewById(R.id.textDay);
        month = (TextView) findViewById(R.id.textMonth);
        year = (TextView) findViewById(R.id.textYear);
        gender = (TextView) findViewById(R.id.textGender);

        // Get the values for username, email, gender and DoB from Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = mAuth.getCurrentUser();
                if (user != null) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", error.toException());
            }
        });
    }

    // Delete user from Realtime database
    private void deleteUserRealtime() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://health-race-" +
                "app-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                .child(userID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot: snapshot.getChildren()){
                    userSnapshot.getRef().removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Log.d(TAG, String.valueOf(task.getResult()) + "qwz");
                                    } else {
                                        Log.d(TAG, String.valueOf(task.getException()) + "zwq");
                                    }
                                }
                            });
                }
                mAuth.signOut();
                Toast.makeText(ProfileActivity.this, "Account deleted", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ProfileActivity.this,
                        RegisterActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    // Delete user's authentication details
    private void deleteUserAuth() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                            deleteUserRealtime();
                        }
                    }
                });
    }

    // Handle a click on Profile Image
    public void handleImageClick(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    userProfileImage.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }
    }

    // Upload the image to storage
    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e.getCause() );
                    }
                });
    }

    // Get the URL of the image
    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: " + uri);
                        setUserProfileUrl(uri);
                    }
                });
    }

    // Set the image as user's profile image
    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Updated successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Profile image failed...",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}