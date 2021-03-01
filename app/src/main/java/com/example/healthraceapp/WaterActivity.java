package com.example.healthraceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class WaterActivity extends AppCompatActivity {

    TextView tvProgressLabel; //stores the progress on the slider
    TextView waterProgress; //displays how much progress the user has made
    TextView checkpoint; //displays the points for each checkpoint
    TextView milliliters;
    Button buttonAdd; //button to add water quantity
    Button buttonMainPage; //button to go back to main page
    ProgressBar progressBar; //progress bar for user to enter water intake

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        progressBar = findViewById(R.id.progressBar); //finds progress bar in activity page
        progressBar.setProgress(0);
        progressBar.setMax(2000);
        buttonAdd = findViewById(R.id.buttonAdd); //finds add button in activity page
        buttonMainPage = findViewById(R.id.buttonMainPage); //finds main page button in activity
        // page

        // set a change listener on the SeekBar
        SeekBar seekBar = findViewById(R.id.seekBar); //finds slider in activity page
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("" + progress);
        tvProgressLabel.setTextColor(Color.WHITE);
        tvProgressLabel.setTextSize(15);

        waterProgress = findViewById(R.id.waterProgress);
        int remaining = 2000 - progress;
        waterProgress.setText("You drank " + progress + " ml of water today out of the " +
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
                progressBar.setProgress(seekBar.getProgress());
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
                startActivity(new Intent(WaterActivity.this, MainActivity.class));
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