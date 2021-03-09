package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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

import java.util.ArrayList;

public class WaterActivity extends AppCompatActivity {

    TextView tvProgressLabel; //stores the progress on the slider
    TextView waterProgress; //displays how much progress the user has made
    TextView checkpoint; //displays the points for each checkpoint
    TextView milliliters;
    Button buttonAdd; //button to add water quantity
    ProgressBar progressBar; //progress bar for user to enter water intake
    static int totalProgress; //initiate total progress
    int progress; //initiate progress value

    // Initiate bar chart
    BarChart barChartWater;

    // Initialize values for barChart
    int waterMinusOne = 0;
    int waterMinusTwo = 0;
    int waterMinusThree = 0;
    int waterMinusFour = 0;
    int waterMinusFive = 0;
    int waterMinusSix = 0;

    // Initialize value for information text view
    int remaining = 2000;

    //initialize instances for writing and reading data from the database
    private static final String TAG = "ViewDatabase";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference waterReference;
    private FirebaseAuth firebaseAuth;
    private String userID;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        progressBar = findViewById(R.id.progressBar); //finds progress bar in activity page
        progressBar.setProgress(totalProgress);
        progressBar.setMax(2000);
        buttonAdd = findViewById(R.id.buttonAdd); //finds add button in activity page

        barChartWater = findViewById(R.id.barChartWater);
        createBarChart();

        // set a change listener on the SeekBar
        SeekBar seekBar = findViewById(R.id.seekBar); //finds slider in activity page
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("" + progress);
        tvProgressLabel.setTextColor(Color.WHITE);
        tvProgressLabel.setTextSize(15);

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

        checkpoint = findViewById(R.id.checkpoint);
        checkpoint.setTextColor(Color.WHITE);
        checkpoint.setTextSize(25);

        milliliters = findViewById(R.id.ml);
        milliliters.setTextColor(Color.WHITE);
        milliliters.setTextSize(20);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = seekBar.getProgress();
                totalProgress = totalProgress + progress;
                progressBar.setProgress(totalProgress);
                tvProgressLabel.setText("" + progress);
                waterReference.setValue(totalProgress);

                if ((2000 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 2000 - totalProgress;
                }
                waterProgress.setText("You drank " + totalProgress + " ml of water today out of the " +
                        "recommended 2000 ml. Only " + remaining + " ml of water remains.");

                createBarChart();
            }
        });

        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
                "race-app-default-rtdb.europe-west1.firebasedatabase.app/");

        waterReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfWater");

        waterReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int waterFromDatabase = snapshot.getValue(int.class);
                totalProgress = waterFromDatabase;
                progressBar.setProgress(totalProgress);
                tvProgressLabel.setText("" + progress);
                waterReference.setValue(totalProgress);
                if ((2000 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 2000 - totalProgress;
                }
                waterProgress.setText("You drank " + totalProgress + " ml of water today out of the " +
                        "recommended 2000 ml. Only " + remaining + " ml of water remains.");
                Log.d("Fruitchecker", String.valueOf(waterFromDatabase));
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
        BarDataSet barDataSetWater = new BarDataSet(graphData, "Days");
        barDataSetWater.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSetWater.setValueTextColor(Color.BLACK);
        barDataSetWater.setValueTextSize(16f);
        BarData barDataWater = new BarData(barDataSetWater);
        barChartWater.setFitBars(true);
        barChartWater.setData(barDataWater);
        barChartWater.getDescription().setText("Water intake progress over the last 7 days");
        barChartWater.animateY(200);
    }

    // Every day at midnight the bar chart will get updated
    // This function makes sure the right data is swapped for the next day
    public void switchDays() {
        waterMinusSix = waterMinusFive;
        waterMinusFive = waterMinusFour;
        waterMinusFour = waterMinusThree;
        waterMinusThree = waterMinusTwo;
        waterMinusTwo = waterMinusOne;
        waterMinusOne = progress;
        totalProgress = 0;
    }

}