package com.example.healthraceapp;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public interface GroupCreationInterface {
    void createGroupButtonAction(String usernameFromMain, GroupActivityInterface groupActivityInterface, Context context);
    void createNewGroup(Boolean fromMainDialog, String usernameFromMain, String newGroupName1,
                        GroupAdjustmentsInterface groupAdjustments,
                        FirebaseDatabase firebaseDatabase,
                        DatabaseReference databaseReference,
                        FirebaseAuth firebaseAuth, ArrayList<String> allGroupNames ,
                        GroupActivityInterface groupActivityInterface, Context context);
}
