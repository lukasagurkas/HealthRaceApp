package com.example.healthraceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    // Instances of all the textViews on the profile page
    private TextView email, username, day, month, year, gender, points, groups;

    // Instances of all the Buttons on the profile page
    private Button buttonChangePassword, buttonDeleteAccount, buttonLogout;

    // Instance of the profile picture on the profile page
    private ImageView userProfileImage;

    // String with the userID of the user and string for username to search
    private String userID, searchUsername;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    FirebaseUser user;

    // Tag  value for debugging
    private static final String TAG = "ProfileActivity";

    // ArrayList with all the groups the user is part of
    private final ArrayList<String> allGroups = new ArrayList<>();

    // Instances for profile image
    String PROFILE_IMAGE_URL = null;
    int TAKE_IMAGE_CODE = 10001;

    // boolean to see if the user is looking at his own profile
    public boolean ownProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Sets ownProfile to true if the user is looking at his own profile,
        // Sets ownProfile to false otherwise
        ownProfile = isOwnProfile();

        // Create title for the page
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        // Firebase instantiations
        mAuth = FirebaseAuth.getInstance();

        // Initializes the database references
//        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance("https://health-race-app-default-rtdb." +
                "europe-west1.firebasedatabase.app/");

        // Get user ID of current user
        userID = mAuth.getCurrentUser() .getUid();

        // Get the username if the user is looking at another profile
        if (!ownProfile){
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                searchUsername = bundle.getString("username");
            }
        }

        setProfile(ownProfile);

        // Buttons and image reference to the UI
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        userProfileImage = findViewById(R.id.userProfileImage);

        //set the profile page according to the value of ownProfile
        setButtonVisibility(ownProfile);

        // If there is a user a profile image should be fetched from storage
        if (mAuth.getCurrentUser() != null) {
            if (mAuth.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .into(userProfileImage);
            }
        }

        // OnClick listener for the log out button
        // Gives a pop-up asking the user if he/she really wants to log out
        // If the user does not, he/she will get taken back to the profile page
        // Otherwise he/she will be logged out and taken to the login page
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder logoutDialog = new AlertDialog.Builder(v.getContext());
                logoutDialog.setTitle("Log out");
                // Pop-up asking the user if he/she really wants to log out
                logoutDialog.setMessage("Are you sure you want to log out?");

                // The user wants to log out
                logoutDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Logs out
                        mAuth.signOut();
                        finish();
                        // Starts the login activity
                        startActivity(new Intent(ProfileActivity.this,
                                LoginActivity.class));
                    }
                });

                // The user does not want to log out
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
                final AlertDialog.Builder changePasswordDialog =
                        new AlertDialog.Builder(v.getContext());
                changePasswordDialog.setTitle("Change password");
                // Pop-up asking the user to enter its current password
                changePasswordDialog.setMessage("To change your password enter you current password");

                // Get a reference to the already created profile activity layout
                ConstraintLayout parentLayout = (ConstraintLayout) findViewById(R.id.constraintLayoutProfile);

                // Inflate (create) another copy of our custom layout
                LayoutInflater inflater = getLayoutInflater();
                View passwordLayout = inflater.inflate(R.layout.password_alert_dialog, parentLayout, false);

                // Setting up the input for the AlertDialog
                EditText inputPassword = (EditText) passwordLayout.findViewById(R.id.editTextTextPassword);

                changePasswordDialog.setView(inputPassword);

                // The user wants to change its password
                changePasswordDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Get auth credentials from the user for re-authentication.
                                assert FirebaseAuth.getInstance().getCurrentUser() != null;
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(Objects.requireNonNull(user.getEmail()), inputPassword.getText().toString());

                                // Prompt the user to re-provide their sign-in credentials
                                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // If the user entered the correct current password
                                                // An email will be sent, in which the user can
                                                // change its password. Otherwise, an error Toast
                                                // will be shown
                                                if (task.isSuccessful()) {
                                                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
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
                                                } else {
                                                    Toast.makeText(ProfileActivity.this, String.valueOf(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                // The user does not want to change its password
                changePasswordDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                changePasswordDialog.show();
            }


        });

        // OnClick listener for the delete account button
        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder deleteAccountDialog =
                        new AlertDialog.Builder(v.getContext());
                deleteAccountDialog.setTitle("Delete Account");
                // Pop-up asking the user if he/she really want to deleted their account
                deleteAccountDialog.setMessage("If you are sure that you want to delete this account enter your password");

                // Get a reference to the already created profile activity layout
                ConstraintLayout parentLayout = (ConstraintLayout) findViewById(R.id.constraintLayoutProfile);

                // Inflate (create) another copy of our custom layout
                LayoutInflater inflater = getLayoutInflater();
                View passwordLayout = inflater.inflate(R.layout.password_alert_dialog, parentLayout, false);

                // Setting up the input for the AlertDialog
                EditText inputPassword = (EditText) passwordLayout.findViewById(R.id.editTextTextPassword);

                deleteAccountDialog.setView(inputPassword);

                // The user wants to delete their account
                deleteAccountDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                // Get auth credentials from the user for re-authentication.
                                //assert user != null;
                                AuthCredential credential = EmailAuthProvider
                                        .getCredential(Objects.requireNonNull(user.getEmail()), inputPassword.getText().toString());

                                // Prompt the user to re-provide their sign-in credentials
                                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // If the task is successful, the account is deleted
                                                // Otherwise an error Toast is shown.
                                                if (task.isSuccessful()) {
                                                    deleteUserAuth();
                                                    deleteUserFromGroups();
                                                } else {
                                                    Toast.makeText(ProfileActivity.this, String.valueOf(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                // The user does not want to delete their account
                deleteAccountDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                deleteAccountDialog.show();
            }
        });
    }

    // Method to set the reference for the databsae
    public DatabaseReference setRef(String uID) {
        return FirebaseDatabase.getInstance("https://health-" +
                "race-app-default-rtdb.europe-west1.firebasedatabase.app/").
                getReference("Users").child(uID);
    }

    // Method to show the right data for each textView
    public void setContent(DatabaseReference databaseReference) {
        //TextView references from the UI
        username = (TextView) findViewById(R.id.textUsernameProfile);
        email = (TextView) findViewById(R.id.textEmailProfile);
        day = (TextView) findViewById(R.id.textDay);
        month = (TextView) findViewById(R.id.textMonth);
        year = (TextView) findViewById(R.id.textYear);
        gender = (TextView) findViewById(R.id.textGender);
        points = (TextView) findViewById(R.id.textPoints);
        groups = (TextView) findViewById(R.id.textGroups);

        // Get the values for username, email, gender, DoB and total points for the day from
        // Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mAuth.getCurrentUser() != null) {
                    String myUsername = snapshot.child("username").getValue(String.class);
                    String myEmail = snapshot.child("email").getValue(String.class);
                    Boolean myGender = (Boolean) snapshot.child("male").getValue();
                    String myDay = String.valueOf(snapshot.child("day").getValue());
                    String myMonth = String.valueOf(snapshot.child("month").getValue());
                    String myYear = String.valueOf(snapshot.child("year").getValue());
                    String days_totalPoints = String.valueOf(snapshot.child("totalPoints").getValue());

                    // Getting all group names that the user is part of
                    User currentUser = snapshot.getValue(User.class);
                    ArrayList<String> allUserGroupNames = currentUser.getGroupNames();
                    allUserGroupNames.remove("");
                    String groupNames = allUserGroupNames.toString();
                    groupNames = groupNames.substring(1, groupNames.length() - 1);

                    // In a textView, the number of groups the user is part of is shown
                    if (allUserGroupNames.size() == 0) {
                        groups.setText("Part of groups: None yet!");
                    } else {
                        groups.setText("Part of groups: " + groupNames);
                    }

                    // Shows the right data for each textView
                    username.setText("@" + myUsername);
                    email.setText("Email: " + myEmail);
                    day.setText(myDay);
                    month.setText("/" + myMonth);
                    year.setText("/" + myYear);
                    if (myGender) {
                        gender.setText("Gender: Male");
                    } else {
                        gender.setText("Gender: Female");
                    }
                    points.setText("Today's points: " + days_totalPoints);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w("error", "loadPost:onCancelled", error.toException());
            }
        });
    }

    // If ownProfile is true, it shows the profile of the user itself
    // Otherwise it will show the profile of the searched username
    private void setProfile(Boolean ownProfile) {
        if (ownProfile) {
            // Get user ID of current user
            setContent(setRef(mAuth.getCurrentUser().getUid()));
        } else {
            FirebaseDatabase.getInstance("https://health-" +
                    "race-app-default-rtdb.europe-west1.firebasedatabase.app/").
                    getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Iterating over all users
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        if (dataSnapshot.child("username").getValue() != null) {
                            if (dataSnapshot.child("username").getValue().equals(searchUsername)) {
                                String uID = dataSnapshot.getKey();
                                setContent(setRef(uID));
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // Method for grabbing the boolean variable ownProfile
    private boolean isOwnProfile() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            return bundle.getBoolean("ownProfile");
        } else {
            return true;
        }
    }

    // If the user is not viewing its own profile,
    // the logOut button, changePassword button and deleteAccount button are not shown.
    private void setButtonVisibility(Boolean ownProfile) {
        if (!ownProfile) {
            buttonLogout.setVisibility(Button.GONE);
            buttonChangePassword.setVisibility(Button.GONE);
            buttonDeleteAccount.setVisibility(Button.GONE);
        }
    }

    // Delete user from Realtime database
    private void deleteUserRealtime() {
        FirebaseDatabase.getInstance("https://health-race-" +
                "app-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                .child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    userSnapshot.getRef().removeValue();
                }
                mAuth.signOut();
                Toast.makeText(ProfileActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this,
                        RegisterActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // When the user deletes its account, it will be removed from all the groups it was part of
    private void deleteUserFromGroups() {

        firebaseDatabase.getReference("Users").child(mAuth.getCurrentUser().getUid())
                .child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, task.getException().toString());
                } else {
                    String username = task.getResult().getValue(String.class);

                    // Helper class to either delete a group or remove a user from a group
                    GroupDeletion groupDeletion = new GroupDeletion();

                    firebaseDatabase.getReference("Groups").addListenerForSingleValueEvent(
                            new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Iterating through groups
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                // If the current user is the admin of the group then the whole group is deleted
                                if (userID.equals(dataSnapshot.child("adminUID").getValue(String.class))) {
                                    groupDeletion.deleteGroup(dataSnapshot.child("groupName").getValue(String.class),
                                            ProfileActivity.this, null);
                                } else { // Otherwise we just delete the user from the group
                                    groupDeletion.removeUserFromGroup(username, dataSnapshot.child("groupName").getValue(String.class),
                                            ProfileActivity.this, null);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Getting data was canceled
                            Log.w(TAG, "onCancelled", error.toException());
                        }
                    });
                }
            }
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
                        Log.e(TAG, "onFailure: ", e.getCause());
                    }
                });
    }

    // Get the URL of the image
    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
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
                // Update profile picture succesfully
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Updated successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                // Update profile picture fails
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Profile image failed...",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}