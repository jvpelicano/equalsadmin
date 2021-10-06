package com.philcode.equalsadmin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
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
import com.philcode.equalsadmin.adapters.CandidateAdapter;
import com.philcode.equalsadmin.models.Candidate;

import java.util.ArrayList;


public class PWDFragment extends Fragment {

    RecyclerView rvPwdItems;
    ArrayList<Candidate> candidates = new ArrayList<>();
    CandidateAdapter pwdAdapter;
    DatabaseReference pwdReference;
    FirebaseAuth mAuth;
    FirebaseUser mUSer;
    String uid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup pwdRoot = (ViewGroup) inflater.inflate(R.layout.fragment_p_w_d, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();
        uid = mUSer.getUid();

        candidates = new ArrayList<>();
        rvPwdItems = pwdRoot.findViewById(R.id.pwd_list);
        pwdAdapter = new CandidateAdapter( getContext(), candidates, getActivity());
        rvPwdItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvPwdItems.setAdapter(pwdAdapter);

        pwdReference =  FirebaseDatabase.getInstance().getReference().child("PWD");
        pwdReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                candidates.clear();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    Candidate pwdModel = snapShot.getValue(Candidate.class);

                    candidates.add(pwdModel);
                }
                //update data on Firebase when changes has been made

                pwdAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return pwdRoot;
    }


}