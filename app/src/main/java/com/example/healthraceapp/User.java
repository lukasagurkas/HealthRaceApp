package com.example.healthraceapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String username, email;
    private boolean male;
    private int year, month, day;
    private int dailyNumberOfSteps;
    private int amountOfVeg, amountOfWater, amountOfFruit;
    private int stepDetectMinusOne, StepDetectMinusTwo, StepDetectMinusThree, StepDetectMinusFour
            , StepDetectMinusFive, StepDetectMinusSix;
    private int waterMinusOne, waterMinusTwo, waterMinusThree, waterMinusFour
            , waterMinusFive, waterMinusSix;
    private int veggieMinusOne, veggieMinusTwo, veggieMinusThree, veggieMinusFour
            , veggieMinusFive, veggieMinusSix;
    private int fruitMinusOne, fruitMinusTwo, fruitMinusThree, fruitMinusFour
            , fruitMinusFive, fruitMinusSix;
    private int points, totalPoints;
    private int weeklySteps;


    private HashMap<String, Integer> stepsWeek;

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
        stepsWeek.put("dailyNumberOfSteps", dailyNumberOfSteps);
        stepsWeek.put("stepDetectMinusOne", stepDetectMinusOne);
        stepsWeek.put("stepDetectMinusTwo", StepDetectMinusTwo);
        stepsWeek.put("stepDetectMinusThree", StepDetectMinusThree);
        stepsWeek.put("stepDetectMinusFour", StepDetectMinusFour);
        stepsWeek.put("stepDetectMinusFive", StepDetectMinusFive);
        stepsWeek.put("stepDetectMinusSix", StepDetectMinusSix);
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

    public int getAmountOfVeg() {
        return amountOfVeg;
    }

    public void setAmountOfVeg(int amountOfVeg) {
        this.amountOfVeg = amountOfVeg;
    }

    public int getAmountOfWater() {
        return amountOfWater;
    }

    public void setAmountOfWater(int amountOfWater) {
        this.amountOfWater = amountOfWater;
    }

    public int getAmountOfFruit() {
        return amountOfFruit;
    }

    public void setAmountOfFruit(int amountOfFruit) {
        this.amountOfFruit = amountOfFruit;
    }

    public int getStepDetectMinusOne() { return stepDetectMinusOne; }

    public void setStepDetectMinusOne(int stepDetectMinusOne) {
        this.stepDetectMinusOne = stepDetectMinusOne;
    }

    public int getStepDetectMinusTwo() {
        return StepDetectMinusTwo; }

    public void setStepDetectMinusTwo(int stepDetectMinusTwo) {
        StepDetectMinusTwo = stepDetectMinusTwo; }

    public int getStepDetectMinusThree() {
        return StepDetectMinusThree; }

    public void setStepDetectMinusThree(int stepDetectMinusThree) {
        StepDetectMinusThree = stepDetectMinusThree; }

    public int getStepDetectMinusFour() {
        return StepDetectMinusFour; }

    public void setStepDetectMinusFour(int stepDetectMinusFour) {
        StepDetectMinusFour = stepDetectMinusFour; }

    public int getStepDetectMinusFive() {
        return StepDetectMinusFive; }

    public void setStepDetectMinusFive(int stepDetectMinusFive) {
        StepDetectMinusFive = stepDetectMinusFive; }

    public int getStepDetectMinusSix() {
        return StepDetectMinusSix; }

    public void setStepDetectMinusSix(int stepDetectMinusSix) {
        StepDetectMinusSix = stepDetectMinusSix; }

    public int getWaterMinusOne() {
        return waterMinusOne;
    }

    public void setWaterMinusOne(int waterMinusOne) {
        this.waterMinusOne = waterMinusOne;
    }

    public int getWaterMinusTwo() {
        return waterMinusTwo;
    }

    public void setWaterMinusTwo(int waterMinusTwo) {
        this.waterMinusTwo = waterMinusTwo;
    }

    public int getWaterMinusThree() {
        return waterMinusThree;
    }

    public void setWaterMinusThree(int waterMinusThree) {
        this.waterMinusThree = waterMinusThree;
    }

    public int getWaterMinusFour() {
        return waterMinusFour;
    }

    public void setWaterMinusFour(int waterMinusFour) {
        this.waterMinusFour = waterMinusFour;
    }

    public int getWaterMinusFive() {
        return waterMinusFive;
    }

    public void setWaterMinusFive(int waterMinusFive) {
        this.waterMinusFive = waterMinusFive;
    }

    public int getWaterMinusSix() {
        return waterMinusSix;
    }

    public void setWaterMinusSix(int waterMinusSix) {
        this.waterMinusSix = waterMinusSix;
    }

    public int getVeggieMinusOne() {
        return veggieMinusOne;
    }

    public void setVeggieMinusOne(int veggieMinusOne) {
        this.veggieMinusOne = veggieMinusOne;
    }

    public int getVeggieMinusTwo() {
        return veggieMinusTwo;
    }

    public void setVeggieMinusTwo(int veggieMinusTwo) {
        this.veggieMinusTwo = veggieMinusTwo;
    }

    public int getVeggieMinusThree() {
        return veggieMinusThree;
    }

    public void setVeggieMinusThree(int veggieMinusThree) {
        this.veggieMinusThree = veggieMinusThree;
    }

    public int getVeggieMinusFour() {
        return veggieMinusFour;
    }

    public void setVeggieMinusFour(int veggieMinusFour) {
        this.veggieMinusFour = veggieMinusFour;
    }

    public int getVeggieMinusFive() {
        return veggieMinusFive;
    }

    public void setVeggieMinusFive(int veggieMinusFive) {
        this.veggieMinusFive = veggieMinusFive;
    }

    public int getVeggieMinusSix() {
        return veggieMinusSix;
    }

    public void setVeggieMinusSix(int veggieMinusSix) {
        this.veggieMinusSix = veggieMinusSix;
    }

    public int getFruitMinusOne() {
        return fruitMinusOne;
    }

    public void setFruitMinusOne(int fruitMinusOne) {
        this.fruitMinusOne = fruitMinusOne;
    }

    public int getFruitMinusTwo() {
        return fruitMinusTwo;
    }

    public void setFruitMinusTwo(int fruitMinusTwo) {
        this.fruitMinusTwo = fruitMinusTwo;
    }

    public int getFruitMinusThree() {
        return fruitMinusThree;
    }

    public void setFruitMinusThree(int fruitMinusThree) {
        this.fruitMinusThree = fruitMinusThree;
    }

    public int getFruitMinusFour() {
        return fruitMinusFour;
    }

    public void setFruitMinusFour(int fruitMinusFour) {
        this.fruitMinusFour = fruitMinusFour;
    }

    public int getFruitMinusFive() {
        return fruitMinusFive;
    }

    public void setFruitMinusFive(int fruitMinusFive) {
        this.fruitMinusFive = fruitMinusFive;
    }

    public int getFruitMinusSix() {
        return fruitMinusSix;
    }

    public void setFruitMinusSix(int fruitMinusSix) {
        this.fruitMinusSix = fruitMinusSix;
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

    public void setPoints(int points) { this.points = points; }

    public int getTotalPoints() { return totalPoints; }

    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public int getWeeklySteps() { return weeklySteps; }

    public void setWeeklySteps(int weeklySteps) { this.weeklySteps = weeklySteps; }

    public HashMap<String, Integer> getStepsWeek() { return stepsWeek; }

    public void setStepsWeek(HashMap<String, Integer> stepsWeek2) { this.stepsWeek = stepsWeek2; }
}
