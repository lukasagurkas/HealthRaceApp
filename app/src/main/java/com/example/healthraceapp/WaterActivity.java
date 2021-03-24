package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WaterActivity extends AppCompatActivity implements Intake {
    // Stores the progress on the slider
    TextView waterTvProgressLabel;

    // Displays how much progress the user has made
    TextView waterProgress;

    // Displays the points for each checkpoint
    TextView checkpoint;
    String s;

    // TextView for the 'ml' for grams after the slider
    TextView milliliters;

    // TextView that shows points from water page
    // TODO: delete this later
    TextView pointsWater;

    // Button to add water quantity
    Button buttonAdd;

    // Progress bar for user to enter water intake
    ProgressBar progressBar;

    // Initiate total progress
    int totalProgress;

    // Initiate progress value
    int progress;

    // Initiate bar chart
    BarChart barChartWater;

    // Initialize values for barChart, minusN symbolizes the amount of days since the current day
    int waterMinusOne;
    int waterMinusTwo;
    int waterMinusThree;
    int waterMinusFour;
    int waterMinusFive;
    int waterMinusSix;

    //stores points received from water page
    int points_water;

    //store the checkpoints to be displayed on each widget
    String cp = "25";

    // Database reference for all values in the bar chart
    private DatabaseReference waterReference;
    private DatabaseReference pointsWaterReference;
    private DatabaseReference waterMinusOneDatabaseReference;
    private DatabaseReference waterMinusTwoDatabaseReference;
    private DatabaseReference waterMinusThreeDatabaseReference;
    private DatabaseReference waterMinusFourDatabaseReference;
    private DatabaseReference waterMinusFiveDatabaseReference;
    private DatabaseReference waterMinusSixDatabaseReference;

    // Initialize value for information text view
    int remaining = 2000;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        // Finds progress bar in activity page
        progressBar = findViewById(R.id.progressBar);

        // Sets the progress
        progressBar.setProgress(totalProgress);
        // Sets the maximum progress to 500 (ml of water)
        progressBar.setMax(2000);
        // Finds add button in activity page
        buttonAdd = findViewById(R.id.buttonAdd);

        // Add bar chart to activity and enter the corresponding data
        barChartWater = findViewById(R.id.barChartWater);
        createBarChart(barChartWater, getGraphData());

        // Finds slider in activity page
        SeekBar waterSeekBar = findViewById(R.id.seekBar);
        // Set a change listener on the SeekBar
        waterSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // TextView that shows the amount of ml's the user will add when pressing the add button
        waterTvProgressLabel = findViewById(R.id.textView);
        waterTvProgressLabel.setText("" + progress);
        waterTvProgressLabel.setTextColor(Color.WHITE);
        waterTvProgressLabel.setTextSize(15);

        // TextView that shows the amount of ml's the user will add when pressing the add button
        // TODO: delete this later
        pointsWater = findViewById(R.id.pointsWater);
        pointsWater.setTextColor(Color.WHITE);
        pointsWater.setTextSize(15);

        // Informational textView, showing how many milliliter of water the user should still take
        // Of course, this remaining value cannot be a negative number
        waterProgress = findViewById(R.id.waterProgress);
        if ((2000 - totalProgress) < 0) {
            remaining = 0;
        } else {
            remaining = 2000 - totalProgress;
        }
        waterProgress.setText("You drank " + totalProgress + " ml of water today out of the " +
                "recommended 2000 ml. Only " + remaining + " ml of water remains.");
        waterProgress.setTextColor(Color.WHITE);
        waterProgress.setTextSize(20);

        // Informational textView for the amount of points you get for each checkpoint
        checkpoint = (TextView)findViewById(R.id.checkpoint);

        checkpoint.setText(s);
        checkpoint.setTextColor(Color.WHITE);
        checkpoint.setTextSize(10);

        // Adds a 'ml' for milliliter add the end of the slider
        milliliters = findViewById(R.id.ml);
        milliliters.setTextColor(Color.WHITE);
        milliliters.setTextSize(20);

        // onClickListener for the add quantity button
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the value from the slider and displays it in the textView below
                progress = waterSeekBar.getProgress();
                waterTvProgressLabel.setText("" + progress);
                pointsWater.setText("" + points_water);

                // Add the value from the slider to the totalProgress
                totalProgress = totalProgress + progress;

                // Sets the progressbar to the new totalProgress
                progressBar.setProgress(totalProgress);

                // Updates the value in the database to the new totalProgress
                waterReference.setValue(totalProgress);

                // Updates the points value in the database to the new value
                // pointsWaterReference.setValue(points_water);

                // Informational textView, showing how many milliliter of water the user
                // should still take. Of course, this remaining value cannot be a negative number
                if ((2000 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 2000 - totalProgress;
                }
                waterProgress.setText("You drank " + totalProgress + " ml of water today out of " +
                        "the recommended 2000 ml. Only " + remaining + " ml of water remains.");

                // Creates a new barChart
                createBarChart(barChartWater, getGraphData());
                // Sets the points
                //TODO: uncomment the first line and delete the second when deleting the TextView pointsWater
                //setPoints(totalProgress, pointsWaterReference);
                setPoints(totalProgress, progress, pointsWaterReference, pointsWater);
                checkpoint.setText(s);

                setTotalPoints(firebaseDatabase, userID);
            }
        });

        //set the correct data paths
        setReferences();

        // If the value of amountOfWater in the database changes
        // It takes the value from amountOfWater in the database
        // And saves it in the variable totalProgress
        // And create a new barChart
        waterReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                totalProgress = dataFromDatabase;

                // Sets the progressbar to the totalProgress value in the database
                progressBar.setProgress(totalProgress);

                // Informational textView, showing how many milliliter of water the user
                // should still take. Of course, this remaining value cannot be a negative number
                if ((2000 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 2000 - totalProgress;
                }

                waterProgress.setText("You drank " + totalProgress + " ml of water today out of " +
                        "the recommended 2000 ml. Only " + remaining + " ml of water remains.");
                Log.d("Fruitchecker", String.valueOf(dataFromDatabase));

                // Sets the points
                //TODO uncomment the first line and delete the second when deleting the TextView pointsWater
                //setPoints(totalProgress, pointsWaterReference);
                setPoints(totalProgress, progress, pointsWaterReference, pointsWater);
                checkpoint.setText(s);

                setTotalPoints(firebaseDatabase, userID);

                // Creates a new barChart
                createBarChart(barChartWater, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("error", "loadPost:onCancelled", error.toException());
            }
        });
        Log.d("waterCheck", String.valueOf(totalProgress));


        // If the value of fruitMinusOne in the database changes
        // It takes the value from fruitMinusOne in the database
        // And saves it in the variable fruitMinusOne
        // And create a new barChart
        waterMinusOneDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                waterMinusOne = dataFromDatabase;
                createBarChart(barChartWater, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusTwo in the database changes
        // It takes the value from fruitMinusTwo in the database
        // And saves it in the variable fruitMinusTwo
        // And create a new barChart
        waterMinusTwoDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                waterMinusTwo = dataFromDatabase;
                createBarChart(barChartWater, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusThree in the database changes
        // It takes the value from fruitMinusThree in the database
        // And saves it in the variable fruitMinusThree
        // And create a new barChart
        waterMinusThreeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                waterMinusThree = dataFromDatabase;
                createBarChart(barChartWater, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusFour in the database changes
        // It takes the value from fruitMinusFour in the database
        // And saves it in the variable fruitMinusFour
        // And create a new barChart
        waterMinusFourDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                waterMinusFour = dataFromDatabase;
                createBarChart(barChartWater, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusFive in the database changes
        // It takes the value from fruitMinusFive in the database
        // And saves it in the variable fruitMinusFive
        // And create a new barChartFive
        waterMinusFiveDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                waterMinusFive = dataFromDatabase;
                createBarChart(barChartWater, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusSix in the database changes
        // It takes the value from fruitMinusSix in the database
        // And saves it in the variable fruitMinusSix
        // And create a new barChart
        waterMinusSixDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                waterMinusSix = dataFromDatabase;
                createBarChart(barChartWater, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Adds listener for the slider
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            waterTvProgressLabel.setText("" + progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };


    @Override
    public ArrayList getGraphData() {
        // ArrayList for the shown data
        ArrayList<BarEntry> graphData = new ArrayList<>();
        graphData.add(new BarEntry(1, waterMinusSix));
        graphData.add(new BarEntry(2, waterMinusFive));
        graphData.add(new BarEntry(3, waterMinusFour));
        graphData.add(new BarEntry(4, waterMinusThree));
        graphData.add(new BarEntry(5, waterMinusTwo));
        graphData.add(new BarEntry(6, waterMinusOne));
        graphData.add(new BarEntry(7, totalProgress));

        return graphData;
    }


    @Override
    public void switchDays() {

        //set the correct data paths
        setReferences();

        // Gives the value of waterMinusFive to waterMinusSix
        switchValues(waterMinusFiveDatabaseReference, waterMinusSixDatabaseReference);
        // Gives the value of waterMinusFour to waterMinusFive
        switchValues(waterMinusFourDatabaseReference, waterMinusFiveDatabaseReference);
        // Gives the value of waterMinusThree to waterMinusFour
        switchValues(waterMinusThreeDatabaseReference, waterMinusFourDatabaseReference);
        // Gives the value of waterMinusTwo to waterMinusThree
        switchValues(waterMinusTwoDatabaseReference, waterMinusThreeDatabaseReference);
        // Gives the value of waterMinusOne to waterMinusTwo
        switchValues(waterMinusOneDatabaseReference, waterMinusTwoDatabaseReference);
        // Gives the value of waterReference to waterMinusOne
        switchValues(waterReference, waterMinusOneDatabaseReference);
    }

    @Override
    public void setPoints(int waterTotalProgress, DatabaseReference pointsReference) {
        //adds points to the total from the water page if these checkpoints are crossed
        int points = 0;
        if (waterTotalProgress >= 200 && waterTotalProgress <=500) {
            points = 25;

            //setting value of points received for next checkpoint
            cp = String.valueOf(50);
        }
        else if (waterTotalProgress >= 500 && waterTotalProgress <=1000) {
            points = 75;
            cp = String.valueOf(100);
        }
        else if (waterTotalProgress >= 1000 && waterTotalProgress <= 2000) {
            points = 175;
            cp = String.valueOf(250);
        }
        else if (waterTotalProgress >= 2000 && waterTotalProgress <= 3200) {
            points = 425;
            cp = String.valueOf(500);
        }
        else if (waterTotalProgress >= 3200) {
            points = 925;
            s = "You have crossed all the checkpoints!";
            }
        else if (waterTotalProgress-progress==0 || waterTotalProgress<200) {
            points = 0;
            cp = String.valueOf(25);
        }
        checkpoint.setText(s);
        pointsReference.setValue(points);
    }

    @Override
    public void setReferences(){
        // Give the right data path to the corresponding reference
        waterReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfWater");
        pointsWaterReference = firebaseDatabase.getReference().child("Users").child(userID).child("points").child("waterPoints");
        waterMinusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusOne");
        waterMinusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusTwo");
        waterMinusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusThree");
        waterMinusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusFour");
        waterMinusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusFive");
        waterMinusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusSix");
    }

}