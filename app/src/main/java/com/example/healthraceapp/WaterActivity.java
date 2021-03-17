package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
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

public class WaterActivity extends AppCompatActivity {
    // Stores the progress on the slider
    TextView tvProgressLabel;

    // Displays how much progress the user has made
    TextView waterProgress;

    // Displays the points for each checkpoint
    TextView checkpoint;

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
    private DatabaseReference minusOneDatabaseReference;
    private DatabaseReference minusTwoDatabaseReference;
    private DatabaseReference minusThreeDatabaseReference;
    private DatabaseReference minusFourDatabaseReference;
    private DatabaseReference minusFiveDatabaseReference;
    private DatabaseReference minusSixDatabaseReference;

    // Initialize value for information text view
    int remaining = 2000;

    // Initialize instances for writing and reading data from the database
    private static final String TAG = "ViewDatabase";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
            "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private String userID = firebaseUser.getUid();
    User user = new User();

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
        createBarChart();

        // Finds slider in activity page
        SeekBar seekBar = findViewById(R.id.seekBar);
        // Set a change listener on the SeekBar
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // TextView that shows the amount of ml's the user will add when pressing the add button
        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("" + progress);
        tvProgressLabel.setTextColor(Color.WHITE);
        tvProgressLabel.setTextSize(15);

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
        checkpoint = findViewById(R.id.checkpoint);
        checkpoint.setTextColor(Color.WHITE);
        checkpoint.setTextSize(25);

        // Adds a 'ml' for milliliter add the end of the slider
        milliliters = findViewById(R.id.ml);
        milliliters.setTextColor(Color.WHITE);
        milliliters.setTextSize(20);

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

                // Creates a new barChart
                createBarChart();
            }
        });

        // Give the right data path to the corresponding reference
        waterReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfWater");
        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusSix");

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

                // Creates a new barChart
                createBarChart();
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
        minusOneDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                waterMinusOne = dataFromDatabase;
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
                waterMinusTwo = dataFromDatabase;
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
                waterMinusThree = dataFromDatabase;
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
                waterMinusFour = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of fruitMinusFive in the database changes
        // It takes the value from fruitMinusFive in the database
        // And saves it in the variable fruitMinusFive
        // And create a new barChartFive
        minusFiveDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                waterMinusFive = dataFromDatabase;
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
                waterMinusSix = dataFromDatabase;
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

    public void setBarChart(DatabaseReference ds, int day) {
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int value = snapshot.getValue(int.class);
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Function the create the barChart and insert data into it
    public void createBarChart() {
        // ArrayList for the shown data
        ArrayList<BarEntry> graphData = new ArrayList<>();
        graphData.add(new BarEntry(1, waterMinusSix));
        graphData.add(new BarEntry(2, waterMinusFive));
        graphData.add(new BarEntry(3, waterMinusFour));
        graphData.add(new BarEntry(4, waterMinusThree));
        graphData.add(new BarEntry(5, waterMinusTwo));
        graphData.add(new BarEntry(6, waterMinusOne));
        graphData.add(new BarEntry(7, totalProgress));

        // Layout for the bar chart
        // Create a new dataset for the barChart with the graphData
        BarDataSet barDataSetWater = new BarDataSet(graphData, "Days");
        // Set Bar Colors
        barDataSetWater.setColors(ColorTemplate.MATERIAL_COLORS);
        // Set Text Color
        barDataSetWater.setValueTextColor(Color.BLACK);
        // Set Text Size
        barDataSetWater.setValueTextSize(16f);
        BarData barDataWater = new BarData(barDataSetWater);
        // Adds half of the bar width to each side of the x-axis range in order to
        // allow the bars of the barchart to be fully displayed
        barChartWater.setFitBars(true);
        // Set a new data object for the barChart
        barChartWater.setData(barDataWater);
        // Set description
        barChartWater.getDescription().setText("Water intake progress over the last 7 days");
        // The barChart will show a vertical animation with a duration of 200 ms
        // every time the data changes
        barChartWater.animateY(200);
    }

    //This function copies the value of database reference ds1 to ds2
    public void switchValues(DatabaseReference ds1, DatabaseReference ds2) {
        ds1.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    ds2.setValue(task.getResult().getValue());
                }
            }
        });
    }

    // Every day at midnight the bar chart will get updated
    // This function makes sure the right data is swapped for the next day
    public void switchDays() {
        // Give the right data path to the corresponding reference
        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusSix");
        waterReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfWater");

        // Gives the value of waterMinusFive to waterMinusSix
        switchValues(minusFiveDatabaseReference, minusSixDatabaseReference);
        // Gives the value of waterMinusFour to waterMinusFive
        switchValues(minusFourDatabaseReference, minusFiveDatabaseReference);
        // Gives the value of waterMinusThree to waterMinusFour
        switchValues(minusThreeDatabaseReference, minusFourDatabaseReference);
        // Gives the value of waterMinusTwo to waterMinusThree
        switchValues(minusTwoDatabaseReference, minusThreeDatabaseReference);
        // Gives the value of waterMinusOne to waterMinusTwo
        switchValues(minusOneDatabaseReference, minusTwoDatabaseReference);
        // Gives the value of waterReference to waterMinusOne
        switchValues(waterReference, minusOneDatabaseReference);

//        // Gives the value of fruitMinusFive to fruitMinusSix
//        minusFiveDatabaseReference.get().addOnCompleteListener(
//                                                            new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    minusSixDatabaseReference.setValue(task.getResult().getValue());
//                }
//            }
//        }
//        );
//
//        // Gives the value of fruitMinusFour to fruitMinusFive
//        minusFourDatabaseReference.get().addOnCompleteListener(
//                                                            new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    minusFiveDatabaseReference.setValue(task.getResult().getValue());
//                }
//            }
//        }
//        );
//
//        // Gives the value of fruitMinusThree to fruitMinusFour
//        minusThreeDatabaseReference.get().addOnCompleteListener(
//                                                            new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    minusFourDatabaseReference.setValue(task.getResult().getValue());
//                }
//            }
//        }
//        );
//        // Gives the value of fruitMinusTwo to fruitMinusThree
//        minusTwoDatabaseReference.get().addOnCompleteListener(
//                                                            new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    minusThreeDatabaseReference.setValue(task.getResult().getValue());
//                }
//            }
//        }
//        );
//
//        // Gives the value of fruitMinusOne to fruitMinusTwo
//        minusOneDatabaseReference.get().addOnCompleteListener(
//                                                            new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    minusTwoDatabaseReference.setValue(task.getResult().getValue());
//                }
//            }
//        }
//        );
//
//        // Gives the value of totalProgress to fruitMinusOne
//        waterReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    minusOneDatabaseReference.setValue(task.getResult().getValue());
//                }
//            }
//        }
//        );
    }
}