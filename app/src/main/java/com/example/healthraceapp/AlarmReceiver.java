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
        Log.d("Time", "Alarm is triggered");

        WaterActivity waterActivity = new WaterActivity();
        VegetableActivity vegetableActivity = new VegetableActivity();
        FruitActivity fruitActivity = new FruitActivity();
        StepActivity stepActivity = new StepActivity();

        waterActivity.switchDays();
        vegetableActivity.switchDays();
        fruitActivity.switchDays();
        stepActivity.switchDays();

        MainActivity.reset();


    }


}