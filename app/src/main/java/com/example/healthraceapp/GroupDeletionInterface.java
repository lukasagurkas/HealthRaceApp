package com.example.healthraceapp;

import android.content.Context;

public interface GroupDeletionInterface {

    void deleteGroup(String groupName, Context context, GroupActivityInterface groupActivity);

    void removeUserFromGroup(String username, String currentlySelectedGroup, Context context, GroupActivityInterface groupActivity);
}
