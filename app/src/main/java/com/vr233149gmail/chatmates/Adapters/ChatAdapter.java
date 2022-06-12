package com.vr233149gmail.chatmates.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.vr233149gmail.chatmates.Activities.ImageActivity2;
import com.vr233149gmail.chatmates.Models.MessageModel;
import com.vr233149gmail.chatmates.Models.Users;
import com.vr233149gmail.chatmates.R;

import java.util.ArrayList;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ChatAdapter";
    ArrayList<MessageModel> list;
    Context context;
    String receiverId;
    MessageModel messageModel;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;


    public ChatAdapter(ArrayList<MessageModel> list, Context context, String receiverId) {
        this.list = list;
        this.context = context;
        this.receiverId = receiverId;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_reciever, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        messageModel = list.get(position);
        holder.setIsRecyclable(false);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;

            viewHolder.senderMsg.setText(messageModel.getMessage());
            viewHolder.itemView.setOnLongClickListener(view -> {
                deleteData();
                return true;
            });
            viewHolder.senderTime.setText(messageModel.getTimeStamp());

            if (Objects.equals(messageModel.getMessage(), "photo")) {
                viewHolder.senderMsg.setVisibility(View.GONE);
                viewHolder.senderImage.setVisibility(View.VISIBLE);
                Picasso.get().load(messageModel.getImageUrl())
                        .resize(600, 900)
                        .placeholder(R.drawable.loading)
                        .into(viewHolder.senderImage);
                viewHolder.senderImage.setMaxHeight(900);
                viewHolder.senderImage.setMaxWidth(600);
                viewHolder.senderImage.setOnClickListener(view -> {

                    Intent intent = new Intent(view.getContext(), ImageActivity2.class);
                    String url = messageModel.getImageUrl();
                    intent.putExtra("url", url);
                    intent.putExtra("name", messageModel.getUsername());
                    context.startActivity(intent);
                });
//                viewHolder.senderImage.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//
//                        deleteData();
//                        return true;
//                    }
//                });

//
            }
            if (messageModel.getMessage().equals("pdf")) {
                viewHolder.senderMsg.setVisibility(View.GONE);
                viewHolder.senderImage.setVisibility(View.GONE);
                viewHolder.senderPdf.setVisibility(View.VISIBLE);
                Picasso.get().load(messageModel.getPdfUrl())
                        .placeholder(R.drawable.pdfholder)
                        .resize(700, 450)
                        .into(viewHolder.senderPdf);
                viewHolder.senderPdf.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(messageModel.getPdfUrl()), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    try {
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        Log.d(TAG, "onBindViewHolder: " + e.getLocalizedMessage());
                    }
                });
//                viewHolder.senderPdf.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//                        deleteData();
//                        return true;
//                    }
//                });
//

            }
        }

        else {
            ReceiverViewHolder RviewHolder = (ReceiverViewHolder) holder;
            RviewHolder.receiverMsg.setText(messageModel.getMessage());
            RviewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    deleteReceiverData();
                    return false;
                }
            });
            RviewHolder.receiverTime.setText(messageModel.getTimeStamp());

            if (Objects.equals(messageModel.getMessage(), "photo")) {
                RviewHolder.receiverMsg.setVisibility(View.GONE);
                RviewHolder.receiverImage.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(messageModel.getImageUrl())
                        .resize(600, 900)
                        .centerCrop()
                        .placeholder(R.drawable.loading)
                        .into(RviewHolder.receiverImage);
                RviewHolder.receiverImage.setOnClickListener(view -> {
                    Intent intent = new Intent(context, ImageActivity2.class);
                    intent.putExtra("url", messageModel.getImageUrl());
                    intent.putExtra("name", messageModel.getUsername());
                    context.startActivity(intent);
                });
//                RviewHolder.receiverImage.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//                        deleteReceiverData();
//                        return false;
//                    }
////                }
//                );
//
            }
            if (Objects.equals(messageModel.getMessage(), "pdf")) {
                RviewHolder.receiverMsg.setVisibility(View.GONE);
                RviewHolder.receiverImage.setVisibility(View.GONE);
                RviewHolder.receiverPdf.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(messageModel.getPdfUrl())
                        .placeholder(R.drawable.pdfholder)
                        .into(RviewHolder.receiverPdf);
                RviewHolder.receiverPdf.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(messageModel.getPdfUrl()), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    try {
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        Log.d(TAG, "onBindViewHolder: "+e.getLocalizedMessage());
                    }
                });
//                RviewHolder.receiverPdf.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//                        deleteReceiverData();
//                        return false;
//                    }
//                });
            }

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (Objects.equals(list.get(position).getuId(), FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void deleteData() {
        Log.d(TAG, "deleteData: "+messageModel.getMessageId());
        new MaterialAlertDialogBuilder(context)
                .setIcon(R.drawable.delete)
                .setTitle("Delete data")
                .setMessage("Are You Sure You Want To Delete Data ? ")
                .setPositiveButton("DELETE FOR ME", (dialogInterface, i) -> {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                    database.getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child("MessageId")
                            .child(messageModel.getMessageId())
                            .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Message Successfully deleted", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> Log.d(TAG, "onFailure: "+e.getLocalizedMessage()));
                })
                .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();

    }public void deleteReceiverData() {
        Log.d(TAG, "deleteReceiverData: "+messageModel.getMessageId());
        new MaterialAlertDialogBuilder(context)
                .setIcon(R.drawable.delete)
                .setPositiveButton("Delete for me", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                         String receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();
                        FirebaseDatabase.getInstance().getReference()
                                .child("chats")
                                .child(receiverRoom)
                                .child("MessageId")
                                .child(messageModel.getMessageId())
                                .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Receivers Message Successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: "+e.getLocalizedMessage()));
                    }
                })
                .setTitle("Delete data")
                .setMessage("Are You Sure You Want To Delete Data ? ")
                .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();

    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg, receiverTime;
        ImageView receiverImage, receiverPdf;

        //        VideoView viewVideo;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiveText);
            receiverTime = itemView.findViewById(R.id.receiveTime);
            receiverImage = itemView.findViewById(R.id.image);
            receiverPdf = itemView.findViewById(R.id.imagePdf);
        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;
        ImageView senderImage, senderPdf;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            senderImage = itemView.findViewById(R.id.image);
            senderPdf = itemView.findViewById(R.id.imagePdf);
        }
    }
}
