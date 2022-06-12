package com.vr233149gmail.chatmates.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.vr233149gmail.chatmates.Models.Users;
import com.vr233149gmail.chatmates.R;
import com.vr233149gmail.chatmates.databinding.ActivityOtpVerificationBinding;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

/*

Authentication wala part karana baki hai
phone authentication
 */
public class OtpVerification extends AppCompatActivity {

    ActivityOtpVerificationBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    String verificationId;

    ProgressDialog dialog;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);
        dialog.show();

        auth = FirebaseAuth.getInstance();

        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        binding.Verify.setText("Verify " + phoneNumber);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OtpVerification.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {


                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        dialog.dismiss();

                        verificationId = verifyId;
//
//                        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                        binding.otpView.requestFocus();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);


        OtpTextView otpTextView;
        otpTextView = findViewById(R.id.otpView);
        otpTextView.setOtpListener(new OTPListener() {
        	@Override
        	public void onInteractionListener() {
        	// fired when user types something in the Otpbox

        	}
        	@Override
        	public void onOTPComplete(@NonNull String otp) {
        	// fired when user has entered the OTP fully.
        	    Toast.makeText(OtpVerification.this, "The OTP is " + otp,  Toast.LENGTH_SHORT).show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

        auth.signInWithCredential(credential).addOnCompleteListener(new
             OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful()) {
                      Intent intent = new Intent(OtpVerification.this, MainActivity.class);
                      startActivity(intent);
                      finishAffinity();

                      } else {
                            Toast.makeText(OtpVerification.this, "Failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
              });
            }
        });
    }
}