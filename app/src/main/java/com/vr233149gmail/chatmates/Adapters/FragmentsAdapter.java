package com.vr233149gmail.chatmates.Adapters;

import android.provider.CallLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vr233149gmail.chatmates.Fragments.CallsFragment;
import com.vr233149gmail.chatmates.Fragments.ChatFragment;
import com.vr233149gmail.chatmates.Fragments.StatusFragment;

import javax.security.auth.callback.CallbackHandler;

public class FragmentsAdapter extends FragmentStateAdapter {
    String titles[] = new String[] {"Chats","Status","Calls"};


    public FragmentsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ChatFragment();
            case 1:
                return  new StatusFragment();
            case 2:
                return new CallsFragment();

            default:
                return new ChatFragment();
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }


    //deprecated
    //    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        String title= null;
//        if(position==0){
//            title= "CHATS";
//        }
//
//        if(position==1){
//            title= "STATUS";
//        }
//        if(position==2){
//            title= "CALLS";
//        }
//
//        return  title;
//    }
}

//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//        switch (position){
//            case 0:
//                return new ChatFragment();
//            case 1:
//                return  new StatusFragment();
//            case 2:
//                return new CallsFragment();
//
//            default:
//                return new ChatFragment();
//        }
//
//    }
//
//    @Override
//    public int getCount() {
//        return 3;
//    }
//


