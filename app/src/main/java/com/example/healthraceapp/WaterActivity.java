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

    TextView tvProgressLabel; //stores the progress on the slider
    TextView waterProgress; //displays how much progress the user has made
    TextView checkpoint; //displays the points for each checkpoint
    TextView milliliters;
    Button buttonAdd; //button to add water quantity
    ProgressBar progressBar; //progress bar for user to enter water intake
    int totalProgress; //initiate total progress
    int progress; //initiate progress value

    // Initiate bar chart
    BarChart barChartWater;

    // Initialize values for barChart
    long waterMinusOne;
    long waterMinusTwo;
    long waterMinusThree;
    long waterMinusFour;
    long waterMinusFive;
    long waterMinusSix;


    private DatabaseReference dailyDatabaseReference;
    private DatabaseReference minusOneDatabaseReference;
    private DatabaseReference minusTwoDatabaseReference;
    private DatabaseReference minusThreeDatabaseReference;
    private DatabaseReference minusFourDatabaseReference;
    private DatabaseReference minusFiveDatabaseReference;
    private DatabaseReference minusSixDatabaseReference;

    // Initialize value for information text view
    int remaining = 2000;

    //initialize instances for writing and reading data from the database
    private static final String TAG = "ViewDatabase";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
            "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
    private DatabaseReference waterReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private String userID = firebaseUser.getUid();
    User user = new User();




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

        waterReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfWater");
        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusSix");

        waterReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                totalProgress = dataFromDatabase;
                progressBar.setProgress(totalProgress);
                tvProgressLabel.setText("" + progress);
            //    waterReference.setValue(totalProgress);
                if ((2000 - totalProgress) < 0) {
                    remaining = 0;
                } else {
                    remaining = 2000 - totalProgress;
                }
                waterProgress.setText("You drank " + totalProgress + " ml of water today out of the " +
                        "recommended 2000 ml. Only " + remaining + " ml of water remains.");
                Log.d("Fruitchecker", String.valueOf(dataFromDatabase));
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("error", "loadPost:onCancelled", error.toException());
            }
        });
        Log.d("waterCheck", String.valueOf(totalProgress));

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

        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("waterMinusSix");
        waterReference = firebaseDatabase.getReference().child("Users").child(userID).child("amountOfWater");


        minusFiveDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

        minusFourDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

        minusThreeDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

        minusTwoDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

        minusOneDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

        waterReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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