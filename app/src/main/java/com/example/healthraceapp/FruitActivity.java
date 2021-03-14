package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FruitActivity extends AppCompatActivity {
    // Stores the progress on the slider
    TextView tvProgressLabel;

    // Displays how much progress the user has made
    TextView intakeProgress;

    // Displays the points for each checkpoint
    TextView checkpoint;

    // Displays the entry of the user
    TextView grams;

    // Button to add water quantity
    Button buttonAdd;

    // Progress bar for user to enter water intake
    ProgressBar progressBar;

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

    //initialize instances for writing and reading data from the database
    private static final String TAG = "ViewDatabase";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
            "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private String userID = firebaseUser.getUid();
    User user = new User();

    // Database reference for all values in the bar chart
    private DatabaseReference fruitReference;
    private DatabaseReference minusOneDatabaseReference;
    private DatabaseReference minusTwoDatabaseReference;
    private DatabaseReference minusThreeDatabaseReference;
    private DatabaseReference minusFourDatabaseReference;
    private DatabaseReference minusFiveDatabaseReference;
    private DatabaseReference minusSixDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);

        // Finds progress bar in activity page
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Log.d("check", String.valueOf(totalProgress));
        // Sets the progress
        progressBar.setProgress(totalProgress);
        // Sets the maximum progress to 500 (grams of fruit)
        progressBar.setMax(500);
        // Finds add button in activity page
        buttonAdd = findViewById(R.id.buttonAdd);

        // Add bar chart to activity and enter the corresponding data
        barChartFruit = findViewById(R.id.barChartFruit);
        createBarChart();


        // Finds slider in activity page
        SeekBar seekBar = findViewById(R.id.seekBar);
        // set a change listener on the SeekBar
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // TextView that shows the amount of grams the user will add when pressing the add button
        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("" + progress);
        tvProgressLabel.setTextColor(Color.WHITE);
        tvProgressLabel.setTextSize(15);

        // Informational textView, showing how many grams of fruit the user should still take
        // Of course, this remaining value cannot be a negative number
        intakeProgress = findViewById(R.id.intakeProgress);
        if ((500 - totalProgress) < 0) {
            remaining = 0;
        } else {
            remaining = 500 - totalProgress;
        }
        intakeProgress.setText("You ate " + totalProgress + " g of fruits today out of the " +
                "recommended 500 g. Only " + remaining + " grams of fruit remains.");
        intakeProgress.setTextColor(Color.WHITE);
        intakeProgress.setTextSize(20);

        // Informational textView for the amount of points you get for each checkpoint
        checkpoint = findViewById(R.id.checkpoint);
        checkpoint.setTextColor(Color.WHITE);
        checkpoint.setTextSize(25);

        // Adds a 'g' for grams add the end of the slider
        grams = findViewById(R.id.grams);
        grams.setTextColor(Color.WHITE);
        grams.setTextSize(20);

        // onClickListener for the add quantity button
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the value from the slider and displays it in the textView below
                progress = seekBar.getProgress();
                tvProgressLabel.setText("" + progress);

                // Add the value from the slider to the totalProgress
                totalProgress = totalProgress + progress;
                // Sets the progressbar to the new totalProgress
                progressBar.setProgress(totalProgress);

                // Updates the value in the database to the new totalProgress
                fruitReference.setValue(totalProgress);

                // Informational textView, showing how many grams of fruit the user should
                // still take of course, this remaining value cannot be a negative number
                if ((500 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - totalProgress;
                }
                intakeProgress.setText("You ate " + totalProgress + " g of fruits today " +
                        "out of the recommended 500 g. Only " + remaining +
                        " grams of fruit remains.");

                // Creates a new barChart
                createBarChart();
            }
        });

        // Give the right data path to the corresponding reference
        fruitReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfFruit");
        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusSix");

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
                progressBar.setProgress(totalProgress);

                // TODO: check if this line can be deleted
                tvProgressLabel.setText("" + progress);

                // Informational textView, showing how many grams of fruit the user should
                // still take of course, this remaining value cannot be a negative number
                if ((500 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - totalProgress;
                }
                intakeProgress.setText("You ate " + totalProgress + " g of fruits today out " +
                        "of the recommended 500 g. Only " + remaining + " grams of fruit remains.");
                Log.d("Fruitchecker", String.valueOf(dataFromDatabase));

                // Creates a new barChart
                createBarChart();
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
        minusOneDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusOne = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusTwo in the database changes
        // It takes the value from fruitMinusTwo in the database
        // And saves it in the variable fruitMinusTwo
        // And create a new barChart
        minusTwoDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusTwo = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusThree in the database changes
        // It takes the value from fruitMinusThree in the database
        // And saves it in the variable fruitMinusThree
        // And create a new barChart
        minusThreeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusThree = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusFour in the database changes
        // It takes the value from fruitMinusFour in the database
        // And saves it in the variable fruitMinusFour
        // And create a new barChart
        minusFourDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusFour = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusFive in the database changes
        // It takes the value from fruitMinusFive in the database
        // And saves it in the variable fruitMinusFive
        // And create a new barChart
        minusFiveDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusFive = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusSix in the database changes
        // It takes the value from fruitMinusSix in the database
        // And saves it in the variable fruitMinusSix
        // And create a new barChart
        minusSixDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                fruitMinusSix = dataFromDatabase;
                createBarChart();
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
            tvProgressLabel.setText("" + progress);
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

    // Function the create the barChart and insert data into it
    public void createBarChart() {
        // ArrayList for the shown data
        ArrayList<BarEntry> graphData = new ArrayList<>();
        graphData.add(new BarEntry(1, fruitMinusSix));
        graphData.add(new BarEntry(2, fruitMinusFive));
        graphData.add(new BarEntry(3, fruitMinusFour));
        graphData.add(new BarEntry(4, fruitMinusThree));
        graphData.add(new BarEntry(5, fruitMinusTwo));
        graphData.add(new BarEntry(6, fruitMinusOne));
        graphData.add(new BarEntry(7, totalProgress));

        // Layout for the bar chart
        // Create a new dataset for the barChart with the graphData
        BarDataSet barDataSetFruit = new BarDataSet(graphData, "Days");
        // Set Bar Colors
        barDataSetFruit.setColors(ColorTemplate.MATERIAL_COLORS);
        // Set Text Color
        barDataSetFruit.setValueTextColor(Color.BLACK);
        // Set Text Size
        barDataSetFruit.setValueTextSize(16f);
        BarData barDataFruit = new BarData(barDataSetFruit);
        // Adds half of the bar width to each side of the x-axis range in order to
        // allow the bars of the barchart to be fully displayed
        barChartFruit.setFitBars(true);
        // Set a new data object for the barChart
        barChartFruit.setData(barDataFruit);
        // Set description
        barChartFruit.getDescription().setText("Fruit intake progress over the last 7 days");
        // The barChart will show a vertical animation with a duration of 200 ms
        // every time the data changes
        barChartFruit.animateY(200);
    }

    // Every day at midnight the bar chart will get updated
    // This function makes sure the right data is swapped for the next day
    public void switchDays() {

        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("fruitMinusSix");
        fruitReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfFruit");

        // Gives the value of fruitMinusFive to fruitMinusSix
        minusFiveDatabaseReference.get().addOnCompleteListener(
                                                            new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    minusSixDatabaseReference.setValue(task.getResult().getValue());
                }
            }
        }
        );

        // Gives the value of fruitMinusFour to fruitMinusFive
        minusFourDatabaseReference.get().addOnCompleteListener(
                                                            new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    minusFiveDatabaseReference.setValue(task.getResult().getValue());
                }
            }
        }
        );

        // Gives the value of fruitMinusThree to fruitMinusFour
        minusThreeDatabaseReference.get().addOnCompleteListener(
                                                            new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    minusFourDatabaseReference.setValue(task.getResult().getValue());
                }
            }
        }
        );

        // Gives the value of fruitMinusTwo to fruitMinusThree
        minusTwoDatabaseReference.get().addOnCompleteListener(
                                                            new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    minusThreeDatabaseReference.setValue(task.getResult().getValue());
                }
            }
        }
        );

        // Gives the value of fruitMinusOne to fruitMinusTwo
        minusOneDatabaseReference.get().addOnCompleteListener(
                                                            new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    minusTwoDatabaseReference.setValue(task.getResult().getValue());
                }
            }
        }
        );

        // Gives the value of totalProgress to fruitMinusOne
        fruitReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    minusOneDatabaseReference.setValue(task.getResult().getValue());
                }
            }
        }
        );
    }
}
