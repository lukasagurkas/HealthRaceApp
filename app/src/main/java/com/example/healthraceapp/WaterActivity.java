package com.example.healthraceapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class WaterActivity extends AppCompatActivity {

    TextView tvProgressLabel; //stores the progress on the slider
    TextView waterProgress; //displays how much progress the user has made
    TextView checkpoint; //displays the points for each checkpoint
    TextView milliliters;
    Button buttonAdd; //button to add water quantity
    ProgressBar progressBar; //progress bar for user to enter water intake
    static int totalProgress; //initiate total progress
    int progress; //initiate progress value

    // Initiate bar chart
    BarChart barChartWater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        progressBar = findViewById(R.id.progressBar); //finds progress bar in activity page
        progressBar.setProgress(totalProgress);
        progressBar.setMax(2000);
        buttonAdd = findViewById(R.id.buttonAdd); //finds add button in activity page

        barChartWater = findViewById(R.id.barChartWater);

        // ArrayList for the shown data
        ArrayList<BarEntry> visitorsWater = new ArrayList<>();
        visitorsWater.add(new BarEntry(2014, 420));
        visitorsWater.add(new BarEntry(2015, 440));
        visitorsWater.add(new BarEntry(2016, 460));
        visitorsWater.add(new BarEntry(2017, 480));
        visitorsWater.add(new BarEntry(2018, 500));

        // Layout of the bar chart
        BarDataSet barDataSetWater = new BarDataSet(visitorsWater, "Visitors");
        barDataSetWater.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSetWater.setValueTextColor(Color.BLACK);
        barDataSetWater.setValueTextSize(16f);
        BarData barDataWater = new BarData(barDataSetWater);
        barChartWater.setFitBars(true);
        barChartWater.setData(barDataWater);
        barChartWater.getDescription().setText("Bar Chart Example");
        barChartWater.animateY(200);

        // set a change listener on the SeekBar
        SeekBar seekBar = findViewById(R.id.seekBar); //finds slider in activity page
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("" + progress);
        tvProgressLabel.setTextColor(Color.WHITE);
        tvProgressLabel.setTextSize(15);

        waterProgress = findViewById(R.id.waterProgress);
        int remaining = 2000 - progress;
        waterProgress.setText("You drank " + totalProgress + " ml of water today out of the " +
                "recommended 2000 ml. Only " + remaining + " ml of water remains.");
        waterProgress.setTextColor(Color.WHITE);
        waterProgress.setTextSize(20);

        checkpoint = findViewById(R.id.checkpoint);
        checkpoint.setTextColor(Color.WHITE);
        checkpoint.setTextSize(25);

        milliliters = findViewById(R.id.ml);
        milliliters.setTextColor(Color.WHITE);
        milliliters.setTextSize(20);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = seekBar.getProgress();
                totalProgress = totalProgress + progress;
                progressBar.setProgress(totalProgress);
                tvProgressLabel.setText("" + progress);
                waterProgress.setText("You drank " + totalProgress + " ml of water today out of the " +
                        "recommended 2000 ml. Only " + remaining + " ml of water remains.");
            }
        });
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            tvProgressLabel.setText("" + progress);
            int remaining = 2000 - progress;
            waterProgress.setText("You drank " + progress + " ml of water today out of the " +
                    "recommended 2000 ml. Only " + remaining + " ml of water remains.");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };
}