package com.philcode.equalsadmin.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.AddJobPostActivity;
import com.philcode.equalsadmin.activities.AddPostActivity;
import com.philcode.equalsadmin.adapters.EmployerAdapter;
import com.philcode.equalsadmin.adapters.JobAdapter;
import com.philcode.equalsadmin.models.Employer;
import com.philcode.equalsadmin.models.Job;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;


public class JobFragment extends Fragment {

    RecyclerView rvJobItems;
    ArrayList<Job> jobs = new ArrayList<>();
    JobAdapter jobAdapter;
    DatabaseReference jobReference;
    StorageReference jobStorageReference;
    FirebaseStorage firebaseStorage;
    FirebaseAuth mAuth;
    FirebaseUser mUSer;
    String uid;
    Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup jobRoot = (ViewGroup) inflater.inflate(R.layout.fragment_job, container, false);
        setHasOptionsMenu(true);

        //set toolbar
        toolbar = jobRoot.findViewById(R.id.toolbar_job);
        toolbar.inflateMenu(R.menu.add_menu);

        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();
        uid = mUSer.getUid();

        jobs = new ArrayList<>();
        rvJobItems = jobRoot.findViewById(R.id.job_list);
        jobAdapter = new JobAdapter(getContext(), jobs, getActivity());
        rvJobItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvJobItems.setAdapter(jobAdapter);

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rvJobItems);

        firebaseStorage = FirebaseStorage.getInstance();
        jobReference = FirebaseDatabase.getInstance().getReference().child("Job_Offers");

        getData();

        return jobRoot;
    }

    //Delete on swipe function
    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Do you want to proceed?");
            builder.setMessage("By tapping the proceed button, you agree to delete the selected job post.");
            builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String postJobID = jobs.get(viewHolder.getAdapterPosition()).getPostJobId();
                    String imageURL = jobs.get(viewHolder.getAdapterPosition()).getImageURL();
                    jobReference.child(postJobID).removeValue();
                    jobStorageReference = firebaseStorage.getReferenceFromUrl(imageURL);
                    jobStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Data deleted successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), (CharSequence) e, Toast.LENGTH_SHORT).show();
                        }
                    });
                    jobs.remove(viewHolder.getAdapterPosition());
                    jobAdapter.notifyDataSetChanged();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    getData();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(true);

        }
    };

    public void getData(){
        jobReference.orderByChild("permission").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobs.clear();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    Job jobModel = snapShot.getValue(Job.class);
                    try {
                        jobModel.setImageURL(snapShot.child("imageURL").getValue().toString());
                        jobs.add(jobModel);

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Error in Fetching Data", Toast.LENGTH_LONG).show();
                    }

                }
                //update data on Firebase when changes has been made
                Collections.reverse(jobs);
                jobAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.add_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.post_add_menu:
                    startActivity(new Intent(getContext(), AddJobPostActivity.class));
                    return true;
                default:
                    return false;
            }
        });
    }


}