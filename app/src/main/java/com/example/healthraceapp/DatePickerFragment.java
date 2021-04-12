package com.example.healthraceapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, DatePickerFragmentInterface {

    private RegisterActivityInterface currentActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        // Get the current year
        int year = c.get(Calendar.YEAR);
        // Get the current month
        int month = c.get(Calendar.MONTH);
        // Get the current day
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void setCurrentActivity(Activity activity) {
        currentActivity = (RegisterActivityInterface) activity;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        currentActivity.setDateOfBirth(year, month, day);
    }
}
