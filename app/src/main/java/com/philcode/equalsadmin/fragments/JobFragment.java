package com.philcode.equalsadmin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.adapters.EmployerAdapter;
import com.philcode.equalsadmin.adapters.JobAdapter;
import com.philcode.equalsadmin.models.Employer;
import com.philcode.equalsadmin.models.Job;

import java.util.ArrayList;


public class JobFragment extends Fragment {

    RecyclerView rvJobItems;
    ArrayList<Job> jobs = new ArrayList<>();
    JobAdapter jobAdapter;
    DatabaseReference jobReference;
    FirebaseAuth mAuth;
    FirebaseUser mUSer;
    String uid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup jobRoot = (ViewGroup) inflater.inflate(R.layout.fragment_job, container, false);
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();
        uid = mUSer.getUid();

        jobs = new ArrayList<>();
        rvJobItems = jobRoot.findViewById(R.id.job_list);
        jobAdapter = new JobAdapter(getContext(), jobs, getActivity());
        rvJobItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvJobItems.setAdapter(jobAdapter);

        jobReference = FirebaseDatabase.getInstance().getReference().child("Job_Offers");
        jobReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobs.clear();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    Job jobModel = snapShot.getValue(Job.class);

                    try {
                        jobModel.setImageURL(snapShot.child("imageURL").getValue().toString());
                        jobs.add(jobModel);
                        Toast.makeText(getActivity(), "Connected in Firebase", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error in Fetching Data", Toast.LENGTH_LONG).show();
                    }

                }
                //update data on Firebase when changes has been made
                jobAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return jobRoot;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.add_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


}