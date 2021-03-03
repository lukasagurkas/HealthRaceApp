package com.example.healthraceapp;

public class User {
    public String username, email;
    public boolean male;
    public int year, month, day;

    public User() {}

    public User(String username, String email, boolean male, int year, int month, int day) {
        this.username = username;
        this.email = email;
        this.male = male;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getUserName() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getDoB() {
        return year;
    }
}
