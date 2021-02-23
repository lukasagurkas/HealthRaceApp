package com.example.healthraceapp;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PopupForgotPassword extends LoginActivity{
    Toolbar toolbar;
    EditText editUserEmail;
    Button buttonSubmit;

    FirebaseAuth firebaseAuth;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_popupforgot);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout(width, (int) (height*.3));
        firebaseAuth = firebaseAuth.getInstance();

        editUserEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = editUserEmail.getText().toString().trim();

                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(PopupForgotPassword.this, "Please enter your email address", Toast.LENGTH_LONG).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PopupForgotPassword.this, "Password has been sent to your email", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(PopupForgotPassword.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }) ;
                }
            }
        });

    }
}
