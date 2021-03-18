package com.example.healthraceapp;




import com.google.firebase.database.DatabaseReference;

public interface Intake {

    // Method for creating a bar chart
    void createBarChart();

    //This method makes sure the right data is swapped for the next day
    void switchDays();

    //This method copies a value from one database reference to another
    void switchValues(DatabaseReference ds1, DatabaseReference ds2);


}
