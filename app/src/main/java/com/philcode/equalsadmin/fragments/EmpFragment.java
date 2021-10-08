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
import com.philcode.equalsadmin.adapters.EmployerAdapter;
import com.philcode.equalsadmin.models.Employer;
import java.util.ArrayList;


public class EmpFragment extends Fragment {

    RecyclerView rvEmpItems;
    ArrayList<Employer> employers = new ArrayList<>();
    EmployerAdapter empAdapter;
    DatabaseReference empReference;
    FirebaseAuth mAuth;
    FirebaseUser mUSer;
    String uid;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_emp, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();
        uid = mUSer.getUid();

        employers = new ArrayList<>();
        rvEmpItems = root.findViewById(R.id.emp_list);
        empAdapter = new EmployerAdapter( getContext(), employers, getActivity());
        rvEmpItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvEmpItems.setAdapter(empAdapter);

        empReference =  FirebaseDatabase.getInstance().getReference().child("Employers");
        empReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employers.clear();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    Employer empModel = snapShot.getValue(Employer.class);

                    try {
                        empModel.setAvatar(snapShot.child("empProfilePic").getValue().toString());
                        employers.add(empModel);


                    }catch (Exception e){
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                    }

                }
                //update data on Firebase when changes has been made
                empAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }


}