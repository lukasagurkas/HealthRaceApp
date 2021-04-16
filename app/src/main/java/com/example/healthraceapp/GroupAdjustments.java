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

import java.util.ArrayList;
import java.util.HashMap;

public class GroupAdjustments implements GroupAdjustmentsInterface {

    //Method tp add a user to the group
    public void addUserToGroup(String username, String currentlySelectedGroup1,
                               FirebaseDatabase firebaseDatabase,
                               DatabaseReference databaseReference, Context context) {
        // Has to be final boolean array because it is accessed from an inner class
        final boolean[] userExists = new boolean[1];
        firebaseDatabase.getReference("Users").addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //iterating through the children if this database reference
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //check when the username of an user is equal to the username that is given
                    if (dataSnapshot.child("username").getValue(String.class).equals(username)) {
                        userExists[0] = true;
                        User selectedUser = dataSnapshot.getValue(User.class);

                        databaseReference.child("Groups").child(currentlySelectedGroup1)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                //check if it was successful
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    //assign the selected group to the variable selectedGroup
                                    Group selectedGroup = task.getResult().getValue(Group.class);

                                    // Adding the new member to the group
                                    selectedGroup.addMember(selectedUser.getUsername(), selectedUser, currentlySelectedGroup1);
                                    //add the new member of the group in the database
                                    databaseReference.child("Groups").child(currentlySelectedGroup1).child("members").child(selectedUser.getUsername()).setValue(selectedUser);
                                    //update the user attributes in the database
                                    databaseReference.child("Users").child(dataSnapshot.getKey()).setValue(selectedUser);
                                    //display an toast for confirmation
                                    Toast.makeText(context, username + " has been added to the group", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                //chech if the username does not exist
                if (!userExists[0]) {
                    Toast.makeText(context, "A user with this username does not exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting data was canceled
                Log.w("Database", "onCancelled", error.toException());
            }
        });
    }

    //method to change the groupname
    public void changeGroupName(String groupNameNew, ArrayList<String> allGroupNames,
                                 DatabaseReference databaseReference, String currentlySelectedGroup,
                                 GroupActivityInterface groupActivityInterface,
                                 Context context) {
        //Check if the new group name is unique
        if (allGroupNames.contains(groupNameNew)) {
            Toast.makeText(context, "This group name is already in use", Toast.LENGTH_LONG).show();
            return;
        }
        //Check if the new group name has at most 10 characters
        if (groupNameNew.length() > 10) {
            Toast.makeText(context, "The group name has to contain at most ten characters", Toast.LENGTH_LONG).show();
            return;
        }
        //Check if the new group name has at least one character
        if (groupNameNew.length() < 1) {
            Toast.makeText(context, "The group name has to contain at least one character", Toast.LENGTH_LONG).show();
            return;
        }
        //Add listener to the selected group reference
        databaseReference.child("Groups").child(currentlySelectedGroup).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    //remove the old group name from the arraylist
                    allGroupNames.remove(currentlySelectedGroup);
                    //Add the new group name to the arraylist
                    allGroupNames.add(groupNameNew);

                    Group currentGroup = task.getResult().getValue(Group.class);
                    //Set the new group name of the group
                    currentGroup.setGroupName(groupNameNew);

                    HashMap<String, User> allMembers = (HashMap<String, User>) currentGroup.getMembers();
                    //remove the old group name from the database
                    databaseReference.child("Groups").child(currentlySelectedGroup).removeValue();
                    //Add the new group name to the database
                    databaseReference.child("Groups").child(groupNameNew).setValue(currentGroup);

                    databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //iterate through all users
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                //Check when the username is the key and get the value of it
                                if (allMembers.containsKey(dataSnapshot.child("username")
                                        .getValue(String.class))) {
                                    User tempUser = dataSnapshot.getValue(User.class);
                                    //remove the group
                                    tempUser.exitGroup(currentlySelectedGroup);
                                    //add the new group
                                    tempUser.addGroup(groupNameNew);
                                    databaseReference.child("Users").child(dataSnapshot.getKey())
                                            .setValue(tempUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //check if it was successful
                                            if (!task.isSuccessful()) {
                                                Log.e("firebase", task.getException()
                                                        .getMessage());
                                            } else {
                                                //display a toast for confirmation
                                                Toast.makeText(context,
                                                        "The group name has been changed",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                            //get the initial user view
                            groupActivityInterface.getUserInitializeView();
                            //initialize the spinner
                            groupActivityInterface.initializeSpinner();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
}
