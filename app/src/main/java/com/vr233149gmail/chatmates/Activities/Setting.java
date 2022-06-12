package com.vr233149gmail.chatmates.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.vr233149gmail.chatmates.Models.Users;
import com.vr233149gmail.chatmates.R;
import com.vr233149gmail.chatmates.databinding.ActivitySettingBinding;

import java.util.HashMap;
import java.util.Objects;

public class Setting extends AppCompatActivity {
    ActivitySettingBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile pic ...");
        dialog.setCancelable(true);


        binding.backArrow2.setOnClickListener(view -> {
            Intent intent = new Intent(Setting.this, MainActivity.class);
            startActivity(intent);
            finish();

        });
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        SettingInfo();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        //setting information i.e profile pic and user name and about information


        ;
    }

    @NonNull
    private void validateName() {
        String val = Objects.requireNonNull(binding.userNames.getText()).toString();

        if (val.isEmpty()) {
            binding.userNames.setError("Field cannot be empty");

        }
    }

    public void imageProfile() {
        binding.profileImage.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 31);
        });
    }


    private void SettingInfo() {
        imageProfile();
        database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        assert users != null;
                        Picasso.get().load(users.getProfilePic())
                                .placeholder(R.drawable.user)
                                .into(binding.profileImage);
                        binding.userNames.setText(users.getUserName());
                        binding.emailId.setText(users.getMail());
                        binding.status.setText(users.getStatus());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 31) {
            if (data != null && data.getData() != null) {
                Uri file = data.getData();

                binding.profileImage.setImageURI(file);
                dialog.show();
                final StorageReference reference = storage.getReference().child("profile_Pics")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                reference.putFile(file).addOnSuccessListener(taskSnapshot -> {
                    dialog.dismiss();
                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        database.getReference().child("Users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child("profilePic")
                                .setValue(uri.toString());
                        Toast.makeText(this, "Profile Picture Updated Successfully", Toast.LENGTH_SHORT).show();
                    });
                });

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void SaveData(View view) {
        String status = Objects.requireNonNull(binding.status.getText()).toString();
        String username = binding.userNames.getText().toString();
        validateName();

        HashMap<String, Object> obj = new HashMap<>();
        obj.put("status", status);
        obj.put("userName", username);
        database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .updateChildren(obj);
        Toast.makeText(Setting.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
    }
}