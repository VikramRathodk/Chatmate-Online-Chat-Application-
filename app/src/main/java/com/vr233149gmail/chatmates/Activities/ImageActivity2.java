package com.vr233149gmail.chatmates.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vr233149gmail.chatmates.R;
import com.vr233149gmail.chatmates.databinding.ActivityImage2Binding;

public class ImageActivity2 extends AppCompatActivity {
    ActivityImage2Binding binding;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImage2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        dialog.show();


        String name = getIntent().getStringExtra("name");
        binding.usernamee.setText(name);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageActivity2.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        loadImage();
        dialog.dismiss();


    }

    public void loadImage() {
        String url = getIntent().getStringExtra("url");

        Picasso
                .get()
                .load(url)
                .resize(1000, 1000)
                .centerInside()
                .placeholder(R.color.white)
                .into(binding.imagefully);


    }


}