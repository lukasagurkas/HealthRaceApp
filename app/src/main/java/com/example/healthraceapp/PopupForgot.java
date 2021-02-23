package com.example.healthraceapp;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;

public class PopupForgot  extends LoginActivity{

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
    }
}
