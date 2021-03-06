package com.philcode.equalsadmin.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.AddPostActivity;
import com.philcode.equalsadmin.activities.MainActivity;
import com.philcode.equalsadmin.activities.PostDetailsActivity;
import com.philcode.equalsadmin.adapters.HomeAdapter;
import com.philcode.equalsadmin.models.Announcement;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment{

    RecyclerView rvPostItems;
    FloatingActionButton fab;
    Toolbar toolbar;
    private MainActivity mainActivity;
    ArrayList<Announcement> posts = new ArrayList<>();
    HomeAdapter homeAdapter;
    DatabaseReference postReference;
    FirebaseAuth mAuth;
    FirebaseUser mUSer;
    String uid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup homeRoot = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) getActivity();



        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();
        uid = mUSer.getUid();

        posts = new ArrayList<>();
        rvPostItems = homeRoot.findViewById(R.id.post_list);
        toolbar = homeRoot.findViewById(R.id.toolbar_home);
        toolbar.inflateMenu(R.menu.add_menu);


        homeAdapter = new HomeAdapter(getContext(), posts, getActivity());
        rvPostItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvPostItems.setAdapter(homeAdapter);

        postReference =  FirebaseDatabase.getInstance().getReference().child("home_content");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    Announcement postModel = snapShot.getValue(Announcement.class);

                    postModel.setPostImage(snapShot.child("postImage").getValue().toString());
                    posts.add(postModel);

                }
                Collections.reverse(posts);
                //update data on Firebase when changes has been made
                homeAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return homeRoot;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_menu, menu);
    }


    public void clearToolbarMenu() {

        toolbar.getMenu().clear();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.post_add_menu:
                    startActivity(new Intent(getContext(), AddPostActivity.class));
                    return true;
                default:
                    return false;
            }
        });
    }


}