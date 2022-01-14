package com.philcode.equalsadmin.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.philcode.equalsadmin.R;


public class RegisterCandidateStep1_Fragment extends Fragment {
    View view;
    MaterialButton btnNext_fragment1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register_candidate_step1, container, false);
        btnNext_fragment1 = view.findViewById(R.id.btnNext_fragment1);

        btnNext_fragment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.addPWD_frameLayout, new RegisterCandidateStep2_Fragment()).commit();
            }
        });


        return view;
    }
}