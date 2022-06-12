package com.vr233149gmail.chatmates.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vr233149gmail.chatmates.R;

public class ImageActivity extends AppCompatActivity {
//    TextView userName;
    FrameLayout layout ;
    ImageView fullPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
//        userName = findViewById(R.id.imageName);
        fullPic = findViewById(R.id.fullImage);
        layout = findViewById(R.id.framelayout);
        layout.setBackgroundColor(Color.TRANSPARENT);


        String profilePicFull = getIntent().getStringExtra("url");
        String name = getIntent().getStringExtra("name");
//        userName.setText(name);
        Picasso
                .get()
                .load(profilePicFull)
                .placeholder(R.drawable.user)
                .into(fullPic);


    }
}