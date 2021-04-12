package com.example.healthraceapp;

import android.app.Activity;

import androidx.fragment.app.FragmentManager;

public interface DatePickerFragmentInterface {

    void setCurrentActivity(Activity activity);

    void show(FragmentManager fragmentManager, String string);
}
