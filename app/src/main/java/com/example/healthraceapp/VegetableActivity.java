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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class VegetableActivity extends AppCompatActivity implements Intake {

    private static final String TAG = "";

    // Stores the progress on the slider
    TextView veggieTvProgressLabel;

    // Displays how much progress the user has made
    TextView veggieIntakeProgress;

    // Displays the points for each checkpoint
    TextView veggieCheckpoint;
    String points_value;
    int cp_number;
    int cp_value;
    SpannableStringBuilder ssb;

    // TextView for the 'g' for grams after the slider
    TextView veggieGrams;

    // Button to add water quantity
    Button veggieButtonAdd;

    // Progress bar for user to enter water intake
    ProgressBar veggieProgressBar;

    // Initiate total progress
    static int veggieTotalProgress;

    // Initiate progress value
    int progress;

    // Initiate bar chart
    BarChart barChartVeggie;

    // Initialize values for barChart, minusN symbolizes the amount of days since the current day
    int veggieMinusOne = 0;
    int veggieMinusTwo = 0;
    int veggieMinusThree = 0;
    int veggieMinusFour = 0;
    int veggieMinusFive = 0;
    int veggieMinusSix = 0;

    // Initialize value for information text view
    int remaining = 500;

    // Database reference for all values in the bar chart
    private DatabaseReference vegReference;
    private DatabaseReference pointsVeggieReference;
    private DatabaseReference veggieMinusOneDatabaseReference;
    private DatabaseReference veggieMinusTwoDatabaseReference;
    private DatabaseReference veggieMinusThreeDatabaseReference;
    private DatabaseReference veggieMinusFourDatabaseReference;
    private DatabaseReference veggieMinusFiveDatabaseReference;
    private DatabaseReference veggieMinusSixDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetable);

        // Finds progress bar in activity page
        veggieProgressBar = findViewById(R.id.progressBar);
        // Sets the progress
        veggieProgressBar.setProgress(veggieTotalProgress);
        // Sets the maximum progress to 500 (grams of vegetables)
        veggieProgressBar.setMax(500);

        // Finds add button in activity page
        veggieButtonAdd = findViewById(R.id.buttonAdd);

        // Add bar chart to activity and enter the corresponding data
        barChartVeggie = findViewById(R.id.barChartVeggie);
        createBarChart(barChartVeggie, getGraphData());

        // Finds slider in activity page
        SeekBar veggieSeekBar = findViewById(R.id.seekBar);
        // Set a change listener on the SeekBar
        veggieSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // TextView that shows the amount of grams the user will add when pressing the add button
        veggieTvProgressLabel = findViewById(R.id.textView);
        veggieTvProgressLabel.setText("" + progress);
        veggieTvProgressLabel.setTextColor(Color.WHITE);
        veggieTvProgressLabel.setTextSize(15);

        // Informational textView, showing how many grams of veggies the user should still take
        // Of course, this remaining value cannot be a negative number
        veggieIntakeProgress = findViewById(R.id.intakeProgress);
        if ((500 - veggieTotalProgress) < 0) {
            remaining = 0;
        } else {
            remaining = 500 - veggieTotalProgress;
        }
        veggieIntakeProgress.setText("You ate " + veggieTotalProgress + " g of vegetables today out of the " +
                "recommended 500 g. Only " + remaining + " grams of vegetables remains.");
        veggieIntakeProgress.setTextColor(Color.WHITE);
        veggieIntakeProgress.setTextSize(20);

        // Informational textView for the amount of points you get for each checkpoint
        veggieCheckpoint = findViewById(R.id.checkpoint);
        veggieCheckpoint.setTextColor(Color.WHITE);
        veggieCheckpoint.setTextSize(20);

        // Adds a 'g' for grams at the end of the slider
        veggieGrams = findViewById(R.id.grams);
        veggieGrams.setTextColor(Color.WHITE);
        veggieGrams.setTextSize(20);

        // onClickListener for the add quantity button
        veggieButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the value from the slider and displays it in the textView below
                progress = veggieSeekBar.getProgress();
                veggieTvProgressLabel.setText("" + progress);

                // Add the value from the slider to the totalProgress
                veggieTotalProgress = veggieTotalProgress + progress;

                // Sets the progressbar to the new totalProgress
                veggieProgressBar.setProgress(veggieTotalProgress);

                // Updates the value in the database to the new totalProgress
                vegReference.setValue(veggieTotalProgress);

                // Informational textView, showing how many grams of veggies the user should
                // still take of course, this remaining value cannot be a negative number
                if ((500 - veggieTotalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - veggieTotalProgress;
                }
                veggieIntakeProgress.setText("You ate " + veggieTotalProgress +
                        " g of vegetables today out of the recommended 500 g. " +
                        "Only " + remaining + " grams of vegetables remains.");

                // Informational textView, showing how many grams of veggies the user should
                // still take of course, this remaining value cannot be a negative number
                if ((500 - veggieTotalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - veggieTotalProgress;
                }
                veggieIntakeProgress.setText("You ate " + veggieTotalProgress +
                        " g of vegetables today out of the recommended 500 g. " +
                        "Only " + remaining + " grams of vegetables remains.");

                //Informational textview about user's progress and checkpoints
                if (veggieTotalProgress >=500) {
                    ssb = new SpannableStringBuilder("Congratulations! You earned 500 points" +
                            " and have crossed all the checkpoints!");
                    veggieCheckpoint.setText(ssb);
                }
                else if (veggieTotalProgress <50) {
                    ssb = new SpannableStringBuilder("You will receive 25 points for the " +
                            "next checkpoint.");
                    veggieCheckpoint.setText(ssb);
                }
                else {
                    if (veggieTotalProgress >= 50 && veggieTotalProgress < 100) {
                        cp_number = 1;
                        cp_value = 50;
                        points_value = String.valueOf(50);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 25 points! ");
                        ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.RED);
                        ssb.setSpan(fcsRed, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (veggieTotalProgress >= 100 && veggieTotalProgress < 175) {
                        cp_number = 2;
                        cp_value = 100;
                        points_value = String.valueOf(100);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 50 points! ");
                        ForegroundColorSpan fcsOrange = new ForegroundColorSpan(Color.rgb(255,140,0));
                        ssb.setSpan(fcsOrange, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (veggieTotalProgress >= 175 && veggieTotalProgress < 275) {
                        cp_number = 3;
                        cp_value = 175;
                        points_value = String.valueOf(250);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 100 points! ");
                        ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.rgb(255,215,0));
                        ssb.setSpan(fcsYellow, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (veggieTotalProgress >= 275 && veggieTotalProgress < 400) {
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

                    veggieCheckpoint.setText(ssb);
                }

                // Sets the points
                setPoints(veggieTotalProgress, pointsVeggieReference);

                setTotalPoints(firebaseDatabase, userID);

                // Creates a new barChart
                createBarChart(barChartVeggie, getGraphData());
            }
        });

        //set the correct data paths
        setReferences();

        // If the value of amountOfVeg in the database changes
        // It takes the value from amountOfVeg in the database
        // And saves it in the variable totalProgress
        // And create a new barChart
        vegReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                veggieTotalProgress = dataFromDatabase;

                // Sets the progressbar to the totalProgress value in the database
                veggieProgressBar.setProgress(veggieTotalProgress);

                // Informational textView, showing how many grams of veggies the user should
                // still take of course, this remaining value cannot be a negative number
                if ((500 - veggieTotalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - veggieTotalProgress;
                }
                veggieIntakeProgress.setText("You ate " + veggieTotalProgress + " g of vegetables " +
                        "today out of the recommended 500 g. Only "
                        + remaining + " grams of vegetables remains.");
                Log.d("Fruitchecker", String.valueOf(dataFromDatabase));

                //Informational textview about user's progress and checkpoints
                if (veggieTotalProgress >=500) {
                    ssb = new SpannableStringBuilder("Congratulations! You earned 500 points" +
                            " and have crossed all the checkpoints!");
                    veggieCheckpoint.setText(ssb);
                }
                else if (veggieTotalProgress <50) {
                    ssb = new SpannableStringBuilder("You will receive 25 points for the " +
                            "next checkpoint.");
                    veggieCheckpoint.setText(ssb);
                }
                else {
                    if (veggieTotalProgress >= 50 && veggieTotalProgress < 100) {
                        cp_number = 1;
                        cp_value = 50;
                        points_value = String.valueOf(50);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 25 points! ");
                        ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.RED);
                        ssb.setSpan(fcsRed, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (veggieTotalProgress >= 100 && veggieTotalProgress < 175) {
                        cp_number = 2;
                        cp_value = 100;
                        points_value = String.valueOf(100);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 50 points! ");
                        ForegroundColorSpan fcsOrange = new ForegroundColorSpan(Color.rgb(255,140,0));
                        ssb.setSpan(fcsOrange, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (veggieTotalProgress >= 175 && veggieTotalProgress < 275) {
                        cp_number = 3;
                        cp_value = 175;
                        points_value = String.valueOf(250);
                        ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                                + cp_number + " - " + cp_value + " g and earned 100 points! ");
                        ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.rgb(255,215,0));
                        ssb.setSpan(fcsYellow, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else if (veggieTotalProgress >= 275 && veggieTotalProgress < 400) {
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

                    veggieCheckpoint.setText(ssb);
                }

                // Sets the points
                setPoints(veggieTotalProgress, pointsVeggieReference);

                setTotalPoints(firebaseDatabase, userID);

                // Creates a new barChart
                createBarChart(barChartVeggie, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("error", "loadPost:onCancelled", error.toException());
            }
        });

        // If the value of veggieMinusOne in the database changes
        // It takes the value from veggieMinusOne in the database
        // And saves it in the variable veggieMinusOne
        // And create a new barChart
        veggieMinusOneDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                veggieMinusOne = snapshot.getValue(int.class);
                createBarChart(barChartVeggie, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of veggieMinusTwo in the database changes
        // It takes the value from veggieMinusTwo in the database
        // And saves it in the variable veggieMinusTwo
        // And create a new barChart
        veggieMinusTwoDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                veggieMinusTwo = snapshot.getValue(int.class);
                createBarChart(barChartVeggie, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of veggieMinusThree in the database changes
        // It takes the value from veggieMinusThree in the database
        // And saves it in the variable veggieMinusThree
        // And create a new barChart
        veggieMinusThreeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                veggieMinusThree = snapshot.getValue(int.class);
                createBarChart(barChartVeggie, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of veggieMinusFour in the database changes
        // It takes the value from veggieMinusFour in the database
        // And saves it in the variable veggieMinusFour
        // And create a new barChart
        veggieMinusFourDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                veggieMinusFour = snapshot.getValue(int.class);
                createBarChart(barChartVeggie, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of veggieMinusFive in the database changes
        // It takes the value from veggieMinusFive in the database
        // And saves it in the variable veggieMinusFive
        // And create a new barChart
        veggieMinusFiveDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                veggieMinusFive = snapshot.getValue(int.class);
                createBarChart(barChartVeggie, getGraphData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of veggieMinusSix in the database changes
        // It takes the value from veggieMinusSix in the database
        // And saves it in the variable veggieMinusSix
        // And create a new barChart
        veggieMinusSixDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                veggieMinusSix = snapshot.getValue(int.class);
                createBarChart(barChartVeggie, getGraphData());
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
            // Updated continuously as the user slides the thumb
            veggieTvProgressLabel.setText("" + progress);
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
        graphData.add(new BarEntry(1, veggieMinusSix));
        graphData.add(new BarEntry(2, veggieMinusFive));
        graphData.add(new BarEntry(3, veggieMinusFour));
        graphData.add(new BarEntry(4, veggieMinusThree));
        graphData.add(new BarEntry(5, veggieMinusTwo));
        graphData.add(new BarEntry(6, veggieMinusOne));
        graphData.add(new BarEntry(7, veggieTotalProgress));

        return graphData;
    }

    // Every day at midnight the bar chart will get updated
    // This function makes sure the right data is swapped for the next day
    @Override
    public void switchDays() {
        //set the correct data paths
        setReferences();
        // Gives the value of veggieMinusFive to veggieMinusSix
        switchValues(veggieMinusFiveDatabaseReference, veggieMinusSixDatabaseReference);
        // Gives the value of veggieMinusFour to veggieMinusFive
        switchValues(veggieMinusFourDatabaseReference, veggieMinusFiveDatabaseReference);
        // Gives the value of veggieMinusThree to veggieMinusFour
        switchValues(veggieMinusThreeDatabaseReference, veggieMinusFourDatabaseReference);
        // Gives the value of veggieMinusTwo to veggieMinusThree
        switchValues(veggieMinusTwoDatabaseReference, veggieMinusThreeDatabaseReference);
        // Gives the value of veggieMinusOne to veggieMinusTwo
        switchValues(veggieMinusOneDatabaseReference, veggieMinusTwoDatabaseReference);
        // Gives the value of vegReference to veggieMinusOne
        switchValues(vegReference, veggieMinusOneDatabaseReference);
    }

    @Override
    public void setPoints(int totalProgress, DatabaseReference pointsReference) {
        pointsReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int points;
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(Objects.requireNonNull(task.getResult()).getValue()));

                    points = task.getResult().getValue(Integer.class);
                    //adds points to the total from the water page if these checkpoints are crossed
                    if (totalProgress >= 50 && totalProgress < 100) { points += 25; }
                    else if (totalProgress >= 100 && totalProgress < 175) { points += 50; }
                    else if (totalProgress >= 175 && totalProgress < 275) { points += 100; }
                    else if (totalProgress >= 275 && totalProgress < 500) { points += 250; }
                    else if (totalProgress >= 500) { points += 500; }
                    else if (totalProgress < 50) { points = 0; }
                    pointsReference.setValue(points);
                }
            }
        });
    }

    @Override
    public void setReferences() {
        // Give the right data path to the corresponding reference
        vegReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieWeek").child("amountOfVeg");
        pointsVeggieReference = firebaseDatabase.getReference().child("Users").child(userID).child("points").child("veggiePoints");
        veggieMinusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieWeek").child("veggieMinusOne");
        veggieMinusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieWeek").child("veggieMinusTwo");
        veggieMinusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieWeek").child("veggieMinusThree");
        veggieMinusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieWeek").child("veggieMinusFour");
        veggieMinusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieWeek").child("veggieMinusFive");
        veggieMinusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieWeek").child("veggieMinusSix");
    }
}