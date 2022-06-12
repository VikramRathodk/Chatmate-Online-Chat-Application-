package com.vr233149gmail.chatmates.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.vr233149gmail.chatmates.R;
import com.vr233149gmail.chatmates.databinding.ActivityLoginPageBinding;

import java.util.Objects;


public class LoginPage extends AppCompatActivity {

    private static final String TAG = "LoginPage";
    ActivityLoginPageBinding binding;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        //Working with progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Getting logged in");


        //After clicking SignIn button
        binding.LoginBtn.setOnClickListener(this::onClick);
        //User is Already Logged in do this
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
            finish();

        }


        //  TO go back to SIgnUp page
        binding.signText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginPage.this, SignUp.class);
            startActivity(intent);
            finish();
        });
    }


    private void onClick(View view) {
        if(Objects.requireNonNull(binding.emailIdLogin.getText()).toString().isEmpty()){
            binding.emailIdLogin.setError("This field can not empty");
        }if(Objects.requireNonNull(binding.userPassLogin.getText()).toString().isEmpty()){
            binding.userPassLogin.setError("This field can not empty");
        }
        progressDialog.show();
        auth.signInWithEmailAndPassword(Objects.requireNonNull(binding.emailIdLogin.getText()).toString(), Objects.requireNonNull(binding.userPassLogin.getText()).toString())
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        String currentId = auth.getUid();
                        if (currentId != null) {
                            FirebaseDatabase.getInstance().getReference().child("Presence").child(currentId).setValue("Online");
                        }
                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginPage.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void RegisterPageSlider(View view) {
        startActivity(new Intent(this, SignUp.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
        this.finish();

    }

    public void forgotPassword(View view) {
        Toast.makeText(this, "clicked on forgot password", Toast.LENGTH_SHORT).show();
        final EditText resetMail = new EditText(LoginPage.this);
        new MaterialAlertDialogBuilder(LoginPage.this)
                .setTitle("Reset Password ?")
                .setView(resetMail)
                .setMessage("Enter Your Email to Change Password")
                .setPositiveButton("Send", (dialogInterface, i) -> {
                    String mail = resetMail.getText().toString();
                    auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(LoginPage.this, "Reset link sent to your email address", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginPage.this, "Reset link is no sent", Toast.LENGTH_SHORT).show();

                        }
                    });

                }).setNegativeButton("No",null)
                .show();
    }
}