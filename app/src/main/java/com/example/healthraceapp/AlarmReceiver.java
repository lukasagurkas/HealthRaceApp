package com.example.healthraceapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log to logcat that the alarm was triggered
        Log.d("Time", "Alarm is triggered");
        //Initiation of all the pages
        WaterActivity waterActivity = new WaterActivity();
        VegetableActivity vegetableActivity = new VegetableActivity();
        FruitActivity fruitActivity = new FruitActivity();
        StepActivity stepActivity = new StepActivity();

        // Switch days for all the health activities
        waterActivity.switchDays();
        vegetableActivity.switchDays();
        fruitActivity.switchDays();
        stepActivity.switchDays();
        // Reset the main page
        MainActivity.reset();


    }


}