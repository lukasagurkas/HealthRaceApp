package com.example.healthraceapp;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public interface GroupAdjustmentsInterface {
    void addUserToGroup(String username, String currentlySelectedGroup1,
                        FirebaseDatabase firebaseDatabase, DatabaseReference databaseReference, Context context);
}
