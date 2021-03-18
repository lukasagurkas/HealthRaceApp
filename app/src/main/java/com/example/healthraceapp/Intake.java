package com.example.healthraceapp;




import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public interface Intake {


    //Method to get the data for the bar chart
    ArrayList getGraphData();

    // Method for creating a bar chart
    default void createBarChart(BarChart barChart, ArrayList<BarEntry> graphData) {

        // Layout for the bar chart
        // Create a new dataset for the barChart with the graphData
        BarDataSet barDataSetWater = new BarDataSet(graphData, "Days");
        // Set Bar Colors
        barDataSetWater.setColors(ColorTemplate.MATERIAL_COLORS);
        // Set Text Color
        barDataSetWater.setValueTextColor(Color.BLACK);
        // Set Text Size
        barDataSetWater.setValueTextSize(16f);
        BarData barDataWater = new BarData(barDataSetWater);
        // Adds half of the bar width to each side of the x-axis range in order to
        // allow the bars of the barchart to be fully displayed
        barChart.setFitBars(true);
        // Set a new data object for the barChart
        barChart.setData(barDataWater);
        // Set description
        barChart.getDescription().setText("Water intake progress over the last 7 days");
        // The barChart will show a vertical animation with a duration of 200 ms
        // every time the data changes
        barChart.animateY(200);
    }

    //This method makes sure the right data is swapped for the next day
    void switchDays();

    //This method copies a value from one database reference to another
    default void switchValues(DatabaseReference ds1, DatabaseReference ds2) {
        ds1.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                ds2.setValue(task.getResult().getValue());
            }
        });
    }


}
