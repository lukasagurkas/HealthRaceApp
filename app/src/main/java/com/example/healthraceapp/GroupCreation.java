package com.example.healthraceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class GroupCreation implements GroupCreationInterface{
    String newGroupName = "";


    public void createGroupButtonAction(String usernameFromMain, GroupActivityInterface groupActivityInterface, Context context){
        // Dialog to input the group name
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter group name");

        // Setting up the input for the AlertDialog
        final EditText input = new EditText(context);
        builder.setView(input);

        // Setting up the buttons for the AlertDialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newGroupName = input.getText().toString();
                Log.d("Check", newGroupName);
                Log.d("Check", "check1");
                groupActivityInterface.checkGroupNameUniqueness(true, newGroupName);
                Log.d("Check", "check2");
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Create new group
    public void createNewGroup(Boolean fromMainDialog, String usernameFromMain, String newGroupName1,
                                GroupAdjustmentsInterface groupAdjustments,
                                FirebaseDatabase firebaseDatabase,
                                DatabaseReference databaseReference,
                                FirebaseAuth firebaseAuth, ArrayList<String> allGroupNames ,
                                GroupActivityInterface groupActivityInterface, Context context) {
        if (allGroupNames.contains(newGroupName1)) {
            Toast.makeText(context, "This group name is already in use", Toast.LENGTH_LONG).show();
            return;
        }

        if (newGroupName1.length() > 10) {
            Toast.makeText(context, "The group name has to contain at most ten characters", Toast.LENGTH_LONG).show();
            return;
        }

        if (newGroupName1.length() < 1) {
            Toast.makeText(context, "The group name has to contain at least one character", Toast.LENGTH_LONG).show();
            return;
        }


        firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Database", task.getException().toString());
                } else {
                    Log.d("Database", task.getResult().toString());
                    User user = task.getResult().getValue(User.class);

                    // New group
                    Group newGroup = new Group(user.getUsername(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), user, newGroupName1);

                    // Add the new group to the Firebase database
                    DatabaseReference newGroupRef = firebaseDatabase.getReference("Groups");
                    newGroupRef.child(newGroup.getGroupName()).setValue(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                Toast.makeText(context, "Group has been created", Toast.LENGTH_LONG).show();

                                groupActivityInterface.setRecycleView();

                                firebaseDatabase.getReference("Users")
                                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task1) {
                                        if (!task1.isSuccessful()) {
                                            Log.e("firebase", "Error getting data", task1.getException());
                                        } else {
                                            Log.d("firebase", String.valueOf(task1.getResult()));
                                            groupActivityInterface.initializeSpinner();
                                            String userID = firebaseAuth.getCurrentUser().getUid();
                                            groupActivityInterface.getUserInitializeView();
                                            if (fromMainDialog) {
                                                groupAdjustments.addUserToGroup(usernameFromMain, newGroupName1, firebaseDatabase, databaseReference, context);
                                            }
                                        }
                                    }
                                });
                                Log.d("firebase", String.valueOf(task.getResult()));
                            }
                        }
                    });
                }
            }
        });
    }

}
