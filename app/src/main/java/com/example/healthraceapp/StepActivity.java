package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
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
import java.util.Objects;

public class StepActivity extends AppCompatActivity implements SensorEventListener, Intake {
    // If permission to use physical activity is granted
    private int PERMISSION_CODE = 1;

    // Text field to display remaining progress of user
    private TextView progress, dailyPoints;

    // Text field to display the points for each checkpoint
    private TextView checkpoint;
    String points_value;
    int cp_number;
    int cp_value;
    SpannableStringBuilder ssb;

    // Text field for the amount of steps of stepDetector
//    private static TextView textViewStepDetector;

    // The sensor manager for the step counter
    private SensorManager sensorManager;

    // The step detector sensor
    private Sensor myStepDetector;

    // If the device has a step counter
    private boolean isDetectorSensorPresent;

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
    private DatabaseReference pointsStepReference;
    private DatabaseReference stepMinusOneDatabaseReference;
    private DatabaseReference stepMinusTwoDatabaseReference;
    private DatabaseReference stepMinusThreeDatabaseReference;
    private DatabaseReference stepMinusFourDatabaseReference;
    private DatabaseReference stepMinusFiveDatabaseReference;
    private DatabaseReference stepMinusSixDatabaseReference;
    private DatabaseReference weeklyStepsReference;
    private DatabaseReference stepsWeekReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Ask with a popup for permission to track physical activity
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_CODE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Add stepDetector counter to activity
//        textViewStepDetector = findViewById(R.id.textViewStepDetector);
//        textViewStepDetector.setTextColor(Color.WHITE);
//        textViewStepDetector.setTextSize(20);

        // Add bar chart to activity and enter the corresponding data
        barChartStep = findViewById(R.id.barChartStep);
        createBarChart(barChartStep, getGraphData());

        //set the correct data paths
        setReferences();

