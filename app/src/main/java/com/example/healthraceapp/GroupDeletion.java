package com.example.healthraceapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class GroupDeletion implements GroupDeletionInterface {
    // Tag for error location
    private static final String TAG = "GroupDeletion";

    // Database references
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // Initializes the database references
    GroupDeletion() {
        firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
                "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference();
    }

    // Method that deletes a group
    @Override
    public void deleteGroup(String groupName, Context context, GroupActivityInterface groupActivity) {
        databaseReference.child("Groups").child(groupName).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                // If the task fails, it gives an error message in the console
                // If the task is successful, it deletes the group
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    // HashMap that contains all the members of the group to be deleted
                    HashMap<String, User> allMembers = (HashMap<String, User>) task.getResult().getValue();

                    databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Removes all members in the group to be deleted
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (allMembers.containsKey(dataSnapshot.child("username").getValue(String.class))) {
                                    User tempUser = dataSnapshot.getValue(User.class);
                                    // The user will no longer be part of the group
                                    tempUser.exitGroup(groupName);
                                    databaseReference.child("Users").child(dataSnapshot.getKey()).setValue(tempUser);
                                }
                            }
                            // Removes the group from the database
                            databaseReference.child("Groups").child(groupName).removeValue();

                            // Removes the key of the groupName from every member that was part of it
                            allMembers.remove("groupName");

                            // If the user is part of another group after deleting the previous group
                            // The group page view will be re-initialized
                            // And the spinner will be re-initialized
                            // A toast will confirm that the previous group has been deleted
                            if (groupActivity != null) {
                                groupActivity.getUserInitializeView();
                                groupActivity.initializeSpinner();
                                Toast.makeText(context, "The group has been deleted", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public void removeUserFromGroup(String username, String currentlySelectedGroup, Context context, GroupActivityInterface groupActivity) {
        // Has to be final boolean array because it is accessed from an inner class
        final boolean[] userExists = new boolean[1];
        firebaseDatabase.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // For all the users in the snapshot, it will confirm that there is a user
                // with that username by setting userExists to true.
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("username").getValue(String.class).equals(username)) {
                        userExists[0] = true;
                        User selectedUser = dataSnapshot.getValue(User.class);

                        databaseReference.child("Groups").child(currentlySelectedGroup).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                // If the task fails, it will give an error message in the console
                                // If it is successful, the user will be removed from the group
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    Group selectedGroup = task.getResult().getValue(Group.class);

                                    // Removing the new member from the group
                                    selectedGroup.removeMember(selectedUser.getUsername(), selectedUser, currentlySelectedGroup);

                                    // Removing the database reference for this member
                                    databaseReference.child("Groups").child(currentlySelectedGroup).child("members").child(selectedUser.getUsername()).removeValue();

                                    databaseReference.child("Users").child(dataSnapshot.getKey()).setValue(selectedUser);

                                    // If the user is still part of another group after removing
                                    // the current one, it will re-initialize the view
                                    if (groupActivity != null) {
                                        groupActivity.setRecycleView();
                                        Toast.makeText(context, username + " has been deleted from the group", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }
                }
                // If there is no user with the entered username, there will be a toast
                // saying that there is no user with this username
                if (!userExists[0]) {
                    Toast.makeText(context, "A user with this username does not exist", Toast.LENGTH_LONG).show();
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
