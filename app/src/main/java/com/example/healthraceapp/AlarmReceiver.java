package com.example.healthraceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Time", "Alarm is triggered");

       // StepActivity.switchDays();
        MainActivity.reset();


    }

}