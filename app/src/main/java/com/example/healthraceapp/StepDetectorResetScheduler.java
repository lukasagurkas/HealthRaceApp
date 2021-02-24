package com.example.healthraceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StepDetectorResetScheduler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("schedulerCheck", "it works");
        StepActivity stepActivity = new StepActivity();
        stepActivity.resetCount();
    }
}
