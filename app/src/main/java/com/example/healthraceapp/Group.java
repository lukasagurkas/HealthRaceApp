package com.example.healthraceapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group {
    // The user ID for the admin user
    private String adminUID;

    // Name of a group
    private String groupName;

    // Map with all members of a group
    private final Map<String, User> members = new HashMap<>();

    public Group() {}

    // Contractor of the class group
    public Group(String adminUsername, String adminUID, User admin, String groupName) {
        this.adminUID = adminUID;
        members.put(adminUsername, admin);
        admin.addGroup(groupName);
        this.groupName = groupName;
    }

    // Returns the group name
    public String getGroupName() {
        return groupName;
    }

    // Sets the group name
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    // Adds a member to the map with members and adds the user to the group
    public void addMember(String username, User user, String groupName) {
        Log.d("check", "3");
        members.put(username, user);
        user.addGroup(groupName);
    }

    // Removes a member from the map with members and removes the user from the group
    public void removeMember(String username, User user, String groupName) {
        members.remove(username);
        user.exitGroup(groupName);
    }

    // Returns the admins' user ID
    public String getAdminUID() {
        return adminUID;
    }

    // Sets the admins' user ID
    public void setAdminUID(String adminUID) {
        this.adminUID = adminUID;
    }

    // Returns the map with members
    public Map<String, User> getMembers() {
        return members;
    }
}
