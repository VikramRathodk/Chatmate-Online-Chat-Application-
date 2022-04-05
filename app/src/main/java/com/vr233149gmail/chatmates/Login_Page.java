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
import com.vr233149gmail.chatmates.databinding.ActivityLoginPageBinding;

import java.util.Objects;

public class Login_Page extends AppCompatActivity {
    ActivityLoginPageBinding  binding;
    private FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();       //hide the toolbar

        //Firebase instances
        auth = FirebaseAuth.getInstance();

        //Working with progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Getting logged in");

        //After clicking SignIn button
        binding.LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                auth.signInWithEmailAndPassword(binding.emailIdLogin.getText().toString(),binding.UserPassLogin.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if(task.isSuccessful()) {
                                    Intent intent = new Intent(Login_Page.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(Login_Page.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
        //User is Already Logged in do this
        if(auth.getCurrentUser()!= null){
            Intent intent = new Intent(Login_Page.this,MainActivity.class);
            startActivity(intent);

        }


        //  TO go back to SIgnUp page
        binding.signText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_Page.this,SignUp.class);
                startActivity(intent);
                Toast.makeText(Login_Page.this, "user back to sign up activity", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}