package com.vr233149gmail.chatmates.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import com.vr233149gmail.chatmates.Adapters.ChatAdapter;
import com.vr233149gmail.chatmates.Models.MessageModel;
import com.vr233149gmail.chatmates.R;
import com.vr233149gmail.chatmates.databinding.ActivityChatBinding;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    ActivityChatBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialogImage;
    ProgressDialog dialogPdf;
    String senderId;
    String message;

    String receiverId;
    String senderRoom;
    String receiverRoom;
    final int REQUEST_CODE1 = 35;
    final int REQUEST_CODE2 = 55;
    Uri selectedPdf = null;
    Uri selectedImage = null;
    ArrayList<MessageModel> messageModels = new ArrayList<>();
    MessageModel model;
    String currentId;


    public ChatActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialogImage = new ProgressDialog(this);
        dialogImage.setMessage("Sending image");

        dialogPdf = new ProgressDialog(this);
        dialogPdf.setMessage("Sending pdf");
        dialogPdf.setCancelable(true);
        //getting name form chatFragment
        String name = getIntent().getStringExtra("UserName");
        //getting profilePic from chatFragment
        String profilePic = getIntent().getStringExtra("ProfilePic");
        //setting name to chatActivity at toolbar
        binding.userName.setText(name);
        //setting profilePic got from chatFragment to chatActivity toolbar
        //placeholder for if user does not have profile pic so by default pic will be updated
        Picasso.get()
                .load(profilePic)
                .placeholder(R.drawable.user)
                .into(binding.profileImage);

        senderId = auth.getUid();
        receiverId = getIntent().getStringExtra("UserId");

        database.getReference().child("Presence").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    assert status != null;
                    if (!status.isEmpty()) {
                        if (status.equals("Offline")) {
                            binding.online.setVisibility(View.GONE);

                        } else {
                            binding.online.setText(status);
                            binding.online.setVisibility(View.VISIBLE);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ChatAdapter adapter = new ChatAdapter(messageModels, ChatActivity.this, receiverId);
        binding.chatRecycler2.setAdapter(adapter);
        binding.chatRecycler2.setItemViewCacheSize(messageModels.size());

        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this,
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setStackFromEnd(true);
        binding.chatRecycler2.setLayoutManager(layoutManager);


        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;
        database.getReference().child("chats")
                .child(senderRoom)
                .child("MessageId")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            MessageModel model = snapshot1.getValue(MessageModel.class);
                            assert model != null;
                            model.setMessageId(snapshot1.getKey());
                            messageModels.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        //sending messages
        binding.sendBtn.setOnClickListener(view -> {
            if (binding.chatText.getText().toString().isEmpty()) {
                binding.chatText.setError("Enter Message");
                return;
            }
            message = binding.chatText.getText().toString();
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            model = new MessageModel(senderId, message);
            model.setMessage(message);
            model.setTimeStamp(formatter.format(ts));
            binding.chatText.setText("");

            database.getReference().child("chats")
                    .child(senderRoom)
                    .child("MessageId")
                    .push()
                    .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("MessageId")
                                    .push()
                                    .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused1) {

                                        }
                                    });
                        }
                    });
        });
        //sending Document files
        binding.attachFile.setOnClickListener(view -> {
            boolean state = false;
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            attachMent();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            Toast.makeText(ChatActivity.this, "Storage permission required", Toast.LENGTH_SHORT).show();
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

        });
        StatusChanger();

    }

    public void attachMent() {
        final CharSequence[] items = {"IMAGE", "PDF"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Choose Following Options ");
        builder.setItems(items, (dialog, i) -> {
            switch (i) {
                case 0:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE1);
                    break;
                case 1:
                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_GET_CONTENT);
                    intent2.setType("application/*");
                    startActivityForResult(intent2, REQUEST_CODE2);
                    break;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    //uploading images into firebase storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE1) {
            if (data != null && data.getData() != null) {
                selectedImage = data.getData();
                UploadImage();
            }
        }
        if (requestCode == REQUEST_CODE2) {
            if (data != null && data.getData() != null) {
                selectedPdf = data.getData();
                UploadPdf();
            }


        }
    }

    public void GoingBackToMainActivity(View view) {
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void UploadImage() {
        Calendar calendar = Calendar.getInstance();
        StorageReference reference = storage.getReference().child("chats")
                .child("Images")
                .child(calendar.getTimeInMillis() + "");
        dialogImage.show();
        reference.putFile(selectedImage).addOnCompleteListener(task -> {
            dialogImage.dismiss();
            if (task.isSuccessful()) {
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    message = binding.chatText.getText().toString();
                    String filePath = uri.toString();
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                    final MessageModel model = new MessageModel(senderId, message);
                    model.setMessage("photo");
                    model.setImageUrl(filePath);
                    model.setUsername(uri.getUserInfo());
                    model.setTimeStamp(formatter.format(ts));
                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("MessageId")
                            .push()
                            .setValue(model).addOnSuccessListener(unused -> database.getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("MessageId")
                                    .push()
                                    .setValue(model).addOnSuccessListener(unused1 -> {

                                    }));

                });
            }
        });
    }

    private void UploadPdf() {
        Calendar calendar = Calendar.getInstance();
        StorageReference reference = storage.getReference().child("chats")
                .child("PDF")
                .child("Pdf Files")
                .child(calendar.getTimeInMillis() + "");
        dialogPdf.show();
        reference.putFile(selectedPdf).addOnCompleteListener(task -> {
            dialogPdf.dismiss();
            if (task.isSuccessful()) {
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    message = binding.chatText.getText().toString();
                    String filePath2 = uri.toString();
                    model = new MessageModel(senderId, message);
                    model.setMessage("pdf");
                    model.setPdfUrl(filePath2);
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                    model.setTimeStamp(formatter.format(ts));

                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("MessageId")
                            .push()
                            .setValue(model).addOnSuccessListener(unused -> database.getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("MessageId")
                                    .push()
                                    .setValue(model).addOnSuccessListener(unused1 -> {

                                    }));

                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentId = auth.getUid();
        database.getReference().child("Presence").child(currentId).setValue("Online");
    }

    @Override
    public void onPause() {
        super.onPause();
        currentId = auth.getUid();
        assert currentId != null;
        database.getReference().child("Presence").child(currentId).setValue("Offline");
    }

    private void StatusChanger() {
        Handler handler = new Handler();
        binding.chatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("Presence").child(senderId).setValue("Typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStopTyping, 1000);

            }

            final Runnable userStopTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("Presence").child(senderId).setValue("online");
                }
            };
        });
    }

    //to show menu bar at chat activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();  //instance of MenuInflater
        inflater.inflate(R.menu.chatmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //how to select options in the toolbar
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Profile) {

            Toast.makeText(this, "showDetails button was clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}