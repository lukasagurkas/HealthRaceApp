package com.example.healthraceapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public interface Intake {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
            "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String userID = firebaseUser.getUid();

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

    @SuppressLint("SetTextI18n")
    void setPoints(int totalProgress, DatabaseReference pointsReference);

    default void setTotalPoints(FirebaseDatabase firebaseDatabase, String userID){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userID2 = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference pointsReference = firebaseDatabase.getReference().child("Users")
                .child(userID2).child("points");
        DatabaseReference totalPointsReference = firebaseDatabase.getReference().child("Users")
                .child(userID2).child("totalPoints");
        DatabaseReference negativeTotalPointsReference = firebaseDatabase.getReference().child("Users")
                .child(userID2).child("negativeTotalPoints");

        pointsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalPoints = 0;
                int points;
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d("pointschecker", String.valueOf(ds));
                    if (ds.getValue(int.class) == null) {
                        points = 0;
                    } else {
                        points = ds.getValue(int.class);
                        totalPoints = totalPoints + points;
                    }
                }
                int totalPointsCopy = totalPoints;

                firebaseDatabase.getReference("Users").child(userID2).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Intake", task.getException().getMessage());
                        } else {
                            User user = task.getResult().getValue(User.class);
                            ArrayList<String> groupsOfUser = user.getGroupNames();

                            firebaseDatabase.getReference("Groups").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String temporaryGroupName = dataSnapshot.child("groupName").getValue(String.class);
                                        if (groupsOfUser.contains(temporaryGroupName)) {
                                            firebaseDatabase.getReference("Groups")
                                                    .child(temporaryGroupName).child("members")
                                                    .child(user.getUsername()).child("totalPoints").setValue(totalPointsCopy);
                                            firebaseDatabase.getReference("Groups")
                                                    .child(temporaryGroupName).child("members")
                                                    .child(user.getUsername()).child("negativeTotalPoints").setValue(totalPointsCopy * -1);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });
                totalPointsReference.setValue(totalPoints);
                negativeTotalPointsReference.setValue(totalPoints * -1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void setReferences();


//    default void barListener(TextView fruitTvProgressLabel, SeekBar fruitSeekBar) {
//
//        // Adds listener for the slider
//        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                // updated continuously as the user slides the thumb
//                fruitTvProgressLabel.setText("" + progress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // called when the user first touches the SeekBar
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // called after the user finishes moving the SeekBar
//            }
//        };

//        // Finds slider in activity page
//        SeekBar fruitSeekBar = findViewById(R.id.seekBar);
//        // set a change listener on the SeekBar
//        fruitSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
//    }

}
