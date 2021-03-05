package com.example.healthraceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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

public class FruitActivity extends AppCompatActivity {

    //stores the progress on the slider
    TextView tvProgressLabel;

    //displays how much progress the user has made
    TextView intakeProgress;

    //displays the points for each checkpoint
    TextView checkpoint;

    //displays the entry of the user
    TextView grams;

    //button to add water quantity
    Button buttonAdd;

    //button to go back to main page
    Button buttonMainPage;

    //progress bar for user to enter water intake
    ProgressBar progressBar;

    // Initiate bar chart
    BarChart barChartFruit;

    //initiate progress value
    int progress;

    //initiate total progress
    static int totalProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);

        progressBar = (ProgressBar) findViewById(R.id.progressBar); //finds progress bar in activity page
        Log.d("check", String.valueOf(totalProgress));
        progressBar.setProgress(totalProgress);
        progressBar.setMax(500);
        buttonAdd = findViewById(R.id.buttonAdd); //finds add button in activity page
        //buttonMainPage = findViewById(R.id.buttonMainPage); //finds main page button in activity
        // page

        barChartFruit = findViewById(R.id.barChartFruit);

        // ArrayList for the shown data
        ArrayList<BarEntry> visitorsFruit = new ArrayList<>();
        visitorsFruit.add(new BarEntry(2014, 420));
        visitorsFruit.add(new BarEntry(2015, 440));
        visitorsFruit.add(new BarEntry(2016, 460));
        visitorsFruit.add(new BarEntry(2017, 480));
        visitorsFruit.add(new BarEntry(2018, 500));

        // Layout of the bar chart
        BarDataSet barDataSetFruit = new BarDataSet(visitorsFruit, "Visitors");
        barDataSetFruit.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSetFruit.setValueTextColor(Color.BLACK);
        barDataSetFruit.setValueTextSize(16f);
        BarData barDataFruit = new BarData(barDataSetFruit);
        barChartFruit.setFitBars(true);
        barChartFruit.setData(barDataFruit);
        barChartFruit.getDescription().setText("Bar Chart Example");
        barChartFruit.animateY(200);

        // set a change listener on the SeekBar
        SeekBar seekBar = findViewById(R.id.seekBar); //finds slider in activity page
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);


        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("" + progress);
        tvProgressLabel.setTextColor(Color.WHITE);
        tvProgressLabel.setTextSize(15);

        intakeProgress = findViewById(R.id.intakeProgress);
        int remaining = 500 - progress;
        intakeProgress.setText("You ate " + progress + " g of fruits today out of the " +
                "recommended 500 g. Only " + remaining + " grams of fruit remains.");
        intakeProgress.setTextColor(Color.WHITE);
        intakeProgress.setTextSize(20);

        checkpoint = findViewById(R.id.checkpoint);
        checkpoint.setTextColor(Color.WHITE);
        checkpoint.setTextSize(25);

        grams = findViewById(R.id.grams);
        grams.setTextColor(Color.WHITE);
        grams.setTextSize(20);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = seekBar.getProgress();
                totalProgress = totalProgress + progress;
                progressBar.setProgress(totalProgress);
                tvProgressLabel.setText("" + progress);
                intakeProgress.setText("You ate " + totalProgress + " g of fruits today out of the " +
                        "recommended 500 g. Only " + remaining + " grams of fruit remains.");
            }
        });
//        buttonMainPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(FruitActivity.this, MainActivity.class));
//            }
//        });
//        progressBar.setProgress(progress);
        buttonMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FruitActivity.this, MainActivity.class));
            }
        });
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            tvProgressLabel.setText("" + progress);
            int remaining = 500 - progress;
            intakeProgress.setText("You ate " + totalProgress + " g of fruits today out of the " +
                    "recommended 500 g. Only " + remaining + " grams of fruit remains.");
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