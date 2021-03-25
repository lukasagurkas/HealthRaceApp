package com.example.healthraceapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
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

    //TextView to display points received for upcoming checkpoint
    TextView waterWidget_points;
    TextView fruitWidget_points;
    TextView vegWidget_points;
    TextView stepWidget_points;
    String next_cp_points;
    int progress;

    MaterialToolbar topAppBar;

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

//        buttonProfile = findViewById(R.id.buttonProfile);
        buttonGroup = findViewById(R.id.buttonGroup);

        //layout for the step page
        layout_step = (LinearLayout)findViewById(R.id.layout_step);
        progressBar_step = findViewById(R.id.progressBar_step);
        stepWidget_points = findViewById(R.id.stepWidget_points);

        //layout for the water page
        layout_water = (LinearLayout)findViewById(R.id.layout_water);
        progressBar_water = findViewById(R.id.progressBar_water);
        waterWidget_points = findViewById(R.id.waterWidget_points);

        //layout for the fruit page
        layout_fruit = (LinearLayout)findViewById(R.id.layout_fruit);
        progressBar_fruit = findViewById(R.id.progressBar_fruit);
        fruitWidget_points = findViewById(R.id.fruitWidget_points);

        //layout for the vegetable page
        layout_vegetable = (LinearLayout)findViewById(R.id.layout_vegetable);
        progressBar_veg = findViewById(R.id.progressBar_veg);
        vegWidget_points = findViewById(R.id.vegWidget_points);

        setValues(progressBar_step, "stepsWeek", "dailyNumberOfSteps", 7000, "step");
//        stepWidget_points.setText("Steps: +" + next_cp_points);

        setValues(progressBar_water,"waterWeek", "amountOfWater", 2000, "water");
//        waterWidget_points.setText("Water: +" + next_cp_points);

        setValues(progressBar_fruit, "fruitWeek", "amountOfFruit", 500, "fruit");
//        fruitWidget_points.setText("Fruit: +" + next_cp_points);

        setValues(progressBar_veg, "veggieWeek", "amountOfVeg", 500, "veg");



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

//        buttonProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//            }
//        });

        buttonGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GroupActivity.class));

            }
        });

    }

    // Create an ActionBar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Instantiate XML file into its corresponding View object
        getMenuInflater().inflate(R.menu.top_app_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Handle ActionBar requests
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.profile_page) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        } else {}
        return super.onOptionsItemSelected(item);
    }

    // sets values for the progress bars and the checkpoints on the widgets
    private void setValues(ProgressBar progressBar, String progressMap, String progressValue,
                           int max, String intakeType) {
        progressBar.setMax(max);

        reference.child(progressMap).child(progressValue).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int dataFromDatabase = requireNonNull(snapshot).getValue(int.class);
                    progressBar.setProgress(dataFromDatabase);

                    if (intakeType.equals("step")) {
                        if (dataFromDatabase < 500) { next_cp_points = "+ 50 points";;  }
                        if (dataFromDatabase >= 500 && dataFromDatabase < 1500) { next_cp_points = "+ 150 points"; }
                        if (dataFromDatabase >= 1500 && dataFromDatabase < 3000) { next_cp_points = "+ 300 points"; }
                        if (dataFromDatabase >= 3000 && dataFromDatabase < 6000) { next_cp_points = "+ 600 points"; }
                        if (dataFromDatabase >= 6000 && dataFromDatabase < 8000) { next_cp_points = "+ 800 points"; }
                        if (dataFromDatabase >= 8000) { next_cp_points = "Complete!";  }

                        stepWidget_points.setText("Steps: " + next_cp_points);
                    }

                    else if (intakeType.equals("fruit") || intakeType.equals("veg")) {
                        if (dataFromDatabase < 50) { next_cp_points = "+ 25 points";  }
                        if (dataFromDatabase >= 50 && dataFromDatabase < 100) { next_cp_points = "+ 50 points"; }
                        if (dataFromDatabase >= 100 && dataFromDatabase < 175) { next_cp_points = "+ 100 points"; }
                        if (dataFromDatabase >= 175 && dataFromDatabase < 275) { next_cp_points = "+ 250 points"; }
                        if (dataFromDatabase >= 275 && dataFromDatabase < 400) { next_cp_points = "+ 500 points"; }
                        if (dataFromDatabase >= 400) { next_cp_points = "Complete!";  }

                        switch (intakeType) {
                            case "fruit": fruitWidget_points.setText("Fruit: " + next_cp_points);
                                break;
                            case "veg": vegWidget_points.setText("Vegetables: " + next_cp_points);
                                break;
                        }
                    }

                    else if (intakeType.equals("water")) {
                        if (dataFromDatabase < 200) { next_cp_points = "+ 25 points";;  }
                        if (dataFromDatabase >= 200 && dataFromDatabase < 500) { next_cp_points = "+ 50 points"; }
                        if (dataFromDatabase >= 500 && dataFromDatabase < 1000) { next_cp_points = "+ 100 points"; }
                        if (dataFromDatabase >= 1000 && dataFromDatabase < 2000) { next_cp_points = "+ 250 points"; }
                        if (dataFromDatabase >= 2000 && dataFromDatabase < 3200) { next_cp_points = "+ 500 points"; }
                        if (dataFromDatabase >= 3200) { next_cp_points = "Complete!";  }

                        waterWidget_points.setText("Water: " + next_cp_points);
                    }

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

    private void setNextCheckpoint(String progressMap, String progressValue, String intake) {

        reference.child(progressMap).child(progressValue).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int dataFromDatabase = snapshot.getValue(int.class);
                    progress = dataFromDatabase;

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
        //Sets all the data inputs from the last day to zero
        reference.child("veggieWeek").child("amountOfVeg").setValue(0);
        reference.child("fruitWeek").child("amountOfFruit").setValue(0);
        reference.child("waterWeek").child("amountOfWater").setValue(0);
        reference.child("stepsWeek").child("dailyNumberOfSteps").setValue(0);

        //sets all the points from last day to zero
        reference.child("points").child("fruitPoints").setValue(0);
        reference.child("points").child("waterPoints").setValue(0);
        reference.child("points").child("stepPoints").setValue(0);
        reference.child("points").child("veggiePoints").setValue(0);
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
        setCalendar.set(Calendar.HOUR_OF_DAY, 17);
        setCalendar.set(Calendar.MINUTE, 44);
        setCalendar.set(Calendar.SECOND, 20);
        Log.d("Timecheck", String.valueOf(setCalendar.getTime()));

        if (setCalendar.before(calendar)){
            setCalendar.add(Calendar.DATE, 1);
        }
        Log.d("TimeCheck after fix", String.valueOf(setCalendar.getTime()));

        alarmManager.setRepeating(AlarmManager.RTC, setCalendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }



}