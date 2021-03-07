package com.example.healthraceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiverStepDetector extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Time", "Alarm is triggered");
        StepActivity stepActivity = new StepActivity();
//        stepActivity.switchDays();
        stepActivity.resetCount();
    }
}