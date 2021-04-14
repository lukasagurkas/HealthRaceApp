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

public class GroupAdjustments implements GroupAdjustmentsInterface {

    public void addUserToGroup(String username, String currentlySelectedGroup1,
                               FirebaseDatabase firebaseDatabase,
                               DatabaseReference databaseReference, Context context) {
        Log.d("check", "1");
        // Has to be final boolean array because it is accessed from an inner class
        final boolean[] userExists = new boolean[1];
        firebaseDatabase.getReference("Users").addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("check", "2");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("username").getValue(String.class).equals(username)) {
                        userExists[0] = true;
                        User selectedUser = dataSnapshot.getValue(User.class);

                        databaseReference.child("Groups").child(currentlySelectedGroup1)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    Group selectedGroup = task.getResult().getValue(Group.class);

                                    // Adding the new member to the group
                                    selectedGroup.addMember(selectedUser.getUsername(), selectedUser, currentlySelectedGroup1);

                                    Log.d("checkGroup", currentlySelectedGroup1);
                                    Log.d("check", selectedUser.getUsername());
                                    databaseReference.child("Groups").child(currentlySelectedGroup1).child("members").child(selectedUser.getUsername()).setValue(selectedUser);

                                    databaseReference.child("Users").child(dataSnapshot.getKey()).setValue(selectedUser);

                                    Toast.makeText(context, username + " has been added to the group", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
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
}
