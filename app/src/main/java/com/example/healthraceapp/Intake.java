package com.example.healthraceapp;




import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    //TODO: delete this when deleting TextView pointsWater in water activity
    @SuppressLint("SetTextI18n")
    default void setPoints(int totalProgress, int progress, String userID, DatabaseReference pointsReference, TextView pointsWater) {

        //adds points to the total from the water page if these checkpoints are crossed
                int points = 0;
                if (totalProgress >= 200 && totalProgress <=500) { points = 25; }
                else if (totalProgress >= 500 && totalProgress <=1000) { points = 75; }
                else if (totalProgress >= 1000 && totalProgress <= 2000) { points = 175; }
                else if (totalProgress >= 2000 && totalProgress <= 3200) { points = 425; }
                else if (totalProgress >= 3200) { points = 925; }
                else if (totalProgress-progress==0 || totalProgress<200) { points = 0; }
                pointsReference.setValue(points);
                pointsWater.setText("" + points);

            }

    @SuppressLint("SetTextI18n")
    void setPoints(int totalProgress, DatabaseReference pointsReference);

    default void setTotalPoints(FirebaseDatabase firebaseDatabase, String userID){
        DatabaseReference pointsReference = firebaseDatabase.getReference().child("Users")
                .child(userID).child("points");
        DatabaseReference totalPointsReference = firebaseDatabase.getReference().child("Users")
                .child(userID).child("totalPoints");

        pointsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalPoints = 0;

                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d("pointschecker", String.valueOf(ds));
                    int points = ds.getValue(int.class);
                    totalPoints = totalPoints + points;
                }
                totalPointsReference.setValue(totalPoints);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
