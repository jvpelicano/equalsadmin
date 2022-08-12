package com.philcode.equalsadmin.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.AddEmployerActivity;
import com.philcode.equalsadmin.activities.AddJobPostActivity;
import com.philcode.equalsadmin.activities.AddPWDActivity;
import com.philcode.equalsadmin.adapters.EmployerAdapter;
import com.philcode.equalsadmin.models.Employer;
import java.util.ArrayList;
import java.util.Collections;


public class EmpFragment extends Fragment {

    RecyclerView rvEmpItems;
    ArrayList<Employer> employers = new ArrayList<>();
    EmployerAdapter empAdapter;
    DatabaseReference empReference;
    FirebaseAuth mAuth;
    FirebaseUser mUSer;
    String uid;
    Toolbar toolbar;



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

        //set toolbar and toolbar menu
        toolbar = root.findViewById(R.id.toolbar_emp);
        toolbar.inflateMenu(R.menu.add_menu);

        empReference =  FirebaseDatabase.getInstance().getReference().child("Employers");
        empReference.orderByChild("typeStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employers.clear();
                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                    Employer empModel = snapShot.getValue(Employer.class);
                    employers.add(empModel);
                    try {
//
                    if(snapShot.hasChild("empProfilePic")){
                        empModel.setAvatar(snapShot.child("empProfilePic").getValue().toString());
                    }


                    }catch (Exception e){
                    }

                }
                Collections.reverse(employers);
                //update data on Firebase when changes has been made
                empAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
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
                    startActivity(new Intent(getContext(), AddEmployerActivity.class));
                    return true;
                default:
                    return false;
            }
        });
    }

}