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
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VegetableActivity extends AppCompatActivity implements Intake{

    private static final String TAG = "";

    // Stores the progress on the slider
    TextView tvProgressLabel;

    // Displays how much progress the user has made
    TextView intakeProgress;

    // Displays the points for each checkpoint
    TextView checkpoint;

    // TextView for the 'g' for grams after the slider
    TextView grams;

    // Button to add water quantity
    Button buttonAdd;

    // Progress bar for user to enter water intake
    ProgressBar progressBar;

    // Initiate total progress
    static int totalProgress;

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

    // Initialize instances for writing and reading data from the database
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
            "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private String userID = firebaseUser.getUid();
    User user = new User();

    // Database reference for all values in the bar chart
    private DatabaseReference vegReference;
    private DatabaseReference pointsVeggieReference;
    private DatabaseReference minusOneDatabaseReference;
    private DatabaseReference minusTwoDatabaseReference;
    private DatabaseReference minusThreeDatabaseReference;
    private DatabaseReference minusFourDatabaseReference;
    private DatabaseReference minusFiveDatabaseReference;
    private DatabaseReference minusSixDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetable);

        // Finds progress bar in activity page
        progressBar = findViewById(R.id.progressBar);
        // Sets the progress
        progressBar.setProgress(totalProgress);
        // Sets the maximum progress to 500 (grams of vegetables)
        progressBar.setMax(500);

        // Finds add button in activity page
        buttonAdd = findViewById(R.id.buttonAdd);

        // Add bar chart to activity and enter the corresponding data
        barChartVeggie = findViewById(R.id.barChartVeggie);
        createBarChart(barChartVeggie, getGraphData());

        // Finds slider in activity page
        SeekBar seekBar = findViewById(R.id.seekBar);
        // Set a change listener on the SeekBar
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // TextView that shows the amount of grams the user will add when pressing the add button
        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("" + progress);
        tvProgressLabel.setTextColor(Color.WHITE);
        tvProgressLabel.setTextSize(15);

        // Informational textView, showing how many grams of veggies the user should still take
        // Of course, this remaining value cannot be a negative number
        intakeProgress = findViewById(R.id.intakeProgress);
        if ((500 - totalProgress) < 0) {
            remaining = 0;
        } else {
            remaining = 500 - totalProgress;
        }
        intakeProgress.setText("You ate " + totalProgress + " g of vegetables today out of the " +
                "recommended 500 g. Only " + remaining + " grams of vegetables remains.");
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
                vegReference.setValue(totalProgress);

                // Informational textView, showing how many grams of veggies the user should
                // still take of course, this remaining value cannot be a negative number
                if ((500 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - totalProgress;
                }
                intakeProgress.setText("You ate " + totalProgress +
                        " g of vegetables today out of the recommended 500 g. " +
                        "Only " + remaining + " grams of vegetables remains.");

                // Sets the points
                setPoints(totalProgress, pointsVeggieReference);

                // Creates a new barChart
                createBarChart(barChartVeggie, getGraphData());
            }
        });

        // Give the right data path to the corresponding reference
        vegReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfVeg");
        pointsVeggieReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggiePoints");
        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusSix");

        // If the value of amountOfVeg in the database changes
        // It takes the value from amountOfVeg in the database
        // And saves it in the variable totalProgress
        // And create a new barChart
        vegReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                totalProgress = dataFromDatabase;

                // Sets the progressbar to the totalProgress value in the database
                progressBar.setProgress(totalProgress);

                // Informational textView, showing how many grams of veggies the user should
                // still take of course, this remaining value cannot be a negative number
                if ((500 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - totalProgress;
                }
                intakeProgress.setText("You ate " + totalProgress + " g of vegetables " +
                        "today out of the recommended 500 g. Only "
                        + remaining + " grams of vegetables remains.");
                Log.d("Fruitchecker", String.valueOf(dataFromDatabase));

                // Sets the points
                setPoints(totalProgress, pointsVeggieReference);

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
        minusOneDatabaseReference.addValueEventListener(new ValueEventListener() {
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
        minusTwoDatabaseReference.addValueEventListener(new ValueEventListener() {
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
        minusThreeDatabaseReference.addValueEventListener(new ValueEventListener() {
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
        minusFourDatabaseReference.addValueEventListener(new ValueEventListener() {
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
        minusFiveDatabaseReference.addValueEventListener(new ValueEventListener() {
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
        minusSixDatabaseReference.addValueEventListener(new ValueEventListener() {
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
        graphData.add(new BarEntry(7, totalProgress));

        return graphData;
    }

    // Every day at midnight the bar chart will get updated
    // This function makes sure the right data is swapped for the next day
    @Override
    public void switchDays() {
        // Give the right data path to the corresponding reference
        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusSix");
        vegReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfVeg");

        // Gives the value of veggieMinusFive to veggieMinusSix
        switchValues(minusFiveDatabaseReference, minusSixDatabaseReference);
        // Gives the value of veggieMinusFour to veggieMinusFive
        switchValues(minusFourDatabaseReference, minusFiveDatabaseReference);
        // Gives the value of veggieMinusThree to veggieMinusFour
        switchValues(minusThreeDatabaseReference, minusFourDatabaseReference);
        // Gives the value of veggieMinusTwo to veggieMinusThree
        switchValues(minusTwoDatabaseReference, minusThreeDatabaseReference);
        // Gives the value of veggieMinusOne to veggieMinusTwo
        switchValues(minusOneDatabaseReference, minusTwoDatabaseReference);
        // Gives the value of vegReference to veggieMinusOne
        switchValues(vegReference, minusOneDatabaseReference);
    }

    @Override
    public void setPoints(int totalProgress, DatabaseReference pointsReference) {
        //adds points to the total from the water page if these checkpoints are crossed
        int points = 0;
        if (totalProgress >= 50 && totalProgress < 100) {
            points = 25;

            //setting value of points received for next checkpoint
//            cp = String.valueOf(50);
        }
        else if (totalProgress >= 100 && totalProgress < 175) {
            points = 75;
//            cp = String.valueOf(100);
        }
        else if (totalProgress >= 175 && totalProgress < 275) {
            points = 175;
//            cp = String.valueOf(250);
        }
        else if (totalProgress >= 275 && totalProgress < 500) {
            points = 425;
//            cp = String.valueOf(500);
        }
        else if (totalProgress >= 500) {
            points = 925;
//            s = "You have crossed all the checkpoints!";
        }
        else if (totalProgress-progress==0 || totalProgress < 50) {
            points = 0;
//            cp = String.valueOf(25);
        }
//        checkpoint.setText(s);
        pointsReference.setValue(points);
    }
}