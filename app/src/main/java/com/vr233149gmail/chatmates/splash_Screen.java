package com.vr233149gmail.chatmates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.vr233149gmail.chatmates.databinding.ActivitySplashScreenBinding;

import java.util.Objects;

public class splash_Screen extends AppCompatActivity {
    private static final int SCREEN_TIME =2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();
        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the Menu-Activity. */
            Intent mainIntent = new Intent(splash_Screen.this,SignUp.class);
            startActivity(mainIntent);
            finish();
        }, SCREEN_TIME);
    }
}