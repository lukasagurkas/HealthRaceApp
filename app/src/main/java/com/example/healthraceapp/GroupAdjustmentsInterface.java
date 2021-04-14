package com.example.healthraceapp;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public interface GroupAdjustmentsInterface {
    void addUserToGroup(String username, String currentlySelectedGroup1,
                        FirebaseDatabase firebaseDatabase, DatabaseReference databaseReference,
                        Context context);

    void changeGroupName(String groupNameNew, ArrayList<String> allGroupNames,
                         DatabaseReference databaseReference, String currentlySelectedGroup,
                         GroupActivityInterface groupActivityInterface,
                         Context context);
}
