package com.vr233149gmail.chatmates.Activities;/*
        //Google signUp need to done

 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.vr233149gmail.chatmates.Models.Users;
import com.vr233149gmail.chatmates.R;
import com.vr233149gmail.chatmates.databinding.ActivitySignUpBinding;

import java.util.Objects;

public class SignUp extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth auth;   //initializing authentication
    FirebaseDatabase database;  //initializing database
    ProgressDialog progressDialog;
    private static final String TAG = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //Dialog implementation
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating an account");
        progressDialog.setMessage("Account is been creating");
        //Already Have Account
        binding.logged.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this, LoginPage.class);
            startActivity(intent);
            Toast.makeText(SignUp.this, "User moved Logged in page", Toast.LENGTH_SHORT).show();

            finish();
        });
        setupHyperlink();
    }

    @NonNull
    private Boolean validateName() {
        String val = Objects.requireNonNull(binding.username.getText()).toString();

        if (val.isEmpty()) {
            binding.username.setError("Field cannot be empty");
            return false;
        } else {
            binding.username.setError(null);
            return true;
        }
    }

    @NonNull
    private Boolean validateEmail() {
        String val = Objects.requireNonNull(binding.emailId.getText()).toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            binding.emailId.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            binding.emailId.setError("Invalid email address");
            return false;
        } else {
            binding.emailId.setError(null);
            return true;
        }
    }
    @NonNull
    private Boolean validatePassword() {
        String val = binding.UserPass.getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            binding.UserPass.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            binding.UserPass.setError("Password is too weak");
            return false;
        } else {
            binding.UserPass.setError(null);
            return true;
        }
    }

    public void CreateNewAccount(View view) {

        if(!validateName() | !validatePassword() |!validateEmail()){
            return;
        }
        if (Objects.requireNonNull(binding.UserPass.getText()).toString().isEmpty()) {
            binding.UserPass.setError("Enter your Password");
            return;
        }
        if (binding.checkbox.isChecked()) {
            Log.d(TAG, "CreateNewAccount: " + binding.checkbox.isChecked());
            progressDialog.show();

            auth.createUserWithEmailAndPassword(Objects.requireNonNull(binding.emailId.getText()).toString(), binding.UserPass.getText().toString())
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Users users = new Users(Objects.requireNonNull(binding.username.getText()).toString(),
                                    binding.emailId.getText().toString()
                                    , binding.UserPass.getText().toString());
                            String id = Objects.requireNonNull(task.getResult().getUser()).getUid();
                            database.getReference().child("Users").child(id).setValue(users);

                            Toast.makeText(SignUp.this, "Welcome to Chatmate", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            startActivity(intent);
                            finish();
//
                        } else {
                            Toast.makeText(SignUp.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
        } else {
            Toast.makeText(this, "Please accept our terms and conditions to continue", Toast.LENGTH_SHORT).show();
        }

    }

    public void LoginPageSlider(View view) {
        startActivity(new Intent(this, LoginPage.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        this.finish();
    }

    @SuppressLint("ResourceAsColor")
    public void setupHyperlink() {
        binding.hyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        binding.hyperlink.setLinkTextColor(Color.BLUE);
    }
}