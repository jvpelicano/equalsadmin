package com.philcode.equalsadmin.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.AddJobPostActivity;
import com.philcode.equalsadmin.activities.AddPWDActivity;
import com.philcode.equalsadmin.activities.PostDetailsActivity;
import com.philcode.equalsadmin.adapters.CandidateAdapter;
import com.philcode.equalsadmin.models.Candidate;

import java.util.ArrayList;
import java.util.Collections;


public class PWDFragment extends Fragment {

    RecyclerView rvPwdItems;
    ArrayList<Candidate> candidates = new ArrayList<>();
    CandidateAdapter pwdAdapter;
    DatabaseReference pwdReference;
    FirebaseAuth mAuth;
    FirebaseUser mUSer;
    String uid;
    Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup pwdRoot = (ViewGroup) inflater.inflate(R.layout.fragment_p_w_d, container, false);
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();
        uid = mUSer.getUid();

        candidates = new ArrayList<>();
        rvPwdItems = pwdRoot.findViewById(R.id.pwd_list);
        pwdAdapter = new CandidateAdapter( getContext(), candidates, getActivity());
        rvPwdItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvPwdItems.setAdapter(pwdAdapter);

        //set toolbar
        toolbar = pwdRoot.findViewById(R.id.toolbar_pwd);
        toolbar.inflateMenu(R.menu.add_menu);

        pwdReference =  FirebaseDatabase.getInstance().getReference().child("PWD");
        pwdReference.orderByChild("typeStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                candidates.clear();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    Candidate pwdModel = snapShot.getValue(Candidate.class);

                    candidates.add(pwdModel);
                }
                //update data on Firebase when changes has been made
                Collections.reverse(candidates);
                pwdAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return pwdRoot;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
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
                    startActivity(new Intent(getContext(), AddPWDActivity.class));
                    return true;
                default:
                    return false;
            }
        });
    }

}