package com.vr233149gmail.chatmates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.vr233149gmail.chatmates.Models.Users;
import com.vr233149gmail.chatmates.databinding.ActivitySignUpBinding;

import java.util.Objects;

public class SignUp extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth auth;   //initializing authentication
    FirebaseDatabase database;  //initializing database
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating an account");
        progressDialog.setMessage("Account is been creating");



        binding.signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();
                auth.createUserWithEmailAndPassword(binding.emailId.getText().toString(),binding.UserPass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()){
                                    Users users = new Users(binding.username.getText().toString(),binding.emailId.getText().toString()
                                    ,binding.UserPass.getText().toString());
                                    String id = Objects.requireNonNull(task.getResult().getUser()).getUid();
                                    database.getReference().child("Users").child(id).setValue(users);

                                    Toast.makeText(SignUp.this, "user sign in successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUp.this,MainActivity.class);
                                    startActivity(intent);
//
                                }else{
                                    Toast.makeText(SignUp.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

            //Already Have Account
        binding.logged.setOnClickListener(view -> {
            Intent intent = new Intent(SignUp.this,Login_Page.class);
            startActivity(intent);
            Toast.makeText(SignUp.this, "User moved Logged in page", Toast.LENGTH_SHORT).show();
        });
    }
}