package com.philcode.equalsadmin.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.LoginActivity;


public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    DatabaseReference databaseReference;
    String uid, name;
//    TextView profileName, profileEmail;
    Button logoutBtn;
    Activity activity;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        logoutBtn = root.findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}