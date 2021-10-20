package com.philcode.equalsadmin.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.LoginActivity;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ImageView adminImg;
    private TextInputEditText adminEmail;
    private TextView adminName;

    private Button logoutBtn, changePasswordBtn;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Admin");

        adminImg = root.findViewById(R.id.admin_img);
        adminName = root.findViewById(R.id.admin_name);
        adminEmail = root.findViewById(R.id.admin_email);
        logoutBtn = root.findViewById(R.id.btnLogout);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Query query = databaseReference.orderByChild("uid").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String email = "" + ds.child("email").getValue();
                    String name = "" + ds.child("name").getValue();
                    String image = "" + ds.child("image").getValue();

                    //set data
                    adminEmail.setText(email);
                    adminName.setText(name);
//
//                    try {
//                        Picasso.get().load(image).placeholder(R.drawable.profile).into(avatarIv);
//                    }
//                    catch (Exception e){
//                        //Picasso.get().load(R.drawable.profile).into(avatarIv);
//                        avatarIv.setImageResource(R.drawable.profile);
//                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }


}