        // If the value of stepDetectMinusOne in the database changes
        // It takes the value from stepDetectMinusOne in the database
        // And saves it in the variable stepDetectMinusOne
        // And create a new barChart
        stepMinusOneDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(int.class) != null) {
                    stepDetectMinusOne = snapshot.getValue(int.class);
                    createBarChart(barChartStep, getGraphData());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of stepDetectMinusTwo in the database changes
        // It takes the value from stepDetectMinusTwo in the database
        // And saves it in the variable stepDetectMinusTwo
        // And create a new barChart
        stepMinusTwoDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(int.class) != null) {
                    stepDetectMinusTwo = snapshot.getValue(int.class);
                    createBarChart(barChartStep, getGraphData());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of stepDetectMinusThree in the database changes
        // It takes the value from stepDetectMinusThree in the database
        // And saves it in the variable stepDetectMinusThree
        // And create a new barChart
        stepMinusThreeDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(int.class) != null) {
                    stepDetectMinusThree = snapshot.getValue(int.class);
                    createBarChart(barChartStep, getGraphData());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of stepDetectMinusFour in the database changes
        // It takes the value from stepDetectMinusFour in the database
        // And saves it in the variable stepDetectMinusFour
        // And create a new barChart
        stepMinusFourDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(int.class) != null) {
                    stepDetectMinusFour = snapshot.getValue(int.class);
                    createBarChart(barChartStep, getGraphData());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of stepDetectMinusFive in the database changes
        // It takes the value from stepDetectMinusFive in the database
        // And saves it in the variable stepDetectMinusFive
        // And create a new barChart
        stepMinusFiveDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(int.class) != null) {
                    stepDetectMinusFive = snapshot.getValue(int.class);
                    createBarChart(barChartStep, getGraphData());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // If the value of stepDetectMinusSix in the database changes
        // It takes the value from stepDetectMinusSix in the database
        // And saves it in the variable stepDetectMinusSix
        // And create a new barChart
        stepMinusSixDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(int.class) != null) {
                    int dataFromDatabase = snapshot.getValue(int.class);
                    stepDetectMinusSix = dataFromDatabase;
                    createBarChart(barChartStep, getGraphData());
                }
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
                if (snapshot.getValue(int.class) != null) {
                    stepDetect = snapshot.getValue(int.class);

                    // Show value of stepDetect in textView
//                    textViewStepDetector.setText("Steps today: " + stepDetect);

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

                    //set the points
                    setPoints(stepDetect, pointsStepReference);

                    setTotalPoints(firebaseDatabase, userID);

                    //Saves the steps of the last 7 days
                    setWeeklySteps();

                    // Create a new barChart
                    createBarChart(barChartStep, getGraphData());
                }
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

        //shows the individual activity points
        dailyPoints = findViewById(R.id.dailyPoints);
        dailyPoints.setTextColor(Color.WHITE);
        dailyPoints.setTextSize(20);

        checkpoint = findViewById(R.id.checkpoint);
        checkpoint.setTextColor(Color.WHITE);
        checkpoint.setTextSize(25);

        //Informational textview about user's progress and checkpoints
        if (stepDetect>=8000) {
            ssb = new SpannableStringBuilder("Congratulations! You earned 800 points and " +
                    "have crossed all the checkpoints!");
            checkpoint.setText(ssb);
            dailyPoints.setText("Your step count points today: 1900");
        }
        else if (stepDetect<500) {
            ssb = new SpannableStringBuilder("You will receive 50 points for the " +
                    "next checkpoint.");
            checkpoint.setText(ssb);
            dailyPoints.setText("Your step count points today: 0");
        }
        else {
            if (stepDetect >= 500 && stepDetect < 1500) {
                cp_number = 1;
                cp_value = 500;
                points_value = String.valueOf(150);
                ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                        + cp_number + " - " + cp_value + " steps and earned 50 points! ");
                ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.RED);
                ssb.setSpan(fcsRed, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dailyPoints.setText("Your step count points today: 50");
            }
            else if (stepDetect >= 1500 && stepDetect < 3000) {
                cp_number = 2;
                cp_value = 1500;
                points_value = String.valueOf(300);
                ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                        + cp_number + " - " + cp_value + " steps and earned 150 points! ");
                ForegroundColorSpan fcsOrange = new ForegroundColorSpan(Color.rgb(255,140,0));
                ssb.setSpan(fcsOrange, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dailyPoints.setText("Your step count points today: 200");
            }
            else if (stepDetect >= 3000 && stepDetect < 6000) {
                cp_number = 3;
                cp_value = 3000;
                points_value = String.valueOf(600);
                ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                        + cp_number + " - " + cp_value + " steps and earned 300 points! ");
                ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.rgb(255,215,0));
                ssb.setSpan(fcsYellow, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dailyPoints.setText("Your step count points today: 500");
            }
            else if (stepDetect >= 6000 && stepDetect < 8000) {
                cp_number = 4;
                cp_value = 6000;
                points_value = String.valueOf(800);
                ssb = new SpannableStringBuilder("Good going! You crossed Checkpoint "
                        + cp_number + " - " + cp_value + " steps and earned 600 points! ");
                ForegroundColorSpan fcsGreen = new ForegroundColorSpan(Color.GREEN);
                ssb.setSpan(fcsGreen, 23,36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dailyPoints.setText("Your step count points today: 1100");
            }

            ssb.setSpan(new StyleSpan(Typeface.BOLD),
                    23, 41, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append("You will receive " + points_value + " points for the next checkpoint.");

            checkpoint.setText(ssb);
        }

        // TODO: add comment
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Initiate the progressBar
        prog();

        // Check if step detector is present in device
        // If it is not present, the textView will notify the user
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            myStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            simpleProgressBar.setProgress(stepDetect);
            isDetectorSensorPresent = true;
        } else {
            //for testing purposes
//            textViewStepDetector.setText(String.valueOf(stepDetect));
            simpleProgressBar.setProgress(stepDetect);
//            textViewStepDetector.setText("Detector sensor is not present");
            isDetectorSensorPresent = false;
        }
    }

    @Override
    public ArrayList getGraphData() {
        // ArrayList for the shown data
        ArrayList<BarEntry> graphData = new ArrayList<>();
        graphData.add(new BarEntry(1, stepDetectMinusSix));
        graphData.add(new BarEntry(2, stepDetectMinusFive));
        graphData.add(new BarEntry(3, stepDetectMinusFour));
        graphData.add(new BarEntry(4, stepDetectMinusThree));
        graphData.add(new BarEntry(5, stepDetectMinusTwo));
        graphData.add(new BarEntry(6, stepDetectMinusOne));
        graphData.add(new BarEntry(7, stepDetect));

        return graphData;
    }

    // Every day at midnight the bar chart will get updated
    // This function makes sure the right data is swapped for the next day
    @Override
    public void switchDays() {
        //set the correct data paths
        setReferences();
        // Gives the value of stepMinusFive to stepMinusSix
        switchValues(stepMinusFiveDatabaseReference, stepMinusSixDatabaseReference);
        // Gives the value of stepMinusFour to stepMinusFive
        switchValues(stepMinusFourDatabaseReference, stepMinusFiveDatabaseReference);
        // Gives the value of stepMinusThree to stepMinusFour
        switchValues(stepMinusThreeDatabaseReference, stepMinusFourDatabaseReference);
        // Gives the value of stepMinusTwo to stepMinusThree
        switchValues(stepMinusTwoDatabaseReference, stepMinusThreeDatabaseReference);
        // Gives the value of stepMinusOne to stepMinusTwo
        switchValues(stepMinusOneDatabaseReference, stepMinusTwoDatabaseReference);
        // Gives the value of stepReference to stepMinusOne
        switchValues(stepReference, stepMinusOneDatabaseReference);
    }

    // Sets the progressBar
    public void prog() {
        simpleProgressBar  = (ProgressBar) findViewById(R.id.StepProgress);
        simpleProgressBar.setMax(7000);
    }

    // Changes the shows value in the textView to current amount of steps
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Update the textView of stepDetector when the user steps
        if (sensorEvent.sensor == myStepDetector) {
            stepDetect = (int) (stepDetect + sensorEvent.values[0]);
//            textViewStepDetector.setText("Steps today: " + stepDetect);
            stepReference.setValue(stepDetect);
        }

        // Set the progress of the progressbar to the value of stepDetect
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

        // Set the points
        setPoints(stepDetect, pointsStepReference);

        setTotalPoints(firebaseDatabase, userID);

        // Create a new barChart
        createBarChart(barChartStep, getGraphData());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    //sets points for individual activity to be stored in firebase
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
                    if (totalProgress >= 500 && totalProgress < 1500) { points = 50; }
                    else if (totalProgress >= 1500 && totalProgress < 3000) { points = 200; }
                    else if (totalProgress >= 3000 && totalProgress < 6000) { points = 500; }
                    else if (totalProgress >= 6000 && totalProgress < 8000) { points = 1100; }
                    else if (totalProgress >= 8000) { points = 1900; }
                    else if (totalProgress < 500) { points = 0; }
                    pointsReference.setValue(points);

                    dailyPoints.setText("Your step count points today: " + task.getResult().getValue(Integer.class));
                }
            }
        });
    }

    //sets firebase references that are used in the rest of the class
    @Override
    public void setReferences() {
        // Give the right data path to the corresponding reference
        stepReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepsWeek").child("dailyNumberOfSteps");
        pointsStepReference = firebaseDatabase.getReference().child("Users").child(userID).child("points").child("stepPoints");
        stepMinusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepsWeek").child("stepDetectMinusOne");
        stepMinusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepsWeek").child("stepDetectMinusTwo");
        stepMinusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepsWeek").child("stepDetectMinusThree");
        stepMinusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepsWeek").child("stepDetectMinusFour");
        stepMinusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepsWeek").child("stepDetectMinusFive");
        stepMinusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepsWeek").child("stepDetectMinusSix");
        weeklyStepsReference = firebaseDatabase.getReference().child("Users").child(userID).child("weeklySteps");
        stepsWeekReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepsWeek");
    }

    //sets the combined steps of the last 7 days and stores in firebase
    private void setWeeklySteps() {
        stepsWeekReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalSteps = 0;

                for (DataSnapshot ds: snapshot.getChildren()) {
                    int steps = ds.getValue(int.class);
                    totalSteps = totalSteps + steps;
                }
                weeklyStepsReference.setValue(totalSteps);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

