package com.vr233149gmail.chatmates.Fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vr233149gmail.chatmates.Activities.DeveloperProfile;
import com.vr233149gmail.chatmates.Activities.LoginPage;
import com.vr233149gmail.chatmates.Activities.Setting;
import com.vr233149gmail.chatmates.Adapters.UserAdapter;
import com.vr233149gmail.chatmates.Models.Users;
import com.vr233149gmail.chatmates.R;
import com.vr233149gmail.chatmates.databinding.FragmentChatBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    FragmentChatBinding binding;
    ArrayList<Users> list;
    UserAdapter adapter;
    Users users;
    FirebaseDatabase database;
    FirebaseAuth auth;


    public ChatFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.shimmer.startShimmer();
        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecycler.setLayoutManager(layoutManager);
        binding.chatRecycler.setHasFixedSize(true);
        binding.chatRecycler.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayout.VERTICAL));


        layoutManager.setSmoothScrollbarEnabled(true);


        getAllUsers();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        return binding.getRoot();

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_search:
                Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("Search user here...");
                SearchManager manager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
                searchView.setSearchableInfo(manager.getSearchableInfo(requireActivity().getComponentName()));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            Log.d(TAG, "onQueryTextSubmit: going into submit");
                            searchUser(s);

                        } else {
                            getAllUsers();
                        }
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            Log.d(TAG, "onQueryTextSubmit: going into change ");

                            searchUser(s);

                        } else {
                            getAllUsers();
                        }
                        return true;
                    }
                });
                return true;

            case R.id.logout:
                String currentId = auth.getUid();
                if (currentId != null) {
                    FirebaseDatabase.getInstance().getReference().child("Presence").child(currentId).setValue("Offline");
                }
                auth.signOut();
                Intent sintent = new Intent(getActivity(), LoginPage.class);
                startActivity(sintent);
                requireActivity().onBackPressed();
                break;
            case R.id.setting:

                Intent SettingIntent = new Intent(getActivity(), Setting.class);
                startActivity(SettingIntent);
                break;
            case R.id.share:
                shareApp();
                break;
            case R.id.developer:
                Log.d(TAG, "onOptionsItemSelected: I am a developer");
                Intent intent1 = new Intent(getActivity(), DeveloperProfile.class);
                startActivity(intent1);
                break;
            case R.id.privacy:
                Log.d(TAG, "onOptionsItemSelected: privacy policy");
                privacyPolicy();
                break;
            case R.id.term:
                Log.d(TAG, "onOptionsItemSelected: Terms");
                termsAndCondition();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        ApplicationInfo api = requireContext().getApplicationInfo();
        String apkPath = api.sourceDir;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Let's chat on Chatmate");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(apkPath)));
//                intent.putExtra(Intent.EXTRA_TEXT,"It's a fast,simple and secure app we can use to message and share image for free." +
//                        "Get it at https://play.google.com/store/apps/details?id=com.tutorial.personal.androidstudiopro ");
//                intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private void termsAndCondition() {
        String url = "https://procodehelp.blogspot.com/p/terms-and-conditions-chatmate.html";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void privacyPolicy() {
        String url = "https://procodehelp.blogspot.com/p/privacy-policy-chatmate.html";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void getAllUsers() {
        database.getReference().child("Users")
                .orderByChild("userName").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            users = dataSnapshot.getValue(Users.class);
                            assert users != null;
                            users.setUserId(dataSnapshot.getKey());
                            binding.shimmer.stopShimmer();
                            binding.shimmer.setVisibility(View.GONE);
                            binding.chatRecycler.setVisibility(View.VISIBLE);
                            if (!users.getUserId().equals(auth.getUid())) { //user logged in does not show up at user list
                                list.add(users);
                            }
                        }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    Collections.sort(list, Comparator.comparing(model::getTimeStamp));
//                }
                        adapter = new UserAdapter(list, getContext());
                        binding.chatRecycler.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
    }

    private void searchUser(@NonNull String s) {
        Log.d(TAG, "searchUser: " + s.toString());
        database.getReference().child("Users")
                .orderByChild("userName").startAt(s).endAt(s + "/uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            users = dataSnapshot.getValue(Users.class);
                            assert users != null;
                            users.setUserId(dataSnapshot.getKey());
                            if (users.getUserId() != null && !users.getUserId().equals(auth.getUid())) { //user logged in does not show up at user list
                                if (users.getUserName().toLowerCase().contains(s.toLowerCase()) ||
                                        users.getLastMessage().toLowerCase().contains(s.toLowerCase())
                                ) {
                                    list.add(users);

                                }
                            }
                        }
                        adapter = new UserAdapter(list, getContext());
                        adapter.notifyDataSetChanged();
                        binding.chatRecycler.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentId = auth.getUid();

        if (currentId != null) {
            FirebaseDatabase.getInstance().getReference().child("Presence").child(currentId).setValue("Online");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        String currentId = auth.getUid();
        if (currentId != null) {
            FirebaseDatabase.getInstance().getReference().child("Presence").child(currentId).setValue("Offline");
        }
    }

}
