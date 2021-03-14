package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.PeriodicSync;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;

public class StepActivity extends AppCompatActivity implements SensorEventListener {
    // If permission to use physical activity is granted
    private int PERMISSION_CODE = 1;

    // Text field for the amount of stepCounter
    private TextView textViewStepCounter;

    // Text field to display remaining progress of user
    private TextView progress;

    // Text field to display the points for each checkpoint
    private TextView checkpoint;

    // Text field for the amount of steps of stepDetector
    private static TextView textViewStepDetector;

    // The sensor manager for the step counter
    private SensorManager sensorManager;

    // The step counter and detector sensor
    private Sensor myStepCounter, myStepDetector;

    // If the device has a step counter
    private boolean isCounterSensorPresent;

    // If the device has a step counter
    private boolean isDetectorSensorPresent;

    // Initial stepCount
    int stepCount = 0;

    // Initial stepDetect
    public static int stepDetect;

    // Initiate the progress bar
    ProgressBar simpleProgressBar;

    // Initiate bar chart
    BarChart barChartStep;

    // Initialize value for information text view
    int remaining = 7000;

    // Initialize values for barChart, minusN symbolizes the amount of days since the current day
    int stepDetectMinusOne;
    int stepDetectMinusTwo;
    int stepDetectMinusThree;
    int stepDetectMinusFour;
    int stepDetectMinusFive;
    int stepDetectMinusSix;

    // Database reference for all values in the bar chart
    private DatabaseReference stepReference;
    private DatabaseReference minusOneDatabaseReference;
    private DatabaseReference minusTwoDatabaseReference;
    private DatabaseReference minusThreeDatabaseReference;
    private DatabaseReference minusFourDatabaseReference;
    private DatabaseReference minusFiveDatabaseReference;
    private DatabaseReference minusSixDatabaseReference;

    // initialize instances for writing and reading data from the database
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
            "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private String userID = firebaseUser.getUid();
    User user = new User();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Ask with a popup for permission to track physical activity
        ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_CODE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Add stepCounter counter to activity
        textViewStepCounter = findViewById(R.id.textViewStepCounter);

        // Add stepDetector counter to activity
        textViewStepDetector = findViewById(R.id.textViewStepDetector);

        // Add bar chart to activity and enter the corresponding data
        barChartStep = findViewById(R.id.barChartStep);
        createBarChart();

        // Give the right data path to the corresponding reference
        stepReference = firebaseDatabase.getReference().child("Users").child(userID).child("dailyNumberOfSteps");
        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusSix");

        // If the value of stepDetectMinusOne in the database changes
        // It takes the value from stepDetectMinusOne in the database
        // And saves it in the variable stepDetectMinusOne
        // And create a new barChart
        minusOneDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stepDetectMinusOne = snapshot.getValue(int.class);
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of stepDetectMinusTwo in the database changes
        // It takes the value from stepDetectMinusTwo in the database
        // And saves it in the variable stepDetectMinusTwo
        // And create a new barChart
        minusTwoDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stepDetectMinusTwo = snapshot.getValue(int.class);
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of stepDetectMinusThree in the database changes
        // It takes the value from stepDetectMinusThree in the database
        // And saves it in the variable stepDetectMinusThree
        // And create a new barChart
        minusThreeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stepDetectMinusThree = snapshot.getValue(int.class);
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of stepDetectMinusFour in the database changes
        // It takes the value from stepDetectMinusFour in the database
        // And saves it in the variable stepDetectMinusFour
        // And create a new barChart
        minusFourDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stepDetectMinusFour = snapshot.getValue(int.class);
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of stepDetectMinusFive in the database changes
        // It takes the value from stepDetectMinusFive in the database
        // And saves it in the variable stepDetectMinusFive
        // And create a new barChart
        minusFiveDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stepDetectMinusFive = snapshot.getValue(int.class);
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of stepDetectMinusSix in the database changes
        // It takes the value from stepDetectMinusSix in the database
        // And saves it in the variable stepDetectMinusSix
        // And create a new barChart
        minusSixDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dataFromDatabase = snapshot.getValue(int.class);
                stepDetectMinusSix = dataFromDatabase;
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of dailyNumberOfSteps in the database changes
        // It takes the value from dailyNumberOfSteps in the database
        // And saves it in the variable stepDetect
        stepReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stepDetect = snapshot.getValue(int.class);

                // Show value of stepDetect in textView
                textViewStepDetector.setText(String.valueOf(stepDetect));

                // Update progressBar with the new value
                simpleProgressBar.setProgress(stepDetect);

                // Informational textView, showing how many steps the user should still take
                // Of course, this remaining value cannot be a negative number
                if ((7000 - stepDetect) < 0) {
                    remaining = 0;
                } else {
                    remaining = 7000 - stepDetect;
                }
                progress.setText("You walked " + stepDetect + " steps today out of the " +
                                 "recommended 7000 per day. Only " + remaining +
                                 " steps remain till the next checkpoint.");

