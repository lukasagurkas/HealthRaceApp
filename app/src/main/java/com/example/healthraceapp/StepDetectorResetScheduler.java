package com.example.healthraceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StepDetectorResetScheduler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        StepActivity stepActivity = new StepActivity();
        stepActivity.resetCount();
    }
}
