package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class WaterActivity extends AppCompatActivity implements Intake {
    // Stores the progress on the slider
    TextView waterTvProgressLabel;

    // Displays how much progress the user has made
    TextView waterProgress, dailyPoints;

    // Displays the points for each checkpoint
    TextView waterCheckpoint;
    int cp_number;
    int cp_value;
    String points_value_next;
    SpannableStringBuilder ssb;

    // TextView for the 'ml' for grams after the slider
    TextView milliliters;

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

        //shows the individual activity points
        dailyPoints = findViewById(R.id.dailyPoints);
        dailyPoints.setTextColor(Color.WHITE);
        dailyPoints.setTextSize(20);

        // Informational textView for the amount of points you get for each checkpoint
        waterCheckpoint = findViewById(R.id.checkpoint);
        waterCheckpoint.setTextColor(Color.WHITE);
        waterCheckpoint.setTextSize(20);

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

                // Add the value from the slider to the totalProgress
                totalProgress = totalProgress + progress;

                // Sets the progressbar to the new totalProgress
                progressBar.setProgress(totalProgress);

                // Updates the value in the database to the new totalProgress
                waterReference.setValue(totalProgress);

                // Informational textView, showing how many milliliter of water the user
                // should still take. Of course, this remaining value cannot be a negative number
                if ((2000 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 2000 - totalProgress;
                }
                waterProgress.setText("You drank " + totalProgress + " ml of water today out of " +
                        "the recommended 2000 ml. Only " + remaining + " ml of water remains.");

                //Informational TextView showing which checkpoint they crossed and information about
                // the checkpoint
                if (totalProgress>=3200) {
                    ssb = new SpannableStringBuilder("Congratulations! You earned 500 points" +
                            " and crossed all the checkpoints!");
                    waterCheckpoint.setText(ssb);
                    dailyPoints.setText("Your water intake points today: 925");
                }
                else if (totalProgress<200) {
                    ssb = new SpannableStringBuilder("You will receive 25 points for the " +
                            "next checkpoint.");
                    waterCheckpoint.setText(ssb);
                    dailyPoints.setText("Your water intake points today: 0");
                }
                else {
                    if (totalProgress >= 200 && totalProgress < 500) {
                        cp_number = 1;
                        cp_value = 200;
                        points_value_next = String.valueOf(50);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " ml and earned 25 points! ");
                        ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.RED);
                        ssb.setSpan(fcsRed, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dailyPoints.setText("Your water intake points today: 25");
                    }
                    else if (totalProgress >= 500 && totalProgress < 1000) {
                        cp_number = 2;
                        cp_value = 500;
                        points_value_next = String.valueOf(100);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " ml and earned 50 points! ");
                        ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.rgb(255,140,0));
                        ssb.setSpan(fcsYellow, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dailyPoints.setText("Your water intake points today: 75");
                    }
                    else if (totalProgress >= 1000 && totalProgress < 2000) {
                        cp_number = 3;
                        cp_value = 1000;
                        points_value_next = String.valueOf(250);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " ml and earned 100 points! ");
                        ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.rgb(218,165,32));
                        ssb.setSpan(fcsYellow, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dailyPoints.setText("Your water intake points today: 175");
                    }
                    else if (totalProgress >= 2000 && totalProgress < 3200) {
                        cp_number = 4;
                        cp_value = 2000;
                        points_value_next = String.valueOf(500);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " ml and earned 250 points! ");
                        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.GREEN);
                        ssb.setSpan(fcsGreen, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dailyPoints.setText("Your water intake points today: 425");
                    }

                    ssb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                            23, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append("You will receive " + points_value_next + " points for the next checkpoint.");

                    waterCheckpoint.setText(ssb);
                }

                // Creates a new barChart
                createBarChart(barChartWater, getGraphData());
                // Sets the points
                setPoints(totalProgress, pointsWaterReference);

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

                //Informational TextView showing which checkpoint they crossed and information about
                // the checkpoint
                if (totalProgress>=3200) {
                    ssb = new SpannableStringBuilder("Congratulations! You earned 500 points" +
                            " and crossed all the checkpoints!");
                    waterCheckpoint.setText(ssb);
                    dailyPoints.setText("Your water intake points today: 925");
                }
                else if (totalProgress<200) {
                    ssb = new SpannableStringBuilder("You will receive 25 points for the " +
                            "next checkpoint.");
                    waterCheckpoint.setText(ssb);
                    dailyPoints.setText("Your fruit intake points today: 0");
                }
                else {
                    if (totalProgress >= 200 && totalProgress < 500) {
                        cp_number = 1;
                        cp_value = 200;
                        points_value_next = String.valueOf(50);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " ml and earned 25 points! ");
                        ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.RED);
                        ssb.setSpan(fcsRed, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dailyPoints.setText("Your water intake points today: 25");
                    }
                    else if (totalProgress >= 500 && totalProgress < 1000) {
                        cp_number = 2;
                        cp_value = 500;
                        points_value_next = String.valueOf(100);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " ml and earned 50 points! ");
                        ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.rgb(255,140,0));
                        ssb.setSpan(fcsYellow, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dailyPoints.setText("Your water intake points today: 75");
                    }
                    else if (totalProgress >= 1000 && totalProgress < 2000) {
                        cp_number = 3;
                        cp_value = 1000;
                        points_value_next = String.valueOf(250);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " ml and earned 100 points! ");
                        ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.rgb(218,165,32));
                        ssb.setSpan(fcsYellow, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dailyPoints.setText("Your water intake points today: 175");
                    }
                    else if (totalProgress >= 2000 && totalProgress < 3200) {
                        cp_number = 4;
                        cp_value = 2000;
                        points_value_next = String.valueOf(500);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " ml and earned 250 points! ");
                        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.GREEN);
                        ssb.setSpan(fcsGreen, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dailyPoints.setText("Your water intake points today: 425");
                    }

                    ssb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                            23, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.append("You will receive " + points_value_next + " points for the next checkpoint.");

                    waterCheckpoint.setText(ssb);
                }

                // Sets the points
                setPoints(totalProgress, pointsWaterReference);

                setTotalPoints(firebaseDatabase, userID);

