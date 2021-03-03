package com.example.healthraceapp;

public class User {
    private String username, email;
    private boolean male;
    private int year, month, day;
    private int numberOfSteps;

    public User(){}

    public User(String username, String email) {}

    public User(String username, String email, boolean male, int year, int month, int day) {
        this.username = username;
        this.email = email;
        this.male = male;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isMale() {
        return male;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getNumberOfSteps() { return numberOfSteps; }

    public void setNumberOfSteps(int numberOfSteps) { this.numberOfSteps = numberOfSteps; }
}
