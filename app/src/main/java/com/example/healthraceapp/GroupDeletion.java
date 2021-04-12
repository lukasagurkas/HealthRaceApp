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
    private static final String TAG = "GroupDeletion";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    GroupDeletion() {
        firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
                "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void deleteGroup(String groupName, Context context, GroupActivityInterface groupActivity) {
        databaseReference.child("Groups").child(groupName).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    HashMap<String, User> allMembers = (HashMap<String, User>) task.getResult().getValue();

                    databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (allMembers.containsKey(dataSnapshot.child("username").getValue(String.class))) {
                                    User tempUser = dataSnapshot.getValue(User.class);
                                    tempUser.exitGroup(groupName);
                                    databaseReference.child("Users").child(dataSnapshot.getKey()).setValue(tempUser);
                                }
                            }
                            databaseReference.child("Groups").child(groupName).removeValue();

                            allMembers.remove("groupName");

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
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("username").getValue(String.class).equals(username)) {
                        userExists[0] = true;
                        User selectedUser = dataSnapshot.getValue(User.class);

                        databaseReference.child("Groups").child(currentlySelectedGroup).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    Group selectedGroup = task.getResult().getValue(Group.class);

                                    // Adding the new member to the group
                                    selectedGroup.removeMember(selectedUser.getUsername(), selectedUser, currentlySelectedGroup);

                                    databaseReference.child("Groups").child(currentlySelectedGroup).child("members").child(selectedUser.getUsername()).removeValue();

                                    databaseReference.child("Users").child(dataSnapshot.getKey()).setValue(selectedUser);

                                    if (groupActivity != null) {
                                        groupActivity.setRecycleView();
                                        Toast.makeText(context, username + " has been deleted from the group", Toast.LENGTH_LONG).show();
                                    }
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
                Log.w(TAG, "onCancelled", error.toException());
            }
        });
    }
}
