package com.vr233149gmail.chatmates.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.vr233149gmail.chatmates.databinding.ActivityPhoneNumberBinding;

public class PhoneNumber extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        Objects.requireNonNull(getSupportActionBar()).hide();
        binding.PhoneBox.requestFocus();


        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            Intent intent = new Intent(PhoneNumber.this, MainActivity.class);
            startActivity(intent);
            finish();
        }



        binding.Continue.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                if(binding.PhoneBox.getText().toString().isEmpty()){
                    binding.PhoneBox.setError("Please Enter Valid Number");
                    return;
                }
                Intent intent = new Intent(PhoneNumber.this, OtpVerification.class);
                intent.putExtra("phoneNumber", binding.PhoneBox.getText().toString());
                startActivity(intent);
            }
        });
    }
}