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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

import java.util.ArrayList;

public class VegetableActivity extends AppCompatActivity {

    private static final String TAG = "";
    TextView tvProgressLabel; //stores the progress on the slider
    TextView intakeProgress; //displays how much progress the user has made
    TextView checkpoint; //displays the points for each checkpoint
    TextView grams;
    Button buttonAdd; //button to add water quantity
    ProgressBar progressBar; //progress bar for user to enter water intake
    static int totalProgress; //initiate total progress
    int progress; //initiate progress value

    // Initiate bar chart
    BarChart barChartVeggie;

    // Initialize values for barChart
    int veggieMinusOne = 0;
    int veggieMinusTwo = 0;
    int veggieMinusThree = 0;
    int veggieMinusFour = 0;
    int veggieMinusFive = 0;
    int veggieMinusSix = 0;

    // Initialize value for information text view
    int remaining = 500;

    //initialize instances for writing and reading data from the database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference vegReference;
    private FirebaseAuth firebaseAuth;
    private String userID;
    User user;

    private DatabaseReference dailyDatabaseReference;
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

        progressBar = findViewById(R.id.progressBar); //finds progress bar in activity page
        progressBar.setProgress(totalProgress);
        progressBar.setMax(500);
        buttonAdd = findViewById(R.id.buttonAdd); //finds add button in activity page

        barChartVeggie = findViewById(R.id.barChartVeggie);
        createBarChart();

        // set a change listener on the SeekBar
        SeekBar seekBar = findViewById(R.id.seekBar); //finds slider in activity page
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

//        TickSeekBar slider = findViewById(R.id.slider);
//        slider.setOnSeekChangeListener(sliderChangeListener);


        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("" + progress);
        tvProgressLabel.setTextColor(Color.WHITE);
        tvProgressLabel.setTextSize(15);

        intakeProgress = findViewById(R.id.intakeProgress);

        if ((500 - totalProgress) < 0) {
            remaining = 0;
        } else {
            remaining = 500 - totalProgress;
        }
        intakeProgress.setText("You ate " + totalProgress + " g of vegetables today out of the " +
                "recommended 500 g. Only " + remaining + " grams of vegetables remains.");
//        intakeProgress.setText("Test");
        intakeProgress.setTextColor(Color.WHITE);
        intakeProgress.setTextSize(20);

        checkpoint = findViewById(R.id.checkpoint);
        checkpoint.setTextColor(Color.WHITE);
        checkpoint.setTextSize(25);

        grams = findViewById(R.id.grams);
        grams.setTextColor(Color.WHITE);
        grams.setTextSize(20);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = seekBar.getProgress();
                totalProgress = totalProgress + progress;
                progressBar.setProgress(totalProgress);
                tvProgressLabel.setText("" + progress);
                vegReference.setValue(totalProgress);
                if ((500 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - totalProgress;
                }
                intakeProgress.setText("You ate " + totalProgress + " g of vegetables today out of the " +
                        "recommended 500 g. Only " + remaining + " grams of vegetables remains.");

                createBarChart();
            }
        });

        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
                "race-app-default-rtdb.europe-west1.firebasedatabase.app/");

        vegReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfVeg");
        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("veggieMinusSix");

        vegReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                totalProgress = dataFromDatabase;
                progressBar.setProgress(totalProgress);
                tvProgressLabel.setText("" + progress);
                vegReference.setValue(totalProgress);
                if ((500 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 500 - totalProgress;
                }
                intakeProgress.setText("You ate " + totalProgress + " g of vegetables today out of the " +
                        "recommended 500 g. Only " + remaining + " grams of vegetables remains.");
                Log.d("Fruitchecker", String.valueOf(dataFromDatabase));
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("error", "loadPost:onCancelled", error.toException());
            }
        });

        minusOneDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                veggieMinusOne = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        minusTwoDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                veggieMinusTwo = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        minusThreeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                veggieMinusThree = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        minusFourDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                veggieMinusFour = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        minusFiveDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                veggieMinusFive = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        minusSixDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                veggieMinusSix = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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

//    TickSeekBar sliderChangeListener = new OnSeekChangeListener() {
//        @Override
//        public void onSeeking(SeekParams seekParams) {
//            Log.i(TAG, seekParams.seekBar);
//            Log.i(TAG, seekParams.progress);
//            Log.i(TAG, seekParams.progressFloat);
//            Log.i(TAG, seekParams.fromUser);
//            //when tick count > 0
//            Log.i(TAG, seekParams.thumbPosition);
//            Log.i(TAG, seekParams.tickText);
//        }
//
//        @Override
//        public void onSeeking(SeekParams seekParams) {
//
//        }
//
//        @Override
//        public void onStartTrackingTouch(TickSeekBar seekBar) {
//        }
//
//        @Override
//        public void onStopTrackingTouch(TickSeekBar seekBar) {
//        }
//
//    });

    public void createBarChart() {
    // ArrayList for the shown data
        ArrayList<BarEntry> graphData = new ArrayList<>();
        graphData.add(new BarEntry(1, veggieMinusSix));
        graphData.add(new BarEntry(2, veggieMinusFive));
        graphData.add(new BarEntry(3, veggieMinusFour));
        graphData.add(new BarEntry(4, veggieMinusThree));
        graphData.add(new BarEntry(5, veggieMinusTwo));
        graphData.add(new BarEntry(6, veggieMinusOne));
        graphData.add(new BarEntry(7, totalProgress));

        // Layout for the bar chart
        BarDataSet barDataSetVeggie = new BarDataSet(graphData, "Days");
        barDataSetVeggie.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSetVeggie.setValueTextColor(Color.BLACK);
        barDataSetVeggie.setValueTextSize(16f);
        BarData barDataVeggie = new BarData(barDataSetVeggie);
        barChartVeggie.setFitBars(true);
        barChartVeggie.setData(barDataVeggie);
        barChartVeggie.getDescription().setText("Vegetable intake progress over the last 7 days");
        barChartVeggie.animateY(200);
    }

    // Every day at midnight the bar chart will get updated
    // This function makes sure the right data is swapped for the next day
    public void switchDays() {
        veggieMinusSix = veggieMinusFive;
        veggieMinusFive = veggieMinusFour;
        veggieMinusFour = veggieMinusThree;
        veggieMinusThree = veggieMinusTwo;
        veggieMinusTwo = veggieMinusOne;
        veggieMinusOne = progress;
        totalProgress = 0;
    }

}