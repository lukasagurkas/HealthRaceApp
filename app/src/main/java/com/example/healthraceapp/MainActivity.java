package com.example.healthraceapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Objects;

import static java.util.Objects.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth mAuth;

    //view objects
    private TextView textViewUserEmail;
    //    private TextView stepText;
    private Button buttonLogout, buttonProfile, buttonGroup;

    //variables for water widget
    LinearLayout layout_water;

    //variables for step widget
    LinearLayout layout_step;

    //variables for fruit widget
    LinearLayout layout_fruit;

    //variables for vegetable widget
    LinearLayout layout_vegetable;

    ProgressBar progressBar_water, progressBar_veg, progressBar_fruit, progressBar_step;

    // If permission is granted
    private int PERMISSION_CODE = 1;

    //initialize instances for writing and reading data from the database
    private FirebaseDatabase firebaseDatabase;
    static private DatabaseReference reference;
    private String userID;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Home Page");

        dailyResetAlarm();

        //initializing firebase authentication object
        mAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(mAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
//        FirebaseUser user = mAuth.getCurrentUser();

        user = new User();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
                "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = firebaseDatabase.getReference().child("Users").child(userID);

        //initializing views
        buttonLogout = findViewById(R.id.buttonLogout);

//        stepText = findViewById(R.id.stepText);

        buttonProfile = findViewById(R.id.buttonProfile);
        buttonGroup = findViewById(R.id.buttonGroup);

        //layout for the step page
        layout_step = (LinearLayout)findViewById(R.id.layout_step);
        progressBar_step = findViewById(R.id.progressBar_step);

        //layout for the water page
        layout_water = (LinearLayout)findViewById(R.id.layout_water);
        progressBar_water = findViewById(R.id.progressBar_water);

        //layout for the fruit page
        layout_fruit = (LinearLayout)findViewById(R.id.layout_fruit);
        progressBar_fruit = findViewById(R.id.progressBar_fruit);

        //layout for the vegetable page
        layout_vegetable = (LinearLayout)findViewById(R.id.layout_vegetable);
        progressBar_veg = findViewById(R.id.progressBar_veg);

        createProgressBar(progressBar_step, "dailyNumberOfSteps", 7000);
        createProgressBar(progressBar_water, "amountOfWater", 2000);
        createProgressBar(progressBar_fruit, "amountOfFruit", 500);
        createProgressBar(progressBar_veg, "amountOfVeg", 500);


        //displaying logged in user email
//        textViewUserEmail.setText("Welcome "+ user.getEmail());

        //adding listener to button
        //buttonLogout.setOnClickListener(this);

        //listens for changes on step counter page widget
        layout_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StepActivity.class));
            }
        });

        //listens for changes on fruit page widget
        layout_fruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FruitActivity.class));
            }
        });

        //listens for changes on vegetable page widget
        layout_vegetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VegetableActivity.class));
            }
        });

        //listens for changes on water page widget
        layout_water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WaterActivity.class));
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        buttonGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GroupActivity.class));

            }
        });

    }

    private void createProgressBar(ProgressBar progressBar, String progressValue, int max) {
        progressBar.setMax(max);

        reference.child(progressValue).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int dataFromDatabase = requireNonNull(snapshot).getValue(int.class);
                    progressBar.setProgress(dataFromDatabase);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("error", "loadPost:onCancelled", error.toException());
            }
        });
    }



    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            mAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public static void reset(){
        Log.d("ResetAlarm", "Reset works");
        reference.child("amountOfVeg").setValue(0);
        reference.child("amountOfFruit").setValue(0);
        reference.child("amountOfWater").setValue(0);
        reference.child("dailyNumberOfSteps").setValue(0);
    }

    public void dailyResetAlarm() {
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        Log.d("waitCheck", "It works");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Set calender for a set time in the day
        Calendar setCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        setCalendar.setTimeInMillis(System.currentTimeMillis());
        setCalendar.set(Calendar.HOUR_OF_DAY, 0);
        setCalendar.set(Calendar.MINUTE, 0);
        setCalendar.set(Calendar.SECOND, 0);
        Log.d("Timecheck", String.valueOf(setCalendar.getTime()));

        if (setCalendar.before(calendar)){
            setCalendar.add(Calendar.DATE, 1);
        }
        Log.d("TimeCheck after fix", String.valueOf(setCalendar.getTime()));

        alarmManager.setRepeating(AlarmManager.RTC, setCalendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }



}