//                dailyPoints.setText("Your water intake points today: " + snapshot.child("points").child("waterPoints"));

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
        @SuppressLint("SetTextI18n")
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

    //sets points for individual activity to be stored in firebase
    @Override
    public void setPoints(int totalProgress, DatabaseReference pointsReference) {
        pointsReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int points;
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else
                {
                    Log.d("firebase", String.valueOf(Objects.requireNonNull(task.getResult()).getValue()));

                    points = task.getResult().getValue(Integer.class);
                    //adds points to the total from the water page if these checkpoints are crossed
                    if (totalProgress >= 200 && totalProgress < 500) { points = 25; }
                    else if (totalProgress >= 500 && totalProgress < 1000) { points = 75; }
                    else if (totalProgress >= 1000 && totalProgress < 2000) { points = 175; }
                    else if (totalProgress >= 2000 && totalProgress < 3200) { points = 425; }
                    else if (totalProgress >= 3200) { points = 925; }
                    else if (totalProgress < 200) { points = 0; }
                    pointsReference.setValue(points);
                }
            }
        });
    }

    //sets firebase references that are used in the rest of the class
    @Override
    public void setReferences(){
        // Give the right data path to the corresponding reference
        waterReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterWeek").child("amountOfWater");
        pointsWaterReference = firebaseDatabase.getReference().child("Users").child(userID).child("points").child("waterPoints");
        waterMinusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterWeek").child("waterMinusOne");
        waterMinusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterWeek").child("waterMinusTwo");
        waterMinusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterWeek").child("waterMinusThree");
        waterMinusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterWeek").child("waterMinusFour");
        waterMinusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterWeek").child("waterMinusFive");
        waterMinusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterWeek").child("waterMinusSix");
    }

}