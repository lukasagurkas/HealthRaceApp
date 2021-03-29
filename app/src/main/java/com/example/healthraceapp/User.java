package com.example.healthraceapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String username, email;
    private boolean male;
    private int year, month, day;
    private int totalPoints;
    // Negative total points is used for the leaderboard to order group members by score in
    // descending fashion
    private int negativeTotalPoints;
    private int weeklySteps;


    // Create hash maps for the last 7 days for each intake activity
    private HashMap<String, Integer> stepsWeek;
    private HashMap<String, Integer> veggieWeek;
    private HashMap<String, Integer> fruitWeek;
    private HashMap<String, Integer> waterWeek;
    // Create hash maps of points
    private HashMap<String, Integer> points;

    // Names of groups that the user is part of
    private ArrayList<String> groupNames;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User(){}


//    public User(String username, String email) {}

    public User(String username, String email, boolean male, int year, int month, int day) {
        this.username = username;
        this.email = email;
        this.male = male;
        this.year = year;
        this.month = month ;
        this.day = day;

        // Create an array list of group names
        groupNames = new ArrayList<>();
        // Add an empty group name
        groupNames.add("");

        // Initiate the points hash map
        points = new HashMap<String, Integer>();
        // Put the points into the hash map and set them to 0
        points.put("waterPoints", 0);
        points.put("veggiePoints", 0);
        points.put("fruitPoints", 0);
        points.put("stepPoints", 0);

        // Initiate the steps hash map
        stepsWeek = new HashMap<>();
        //Put the steps from today as well as the other 6 days
        stepsWeek.put("dailyNumberOfSteps", 0);
        stepsWeek.put("stepDetectMinusOne", 0);
        stepsWeek.put("stepDetectMinusTwo", 0);
        stepsWeek.put("stepDetectMinusThree", 0);
        stepsWeek.put("stepDetectMinusFour", 0);
        stepsWeek.put("stepDetectMinusFive", 0);
        stepsWeek.put("stepDetectMinusSix", 0);

        // Initiate the vegetable hash map
        veggieWeek = new HashMap<>();
        //Put the veggie intake from today as well as the other 6 days
        veggieWeek.put("amountOfVeg", 0);
        veggieWeek.put("veggieMinusOne", 0);
        veggieWeek.put("veggieMinusTwo", 0);
        veggieWeek.put("veggieMinusThree", 0);
        veggieWeek.put("veggieMinusFour", 0);
        veggieWeek.put("veggieMinusFive", 0);
        veggieWeek.put("veggieMinusSix", 0);

        // Initiate the water hash map
        waterWeek = new HashMap<>();
        //Put the water intake from today as well as the other 6 days
        waterWeek.put("amountOfWater", 0);
        waterWeek.put("waterMinusOne", 0);
        waterWeek.put("waterMinusTwo", 0);
        waterWeek.put("waterMinusThree", 0);
        waterWeek.put("waterMinusFour", 0);
        waterWeek.put("waterMinusFive", 0);
        waterWeek.put("waterMinusSix", 0);

        // Initiate the fruit hash map
        fruitWeek = new HashMap<>();
        //Put the fruit intake from today as well as the other 6 days
        fruitWeek.put("amountOfFruit", 0);
        fruitWeek.put("fruitMinusOne", 0);
        fruitWeek.put("fruitMinusTwo", 0);
        fruitWeek.put("fruitMinusThree", 0);
        fruitWeek.put("fruitMinusFour", 0);
        fruitWeek.put("fruitMinusFive", 0);
        fruitWeek.put("fruitMinusSix", 0);
    }
    // Getter for username
    public String getUsername() {
        return username;
    }
    // Getter for email
    public String getEmail() {
        return email;
    }

    public boolean isMale() {
        return male;
    }
    // Getter for year
    public int getYear() {
        return year;
    }
    // Getter for month
    public int getMonth() {
        return month;
    }

    // Getter for month
    public int getDay() {
        return day;
    }
    // Getter for group names
    public ArrayList<String> getGroupNames() {
        return groupNames;
    }
    // Add group names
    public void addGroup(String group) {
        if (!groupNames.contains(group)) {
            groupNames.add(group);
        }
    }
    // Exit the group
    public void exitGroup(String group) {
        groupNames.remove(group);
    }
    // Getter for points
    public HashMap<String, Integer> getPoints() {
        return points;
    }
    // Getter for total points
    public int getTotalPoints() { return totalPoints; }
    // Getter for to turn all points negative
    public int getNegativeTotalPoints() { return negativeTotalPoints; }
    // Getter for weekly steps
    public int getWeeklySteps() { return weeklySteps; }


    // Getter for hash maps
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
