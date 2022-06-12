package com.vr233149gmail.chatmates.Adapters;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vr233149gmail.chatmates.Activities.ChatActivity;
import com.vr233149gmail.chatmates.Activities.ImageActivity;
import com.vr233149gmail.chatmates.Models.Users;
import com.vr233149gmail.chatmates.R;

import java.util.ArrayList;
import java.util.Objects;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<Users> list;
    Context context;


    public UserAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_user, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users users = list.get(position);
        holder.setIsRecyclable(false);

        Picasso.get().load(users.getProfilePic())
                .placeholder(R.drawable.user)
                .into(holder.image);
        holder.userName.setText(users.getUserName());
        if (users.getStatus() != null && users.getStatus().isEmpty()) {
            holder.lastMessage.setText(users.getStatus());
        } else {
            holder.lastMessage.setText("Tab to chat");

        }

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(FirebaseAuth.getInstance().getUid() + users.getUserId())
                .child("MessageId")
                .orderByChild("timeStamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                holder.lastMessage.setText(snapshot1.child("message").getValue(String.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(FirebaseAuth.getInstance().getUid() + users.getUserId())
                .child("MessageId")
                .orderByChild("message")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                holder.timeAtChat.setText(snapshot1.child("timeStamp").getValue(String.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.image.setOnClickListener(view -> {

            Intent intent = new Intent(context, ImageActivity.class);
            intent.putExtra("url", users.getProfilePic());
            intent.putExtra("name", users.getUserName());
            context.startActivity(intent);
        });

        //moving towards chatActivity
        //Sending data from main Activity to chat activity tool bar
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("UserId", users.getUserId());
            intent.putExtra("ProfilePic", users.getProfilePic());
            intent.putExtra("UserName", users.getUserName());
            context.startActivity(intent);

            FirebaseDatabase.getInstance().getReference().child("chats")
                    .orderByPriority().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        });


    }


    @Override
    public int getItemCount() {return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView userName, lastMessage, timeAtChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.UserName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timeAtChat = itemView.findViewById(R.id.TimeAtChat);

        }
    }


}