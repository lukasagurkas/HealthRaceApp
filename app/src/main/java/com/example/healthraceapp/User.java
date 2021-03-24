package com.example.healthraceapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String username, email;
    private boolean male;
    private int year, month, day;
    private int points, totalPoints;
    private int weeklySteps;


    //create hash maps for the last 7 days for each intake activity
    private HashMap<String, Integer> stepsWeek;
    private HashMap<String, Integer> veggieWeek;
    private HashMap<String, Integer> fruitWeek;
    private HashMap<String, Integer> waterWeek;

    // Names of groups that the user is part of
    private ArrayList<String> groupNames;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User(){}


    public User(String username, String email) {}

    public User(String username, String email, boolean male, int year, int month, int day) {
        this.username = username;
        this.email = email;
        this.male = male;
        this.year = year;
        this.month = month ;
        this.day = day;

        groupNames = new ArrayList<>();
        groupNames.add("");

        stepsWeek = new HashMap<>();
        stepsWeek.put("dailyNumberOfSteps", 0);
        stepsWeek.put("stepDetectMinusOne", 0);
        stepsWeek.put("stepDetectMinusTwo", 0);
        stepsWeek.put("stepDetectMinusThree", 0);
        stepsWeek.put("stepDetectMinusFour", 0);
        stepsWeek.put("stepDetectMinusFive", 0);
        stepsWeek.put("stepDetectMinusSix", 0);

        veggieWeek = new HashMap<>();
        veggieWeek.put("amountOfVeg", 0);
        veggieWeek.put("veggieMinusOne", 0);
        veggieWeek.put("veggieMinusTwo", 0);
        veggieWeek.put("veggieMinusThree", 0);
        veggieWeek.put("veggieMinusFour", 0);
        veggieWeek.put("veggieMinusFive", 0);
        veggieWeek.put("veggieMinusSix", 0);

        waterWeek = new HashMap<>();
        waterWeek.put("amountOfWater", 0);
        waterWeek.put("waterMinusOne", 0);
        waterWeek.put("waterMinusTwo", 0);
        waterWeek.put("waterMinusThree", 0);
        waterWeek.put("waterMinusFour", 0);
        waterWeek.put("waterMinusFive", 0);
        waterWeek.put("waterMinusSix", 0);

        fruitWeek = new HashMap<>();
        fruitWeek.put("amountOfFruit", 0);
        fruitWeek.put("fruitMinusOne", 0);
        fruitWeek.put("fruitMinusTwo", 0);
        fruitWeek.put("fruitMinusThree", 0);
        fruitWeek.put("fruitMinusFour", 0);
        fruitWeek.put("fruitMinusFive", 0);
        fruitWeek.put("fruitMinusSix", 0);
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

    public ArrayList<String> getGroupNames() {
        return groupNames;
    }

    public void addGroup(String group) {
        groupNames.add(group);
    }

    public void exitGroup(String group) {
        groupNames.remove(group);
    }

    public int getPoints() { return points; }

    public int getTotalPoints() { return totalPoints; }

    public int getWeeklySteps() { return weeklySteps; }

    public HashMap<String, Integer> getStepsWeek() { return stepsWeek; }

    public HashMap<String, Integer> getVeggieWeek() {
        return veggieWeek;
    }

    public HashMap<String, Integer> getFruitWeek() {
        return fruitWeek;
    }

    public HashMap<String, Integer> getWaterWeek() {
        return waterWeek;
    }

}
