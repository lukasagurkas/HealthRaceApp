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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class StepActivity extends AppCompatActivity implements SensorEventListener {
    // If permission is granted
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
    public static int stepDetect = 0;

    // Initiate the progress bar
    ProgressBar simpleProgressBar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACTIVITY_RECOGNITION},PERMISSION_CODE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dailyResetAlarm();
        textViewStepCounter = findViewById(R.id.textViewStepCounter);
        textViewStepDetector = findViewById(R.id.textViewStepDetector);

        progress = findViewById(R.id.progress);
//        int remaining = 7000 - progress;
        progress.setText("You walked x steps today out of the " +
                "recommended 7000 per day. Only y steps remain till the next checkpoint.");
        progress.setTextColor(Color.WHITE);
        progress.setTextSize(15);

        checkpoint = findViewById(R.id.checkpoint);
        checkpoint.setTextColor(Color.WHITE);
        checkpoint.setTextSize(25);

        Button buttonMainPage = findViewById(R.id.buttonMainPage);
        buttonMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StepActivity.this, MainActivity.class));
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        prog();


        // Check if step counter is present in device
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            myStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else {
            textViewStepCounter.setText("No stepCounter detected");
            isCounterSensorPresent = false;
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            myStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isDetectorSensorPresent = true;
        } else {
            textViewStepDetector.setText("Detector sensor is not present");
            isDetectorSensorPresent = false;
        }
    }

    public void dailyResetAlarm() {
        Intent intent = new Intent(StepActivity.this, StepDetectorResetScheduler.class);
        Log.d("waitCheck", "It works");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StepActivity.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar setCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        setCalendar.setTimeInMillis(System.currentTimeMillis());
        setCalendar.set(Calendar.HOUR_OF_DAY, 0);
        setCalendar.set(Calendar.MINUTE, 0);
        setCalendar.set(Calendar.SECOND, 1);
        Log.d("Timecheck", String.valueOf(setCalendar.getTime()));

        if (setCalendar.before(calendar)){
            setCalendar.add(Calendar.DATE, 1);
        }
        Log.d("TimeCheck after fix", String.valueOf(setCalendar.getTime()));

        alarmManager.setRepeating(AlarmManager.RTC, setCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    // Sets the progressBar
    public void prog() {
        simpleProgressBar  = (ProgressBar) findViewById(R.id.StepProgress);
    }

    // Changes the shows value in the textView to current amount of steps
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == myStepCounter){
            stepCount = (int) sensorEvent.values[0];
            textViewStepCounter.setText(String.valueOf(stepCount));
        } else if (sensorEvent.sensor == myStepDetector) {
            stepDetect = (int) (stepDetect + sensorEvent.values[0]);
            textViewStepDetector.setText(String.valueOf(stepDetect));
        }

        simpleProgressBar.setProgress(stepDetect);
//        simpleProgressBar.setProgress(stepCount);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            sensorManager.registerListener(this, myStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
            sensorManager.registerListener(this, myStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void resetCount() {
        stepDetect = 0;
        textViewStepDetector.setText(String.valueOf(stepDetect));
    }



}

