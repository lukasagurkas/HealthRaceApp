package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class StepActivity extends AppCompatActivity implements SensorEventListener, Intake{
    // If permission to use physical activity is granted
    private int PERMISSION_CODE = 1;

    // Text field to display remaining progress of user
    private TextView progress;

    // Text field to display the points for each checkpoint
    private TextView checkpoint;

    // Text field for the amount of steps of stepDetector
    private static TextView textViewStepDetector;

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

        // Add stepDetector counter to activity
        textViewStepDetector = findViewById(R.id.textViewStepDetector);

        // Add bar chart to activity and enter the corresponding data
        barChartStep = findViewById(R.id.barChartStep);
        createBarChart(barChartStep, getGraphData());

        // Give the right data path to the corresponding reference
        stepReference = firebaseDatabase.getReference().child("Users").child(userID).child("dailyNumberOfSteps");
        pointsStepReference = firebaseDatabase.getReference().child("Users").child(userID).child("points").child("stepPoints");
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
                createBarChart(barChartStep, getGraphData());
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
                createBarChart(barChartStep, getGraphData());
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
                createBarChart(barChartStep, getGraphData());
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
                createBarChart(barChartStep, getGraphData());
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
                createBarChart(barChartStep, getGraphData());
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
                createBarChart(barChartStep, getGraphData());
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

                //set the points
                setPoints(stepDetect, pointsStepReference);

                // Create a new barChart
                createBarChart(barChartStep, getGraphData());
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

//    // Function the create the barChart and insert data into it
//    public void createBarChart() {
//        // ArrayList for the shown data
//        ArrayList<BarEntry> graphData = new ArrayList<>();
//        graphData.add(new BarEntry(1, stepDetectMinusSix));
//        graphData.add(new BarEntry(2, stepDetectMinusFive));
//        graphData.add(new BarEntry(3, stepDetectMinusFour));
//        graphData.add(new BarEntry(4, stepDetectMinusThree));
//        graphData.add(new BarEntry(5, stepDetectMinusTwo));
//        graphData.add(new BarEntry(6, stepDetectMinusOne));
//        graphData.add(new BarEntry(7, stepDetect));
//
//        // Layout for the bar chart
//        // Create a new dataset for the barChart with the graphData
//        BarDataSet barDataSetStep = new BarDataSet(graphData, "Days");
//        // Set Bar Colors
//        barDataSetStep.setColors(ColorTemplate.MATERIAL_COLORS);
//        // Set Text Color
//        barDataSetStep.setValueTextColor(Color.BLACK);
//        // Set Text Size
//        barDataSetStep.setValueTextSize(16f);
//        BarData barDataStep = new BarData(barDataSetStep);
//        // Adds half of the bar width to each side of the x-axis range in order to
//        // allow the bars of the barchart to be fully displayed
//        barChartStep.setFitBars(true);
//        // Set a new data object for the barChart
//        barChartStep.setData(barDataStep);
//        // Set description
//        barChartStep.getDescription().setText("Step progress over the last 7 days");
//        // The barChart will show a vertical animation with a duration of 200 ms
//        // every time the data changes
//        barChartStep.animateY(200);
//    }

//    //This function copies the value of database reference ds1 to ds2
//    @Override
//    public void switchValues(DatabaseReference ds1, DatabaseReference ds2) {
//        ds1.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    ds2.setValue(task.getResult().getValue());
//                }
//            }
//        });
//    }

    // Every day at midnight the bar chart will get updated
    // This function makes sure the right data is swapped for the next day
    @Override
    public void switchDays() {
        // Give the right data path to the corresponding reference
        minusOneDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusOne");
        minusTwoDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusTwo");
        minusThreeDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusThree");
        minusFourDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusFour");
        minusFiveDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusFive");
        minusSixDatabaseReference = firebaseDatabase.getReference().child("Users").child(userID).child("stepDetectMinusSix");
        stepReference = firebaseDatabase.getReference().child("Users").child(userID).child("dailyNumberOfSteps");

        // Gives the value of stepMinusFive to stepMinusSix
        switchValues(minusFiveDatabaseReference, minusSixDatabaseReference);
        // Gives the value of stepMinusFour to stepMinusFive
        switchValues(minusFourDatabaseReference, minusFiveDatabaseReference);
        // Gives the value of stepMinusThree to stepMinusFour
        switchValues(minusThreeDatabaseReference, minusFourDatabaseReference);
        // Gives the value of stepMinusTwo to stepMinusThree
        switchValues(minusTwoDatabaseReference, minusThreeDatabaseReference);
        // Gives the value of stepMinusOne to stepMinusTwo
        switchValues(minusOneDatabaseReference, minusTwoDatabaseReference);
        // Gives the value of stepReference to stepMinusOne
        switchValues(stepReference, minusOneDatabaseReference);

//        // Gives the value of stepDetectMinusFive to stepDetectMinusSix
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
//        // Gives the value of stepDetectMinusFour to stepDetectMinusFive
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
//        // Gives the value of stepDetectMinusThree to stepDetectMinusFour
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
//
//        // Gives the value of stepDetectMinusTwo to stepDetectMinusThree
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
//        // Gives the value of stepDetectMinusOne to stepDetectMinusTwo
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
//        // Gives the value of stepDetect to stepDetectMinusOne
//        stepReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
            textViewStepDetector.setText(String.valueOf(stepDetect));
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
    @Override
    public void setPoints(int totalProgress, DatabaseReference pointsReference) {
        //adds points to the total from the water page if these checkpoints are crossed
        int points = 0;
        if (totalProgress >= 500 && totalProgress < 1500) {
            points = 25;

            //setting value of points received for next checkpoint
//            cp = String.valueOf(50);
        }
        else if (totalProgress >= 1500 && totalProgress < 3000) {
            points = 75;
//            cp = String.valueOf(100);
        }
        else if (totalProgress >= 3000 && totalProgress < 6000) {
            points = 175;
//            cp = String.valueOf(250);
        }
        else if (totalProgress >= 6000 && totalProgress < 7000) {
            points = 425;
//            cp = String.valueOf(500);
        }
        else if (totalProgress >= 7000) {
            points = 925;
//            s = "You have crossed all the checkpoints!";
        }
        else if (totalProgress < 500) {
            points = 0;
//            cp = String.valueOf(25);
        }
//        checkpoint.setText(s);
        pointsReference.setValue(points);
    }

}

