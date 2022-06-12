package com.vr233149gmail.chatmates.Adapters;

import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vr233149gmail.chatmates.Fragments.CallsFragment;
import com.vr233149gmail.chatmates.Fragments.ChatFragment;
import com.vr233149gmail.chatmates.Fragments.StatusFragment;

public class FragmentsAdapter extends FragmentStateAdapter {
    String[] titles = new String[] {"Chats"};


    public FragmentsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new ChatFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }


}



