package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FruitActivity extends AppCompatActivity implements Intake {
    // Stores the progress on the slider
    TextView fruitTvProgressLabel;

    // Displays how much progress the user has made
    TextView fruitIntakeProgress;

    // Displays the points for each checkpoint
    TextView fruitCheckpoint;
    String points_value;
    int cp_number;
    int cp_value;
    SpannableStringBuilder ssb;

    // Displays the entry of the user
    TextView fruitGrams;

    // Button to add water quantity
    Button fruitButtonAdd;

    // Progress bar for user to enter water intake
    ProgressBar fruitProgressBar;

    // Initiate bar chart
    BarChart barChartFruit;

    // Initialize values for barChart, minusN symbolizes the amount of days since the current day
    int fruitMinusOne = 0;
    int fruitMinusTwo = 0;
    int fruitMinusThree = 0;
    int fruitMinusFour = 0;
    int fruitMinusFive = 0;
    int fruitMinusSix = 0;

    // Initiate progress value
    int progress;

    // Initiate total progress
    int totalProgress;

    // Initialize value for information text view
    int remaining = 500;

    // Database reference for all values in the bar chart
    private DatabaseReference fruitReference;
    private DatabaseReference pointsFruitReference;
    private DatabaseReference fruitMinusOneDatabaseReference;
    private DatabaseReference fruitMinusTwoDatabaseReference;
    private DatabaseReference fruitMinusThreeDatabaseReference;
    private DatabaseReference fruitMinusFourDatabaseReference;
    private DatabaseReference fruitMinusFiveDatabaseReference;
    private DatabaseReference fruitMinusSixDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);

        // Finds progress bar in activity page
        fruitProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        Log.d("check", String.valueOf(totalProgress));
        // Sets the progress
        fruitProgressBar.setProgress(totalProgress);
        // Sets the maximum progress to 500 (grams of fruit)
        fruitProgressBar.setMax(500);
        // Finds add button in activity page
        fruitButtonAdd = findViewById(R.id.buttonAdd);

        // Add bar chart to activity and enter the corresponding data
        barChartFruit = findViewById(R.id.barChartFruit);
        createBarChart(barChartFruit, getGraphData());

        // Finds slider in activity page
        SeekBar fruitSeekBar = findViewById(R.id.seekBar);
        // set a change listener on the SeekBar
        fruitSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // TextView that shows the amount of grams the user will add when pressing the add button
        fruitTvProgressLabel = findViewById(R.id.textView);
        fruitTvProgressLabel.setText("" + progress);
        fruitTvProgressLabel.setTextColor(Color.WHITE);
        fruitTvProgressLabel.setTextSize(15);

        // Informational textView, showing how many grams of fruit the user should still take
        // Of course, this remaining value cannot be a negative number
        fruitIntakeProgress = findViewById(R.id.intakeProgress);
        if ((500 - totalProgress) < 0) {
            remaining = 0;
        } else {
            remaining = 500 - totalProgress;
        }
        fruitIntakeProgress.setText("You ate " + totalProgress + " g of fruits today out of the " +
                "recommended 500 g. Only " + remaining + " grams of fruit remains.");
        fruitIntakeProgress.setTextColor(Color.WHITE);
        fruitIntakeProgress.setTextSize(20);

        // Informational textView for the amount of points you get for each checkpoint
        fruitCheckpoint = findViewById(R.id.checkpoint);
        fruitCheckpoint.setTextColor(Color.WHITE);
        fruitCheckpoint.setTextSize(20);

        // Adds a 'g' for grams add the end of the slider
        fruitGrams = findViewById(R.id.grams);
        fruitGrams.setTextColor(Color.WHITE);
        fruitGrams.setTextSize(20);

        // onClickListener for the add quantity button
        fruitButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the value from the slider and displays it in the textView below
                progress = fruitSeekBar.getProgress();
                fruitTvProgressLabel.setText("" + progress);

                // Add the value from the slider to the totalProgress
                totalProgress = totalProgress + progress;
                // Sets the progressbar to the new totalProgress
                fruitProgressBar.setProgress(totalProgress);

                // Updates the value in the database to the new totalProgress
                fruitReference.setValue(totalProgress);

                // Informational textView, showing how many grams of fruit the user should
                // still take of course, this remaining value cannot be a negative number
                if ((500 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - totalProgress;
                }
                fruitIntakeProgress.setText("You ate " + totalProgress + " g of fruits today " +
                        "out of the recommended 500 g. Only " + remaining +
                        " grams of fruit remains.");

                //Informational textview about user's progress and checkpoints
                if (totalProgress>=500) {
                    ssb = new SpannableStringBuilder("Congratulations! You earned 500 points" +
                            " and crossed all the checkpoints!");
                    fruitCheckpoint.setText(ssb);
                }
                else if (totalProgress<50) {
                    ssb = new SpannableStringBuilder("You will receive 25 points for the " +
                            "next checkpoint.");
                    fruitCheckpoint.setText(ssb);
                }
                else {
                    if (totalProgress >= 50 && totalProgress < 100) {
                        cp_number = 1;
                        cp_value = 50;
                        points_value = String.valueOf(50);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 25 points! ");
                        ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.RED);
                        ssb.setSpan(fcsRed, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (totalProgress >= 100 && totalProgress < 175) {
                        cp_number = 2;
                        cp_value = 100;
                        points_value = String.valueOf(100);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 50 points! ");
                        ForegroundColorSpan fcsOrange = new ForegroundColorSpan(Color.rgb(255,140,0));
                        ssb.setSpan(fcsOrange, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (totalProgress >= 175 && totalProgress < 275) {
                        cp_number = 3;
                        cp_value = 175;
                        points_value = String.valueOf(250);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 100 points! ");
                        ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.rgb(255,215,0));
                        ssb.setSpan(fcsYellow, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (totalProgress >= 275 && totalProgress < 400) {
                        cp_number = 4;
                        cp_value = 275;
                        points_value = String.valueOf(500);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 250 points! ");
                        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.GREEN);
                        ssb.setSpan(fcsGreen, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    ssb.append("You will receive " + points_value + " points for the next checkpoint.");
                    ssb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                            23, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    fruitCheckpoint.setText(ssb);
                }

                // Sets the points
                setPoints(totalProgress, pointsFruitReference);

                setTotalPoints(firebaseDatabase, userID);

                // Creates a new barChart
                createBarChart(barChartFruit, getGraphData());
            }
        });

        //set the correct data paths
        setReferences();

        // If the value of amountOfFruit in the database changes
        // It takes the value from amountOfFruit in the database
        // And saves it in the variable totalProgress
        // And create a new barChart
        fruitReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                totalProgress = dataFromDatabase;

                // Sets the progressbar to the totalProgress value in the database
                fruitProgressBar.setProgress(totalProgress);

                // Informational textView, showing how many grams of fruit the user should
                // still take of course, this remaining value cannot be a negative number
                if ((500 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - totalProgress;
                }
                fruitIntakeProgress.setText("You ate " + totalProgress + " g of fruits today out " +
                        "of the recommended 500 g. Only " + remaining + " grams of fruit remains.");
                Log.d("Fruitchecker", String.valueOf(dataFromDatabase));

                //Informational textview about user's progress and checkpoints
                if (totalProgress>=500) {
                    ssb = new SpannableStringBuilder("Congratulations! You earned 500 points" +
                            " and crossed all the checkpoints!");
                    fruitCheckpoint.setText(ssb);
                }
                else if (totalProgress<50) {
                    ssb = new SpannableStringBuilder("You will receive 25 points for the " +
                            "next checkpoint.");
                    fruitCheckpoint.setText(ssb);
                }
                else {
                    if (totalProgress >= 50 && totalProgress < 100) {
                        cp_number = 1;
                        cp_value = 50;
                        points_value = String.valueOf(50);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 25 points! ");
                        ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.RED);
                        ssb.setSpan(fcsRed, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (totalProgress >= 100 && totalProgress < 175) {
                        cp_number = 2;
                        cp_value = 100;
                        points_value = String.valueOf(100);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 50 points! ");
                        ForegroundColorSpan fcsOrange = new ForegroundColorSpan(Color.rgb(255,140,0));
                        ssb.setSpan(fcsOrange, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (totalProgress >= 175 && totalProgress < 275) {
                        cp_number = 3;
                        cp_value = 175;
                        points_value = String.valueOf(250);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 100 points! ");
                        ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.rgb(255,215,0));
                        ssb.setSpan(fcsYellow, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (totalProgress >= 275 && totalProgress < 400) {
                        cp_number = 4;
                        cp_value = 275;
                        points_value = String.valueOf(500);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 250 points! ");
                        ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.GREEN);
                        ssb.setSpan(fcsGreen, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    ssb.append("You will receive " + points_value + " points for the next checkpoint.");
                    ssb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                            23, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    fruitCheckpoint.setText(ssb);
                }

                // Sets the points
                setPoints(totalProgress, pointsFruitReference);

                setTotalPoints(firebaseDatabase, userID);

                // Creates a new barChart
                createBarChart(barChartFruit, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("error", "loadPost:onCancelled", error.toException());
            }
        });

        // If the value of fruitMinusOne in the database changes
        // It takes the value from fruitMinusOne in the database
        // And saves it in the variable fruitMinusOne
        // And create a new barChart
        fruitMinusOneDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusOne = dataFromDatabase;
                createBarChart(barChartFruit, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusTwo in the database changes
        // It takes the value from fruitMinusTwo in the database
        // And saves it in the variable fruitMinusTwo
        // And create a new barChart
        fruitMinusTwoDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusTwo = dataFromDatabase;
                createBarChart(barChartFruit, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusThree in the database changes
        // It takes the value from fruitMinusThree in the database
        // And saves it in the variable fruitMinusThree
        // And create a new barChart
        fruitMinusThreeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusThree = dataFromDatabase;
                createBarChart(barChartFruit, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusFour in the database changes
        // It takes the value from fruitMinusFour in the database
        // And saves it in the variable fruitMinusFour
        // And create a new barChart
        fruitMinusFourDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusFour = dataFromDatabase;
                createBarChart(barChartFruit, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusFive in the database changes
        // It takes the value from fruitMinusFive in the database
        // And saves it in the variable fruitMinusFive
        // And create a new barChart
        fruitMinusFiveDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusFive = dataFromDatabase;
                createBarChart(barChartFruit, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusSix in the database changes
        // It takes the value from fruitMinusSix in the database
        // And saves it in the variable fruitMinusSix
        // And create a new barChart
        fruitMinusSixDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusSix = dataFromDatabase;
                createBarChart(barChartFruit, getGraphData());
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
            fruitTvProgressLabel.setText("" + progress);
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
        graphData.add(new BarEntry(1, fruitMinusSix));
        graphData.add(new BarEntry(2, fruitMinusFive));
        graphData.add(new BarEntry(3, fruitMinusFour));
        graphData.add(new BarEntry(4, fruitMinusThree));
        graphData.add(new BarEntry(5, fruitMinusTwo));
        graphData.add(new BarEntry(6, fruitMinusOne));
        graphData.add(new BarEntry(7, totalProgress));

        return graphData;
    }

    // Every day at midnight the bar chart will get updated
    // This function makes sure the right data is swapped for the next day
    @Override
    public void switchDays() {
        //set the correct data paths
        setReferences();
        // Gives the value of fruitMinusFive to fruitMinusSix
        switchValues(fruitMinusFiveDatabaseReference, fruitMinusSixDatabaseReference);
        // Gives the value of fruitMinusFour to fruitMinusFive
        switchValues(fruitMinusFourDatabaseReference, fruitMinusFiveDatabaseReference);
        // Gives the value of fruitMinusThree to fruitMinusFour
        switchValues(fruitMinusThreeDatabaseReference, fruitMinusFourDatabaseReference);
        // Gives the value of fruitMinusTwo to fruitMinusThree
        switchValues(fruitMinusTwoDatabaseReference, fruitMinusThreeDatabaseReference);
        // Gives the value of fruitMinusOne to fruitMinusTwo
        switchValues(fruitMinusOneDatabaseReference, fruitMinusTwoDatabaseReference);
        // Gives the value of fruitReference to fruitMinusOne
        switchValues(fruitReference, fruitMinusOneDatabaseReference);
    }

    @Override
    public void setPoints(int totalProgress, DatabaseReference pointsReference) {
        //adds points to the total from the water page if these checkpoints are crossed
        int points = 0;
        if (totalProgress >= 50 && totalProgress < 100) { points = 25; }
        else if (totalProgress >= 100 && totalProgress < 175) { points = 75; }
        else if (totalProgress >= 175 && totalProgress < 275) { points = 175; }
        else if (totalProgress >= 275 && totalProgress < 500) { points = 425; }
        else if (totalProgress >= 500) { points = 925; }
        else if (totalProgress < 50) { points = 0; }
        pointsReference.setValue(points);
    }

    @Override
    public void setReferences() {
        // Give the right data path to the corresponding reference
        fruitReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitWeek").child("amountOfFruit");
        pointsFruitReference = firebaseDatabase.getReference().child("Users").child(userID).child("points").child("fruitPoints");
        fruitMinusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitWeek").child("fruitMinusOne");
        fruitMinusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitWeek").child("fruitMinusTwo");
        fruitMinusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitWeek").child("fruitMinusThree");
        fruitMinusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitWeek").child("fruitMinusFour");
        fruitMinusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitWeek").child("fruitMinusFive");
        fruitMinusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitWeek").child("fruitMinusSix");

    }

}
