package com.example.healthraceapp;

public class User {
    private String username, email;
    private boolean male;
    private int year, month, day;
    private int dailyNumberOfSteps;
    private int weeklyNumberOfSteps;

    public User(){}

    public User(String username, String email) {}

    public User(String username, String email, boolean male, int year, int month, int day) {
        this.username = username;
        this.email = email;
        this.male = male;
        this.year = year;
        this.month = month ;
        this.day = day;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDailyNumberOfSteps() {
        return dailyNumberOfSteps;
    }

    public void setDailyNumberOfSteps(int dailyNumberOfSteps) {
        this.dailyNumberOfSteps = dailyNumberOfSteps;
    }

    public int getWeeklyNumberOfSteps() {
        return weeklyNumberOfSteps;
    }

    public void setWeeklyNumberOfSteps(int weeklyNumberOfSteps) {
        this.weeklyNumberOfSteps = weeklyNumberOfSteps;
    }
}
