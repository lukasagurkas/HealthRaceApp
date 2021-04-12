package com.example.healthraceapp;

import android.content.Context;

public interface GroupDeletionInterface {

    void deleteGroup(String groupName, Context context, GroupActivity groupActivity, Class<?> profileActivity);

    void removeUserFromGroup(String username, String currentlySelectedGroup, Context context, GroupActivity groupActivity, Class<?> profileActivity);
}
