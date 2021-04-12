package com.example.healthraceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

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

}
