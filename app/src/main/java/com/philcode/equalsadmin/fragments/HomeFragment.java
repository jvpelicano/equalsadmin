package com.philcode.equalsadmin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.adapters.HomeAdapter;
import com.philcode.equalsadmin.models.Announcement;

import java.util.ArrayList;

public class HomeFragment extends Fragment{

    RecyclerView rvPostItems;
    ArrayList<Announcement> posts = new ArrayList<>();
    HomeAdapter homeAdapter;
    DatabaseReference postReference;
    FirebaseAuth mAuth;
    FirebaseUser mUSer;
    String uid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();
        uid = mUSer.getUid();

        posts = new ArrayList<>();
        rvPostItems = root.findViewById(R.id.post_list);
        homeAdapter = new HomeAdapter(posts, getActivity());
        rvPostItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvPostItems.setAdapter(homeAdapter);

        postReference =  FirebaseDatabase.getInstance().getReference().child("home_content");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    Announcement postModel = snapShot.getValue(Announcement.class);

                    posts.add(postModel);

                }
                //update data on Firebase when changes has been made
                homeAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    return root;
    }
}