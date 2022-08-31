package com.philcode.equalsadmin.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.Context;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.AddJobPostActivity;
import com.philcode.equalsadmin.adapters.JobAdapter;
import com.philcode.equalsadmin.models.Job;

import java.util.ArrayList;
import java.util.Collections;


public class JobFragment extends Fragment {

    RecyclerView rvJobItems;
    ArrayList<Job> jobs = new ArrayList<>();
    ArrayList categorySize;
    JobAdapter jobAdapter;
    DatabaseReference jobReference, categoryReference;
    StorageReference jobStorageReference;
    FirebaseStorage firebaseStorage;
    FirebaseAuth mAuth;
    FirebaseUser mUSer;
    String uid;
    Toolbar toolbar;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup jobRoot = (ViewGroup) inflater.inflate(R.layout.fragment_job, container, false);
        setHasOptionsMenu(true);

        context = requireActivity();

        //set toolbar
        toolbar = jobRoot.findViewById(R.id.toolbar_job);
//        toolbar.inflateMenu(R.menu.more_menu);

        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();
        uid = mUSer.getUid();

        jobs = new ArrayList<>();
        categorySize = new ArrayList<>();
        rvJobItems = jobRoot.findViewById(R.id.job_list);
        jobAdapter = new JobAdapter(getContext(), jobs, getActivity());
        rvJobItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvJobItems.setAdapter(jobAdapter);

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rvJobItems);

        firebaseStorage = FirebaseStorage.getInstance();
        jobReference = FirebaseDatabase.getInstance().getReference().child("Job_Offers");
        categoryReference = FirebaseDatabase.getInstance().getReference().child("Category");

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
        inflater.inflate(R.menu.more_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.add_job_post:
                    startActivity(new Intent(getContext(), AddJobPostActivity.class));
                    return true;
                case R.id.add_skill_category:
                    pop_up();
                    return true;
                case R.id.go_to_category:
                    FragmentManager fm = getFragmentManager();

                    return true;
                default:
                    return false;
            }
        });
    }

    private void pop_up(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Enter new skill category.");

        final EditText newSkillCategory = new EditText(context);
        newSkillCategory.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialog.setView(newSkillCategory);

        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categorySize.clear();
                categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                categorySize.add(snapshot1);
                            }
                            final int categoryListSize = categorySize.size();
                            categoryReference.child("skill" + categoryListSize).child("skill").setValue(newSkillCategory.getText().toString().trim());
                            Toast.makeText(getActivity(), "New Skill Category Added.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();

            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
        alertDialog.setCancelable(true);
    }

}