                // Create a new barChart
                createBarChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("error", "loadPost:onCancelled", error.toException());
            }
        });

        // Give the user information about their step progress in a text view
        progress = findViewById(R.id.progress);

        // Informational textView, showing how many steps the user should still take
        // Of course, this remaining value cannot be a negative number
        if ((7000 - stepDetect) < 0) {
            remaining = 0;
        } else {
            remaining = 7000 - stepDetect;
        }
        progress.setText("You walked " + stepDetect + " steps today out of the " +
                         "recommended 7000 per day. Only " + remaining +
                         " steps remain till the next checkpoint.");

        // Set the layout for this information text view
        progress.setTextColor(Color.WHITE);
        progress.setTextSize(15);
        checkpoint = findViewById(R.id.checkpoint);
        checkpoint.setTextColor(Color.WHITE);
        checkpoint.setTextSize(25);

        // TODO: add comment
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Initiate the progressBar
        prog();

        // Check if step counter is present in device
        // If it is not present, the textView will notify the user
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            myStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else {
            textViewStepCounter.setText("No stepCounter detected");
            isCounterSensorPresent = false;
        }

        // Check if step detector is present in device
        // If it is not present, the textView will notify the user
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            myStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            simpleProgressBar.setProgress(stepDetect);
            isDetectorSensorPresent = true;
        } else {
            //for testing purposes
            textViewStepDetector.setText(String.valueOf(stepDetect));
            simpleProgressBar.setProgress(stepDetect);
            textViewStepDetector.setText("Detector sensor is not present");
            isDetectorSensorPresent = false;
        }
    }

    // Function the create the barChart and insert data into it
    public void createBarChart() {
        // ArrayList for the shown data
        ArrayList<BarEntry> graphData = new ArrayList<>();
        graphData.add(new BarEntry(1, stepDetectMinusSix));
        graphData.add(new BarEntry(2, stepDetectMinusFive));
        graphData.add(new BarEntry(3, stepDetectMinusFour));
        graphData.add(new BarEntry(4, stepDetectMinusThree));
        graphData.add(new BarEntry(5, stepDetectMinusTwo));
        graphData.add(new BarEntry(6, stepDetectMinusOne));
        graphData.add(new BarEntry(7, stepDetect));

        // Layout for the bar chart
        // Create a new dataset for the barChart with the graphData
        BarDataSet barDataSetStep = new BarDataSet(graphData, "Days");
        // Set Bar Colors
        barDataSetStep.setColors(ColorTemplate.MATERIAL_COLORS);
        // Set Text Color
        barDataSetStep.setValueTextColor(Color.BLACK);
        // Set Text Size
        barDataSetStep.setValueTextSize(16f);
        BarData barDataStep = new BarData(barDataSetStep);
        // Adds half of the bar width to each side of the x-axis range in order to
        // allow the bars of the barchart to be fully displayed
        barChartStep.setFitBars(true);
        // Set a new data object for the barChart
        barChartStep.setData(barDataStep);
        // Set description
        barChartStep.getDescription().setText("Step progress over the last 7 days");
        // The barChart will show a vertical animation with a duration of 200 ms
        // every time the data changes
        barChartStep.animateY(200);
    }

    // Every day at midnight the bar chart will get updated
    // This function makes sure the right data is swapped for the next day
    public void switchDays() {
        // Give the right data path to the corresponding reference
        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusSix");
        stepReference = firebaseDatabase.getReference().child("Users").child(userID).child("dailyNumberOfSteps");

        // Gives the value of stepDetectMinusFive to stepDetectMinusSix
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

        // Gives the value of stepDetectMinusFour to stepDetectMinusFive
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

        // Gives the value of stepDetectMinusThree to stepDetectMinusFour
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

        // Gives the value of stepDetectMinusTwo to stepDetectMinusThree
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

        // Gives the value of stepDetectMinusOne to stepDetectMinusTwo
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

        // Gives the value of stepDetect to stepDetectMinusOne
        stepReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

    // Sets the progressBar
    public void prog() {
        simpleProgressBar  = (ProgressBar) findViewById(R.id.StepProgress);
        simpleProgressBar.setMax(7000);
    }

    // Changes the shows value in the textView to current amount of steps
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Update the textView of stepCount when the user steps
        if (sensorEvent.sensor == myStepCounter){
            stepCount = (int) sensorEvent.values[0];
            textViewStepCounter.setText(String.valueOf(stepCount));
        // Update the textView of stepDetect when the user steps
        } else if (sensorEvent.sensor == myStepDetector) {
            stepDetect = (int) (stepDetect + sensorEvent.values[0]);
            textViewStepDetector.setText(String.valueOf(stepDetect));
            stepReference.setValue(stepDetect);
        }

        // Set the progress of the progressbar to the value of stepDetect
        simpleProgressBar.setProgress(stepDetect);
//        simpleProgressBar.setProgress(stepCount);

        // Informational textView, showing how many steps the user should still take
        // Of course, this remaining value cannot be a negative number
        if ((7000 - stepDetect) < 0) {
            remaining = 0;
        } else {
            remaining = 7000 - stepDetect;
        }
        progress.setText("You walked " + stepDetect + " steps today out of the " +
                         "recommended 7000 per day. Only " + remaining +
                         " steps remain till the next checkpoint.");

        // Create a new barChart
        createBarChart();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.registerListener(this,
                                                myStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.registerListener(this,
                                                myStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show();
    }
}

