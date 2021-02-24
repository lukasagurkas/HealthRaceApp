package com.example.healthraceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class FruitActivity extends AppCompatActivity {

    TextView tvProgressLabel;
    Button buttonAdd;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        buttonAdd = findViewById(R.id.buttonAdd);

        // set a change listener on the SeekBar
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("Grams: " + progress);
        buttonAdd.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            progressBar.incrementProgressBy(progress);
            progressBar.setVisibility(View.VISIBLE);

        });
//        progressBar.setProgress(progress);

    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            tvProgressLabel.setText("Grams: " + progress);
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