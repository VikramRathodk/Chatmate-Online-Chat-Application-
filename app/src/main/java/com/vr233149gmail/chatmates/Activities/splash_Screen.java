package com.vr233149gmail.chatmates.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vr233149gmail.chatmates.R;


public class splash_Screen extends AppCompatActivity {
    private static final int SCREEN_TIME = 2000;
    TextView appName;
    ImageView gifImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        appName = findViewById(R.id.appName);
        gifImageView = findViewById(R.id.gifImageView);
        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the Menu-Activity. */
            Intent mainIntent = new Intent(splash_Screen.this, LoginPage.class);
            startActivity(mainIntent);
            finish();
        }, SCREEN_TIME);
        ScaleAnimation scaleAnim = new ScaleAnimation(
                0f, 1f,
                0f, 1f,
                Animation.ABSOLUTE, 1,
                Animation.RELATIVE_TO_SELF , 2);
        scaleAnim.setDuration(500);
        scaleAnim.setRepeatCount(0);
        scaleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnim.setFillAfter(true);
        scaleAnim.setFillBefore(true);
        scaleAnim.setFillEnabled(true);
        gifImageView.startAnimation(scaleAnim);
//        appName.animate().translationX(3000).translationY(4000).setDuration(3000).setStartDelay(2000);

//        gifImageView.animate().translationX(3000).translationY(4000).setDuration(3000).setStartDelay(2000);

    }
}