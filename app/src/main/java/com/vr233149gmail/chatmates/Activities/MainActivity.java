package com.vr233149gmail.chatmates.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.FirebaseDatabase;
import com.vr233149gmail.chatmates.Adapters.FragmentsAdapter;
import com.vr233149gmail.chatmates.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    String[] titles = new String[]{"Chats"};
    ActivityMainBinding binding;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.e("MainError", e.getMessage());
        }
        //ids binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //creating custom toolbar
        binding.mainToolbar.setTitle("Chatmate");
        setSupportActionBar(binding.mainToolbar);

        //TabLayout and viewPages Implementation
        FragmentsAdapter adapter = new FragmentsAdapter(MainActivity.this);
        binding.viewPager.setAdapter(adapter);


        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(titles[position])).attach();

    }


}

