package com.example.healthraceapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group {
    private String adminUID;
    private String groupName;
    private final Map<String, User> members = new HashMap<>();

    public Group() {}

    public Group(String adminUsername, String adminUID, User admin, String groupName) {
        this.adminUID = adminUID;
        members.put(adminUsername, admin);
        admin.addGroup(groupName);
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void addMember(String username, User user, String groupName) {
        members.put(username, user);
        user.addGroup(groupName);
    }

    public void removeMember(String username, User user, String groupName) {
        members.remove(username);
        user.exitGroup(groupName);
    }

    public String getAdminUID() {
        return adminUID;
    }

    public void setAdminUID(String adminUID) {
        this.adminUID = adminUID;
    }

    public Map<String, User> getMembers() {
        return members;
    }
}
