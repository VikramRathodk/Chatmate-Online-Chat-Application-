package com.vr233149gmail.chatmates.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.vr233149gmail.chatmates.R;
import com.vr233149gmail.chatmates.databinding.ActivityDeveloperProfileBinding;

public class DeveloperProfile extends AppCompatActivity {
    ActivityDeveloperProfileBinding binding;

    private static final String TAG = "DeveloperProfile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeveloperProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarDisplay);

        binding.linkedinVik.setOnClickListener(view -> followVikramLinkedIn());
        binding.githubVik.setOnClickListener(view -> followVikramGithub());
        binding.linkedinRam.setOnClickListener(view -> followLinkedInRam());
        binding.githubRam.setOnClickListener(view -> followGithubRam());


    }


    private void followVikramGithub() {
        String url = "https://github.com/VikramRathodk";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void followVikramLinkedIn() {
        String url = "https://www.linkedin.com/in/vikram-rathod-ab012122b/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    private void followGithubRam() {
        String url = "https://github.com/VikramRathodk";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    private void followLinkedInRam() {
        String url = "https://www.linkedin.com/in/vikram-rathod-ab012122b/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


}