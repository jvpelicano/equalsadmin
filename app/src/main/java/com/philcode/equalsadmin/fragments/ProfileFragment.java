package com.philcode.equalsadmin.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
    private ProgressDialog pd;

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
        changePasswordBtn = root.findViewById(R.id.btn_change_pw);

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        //init progress dialog
        pd = new ProgressDialog(getActivity());

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

                    try {
                        Picasso.get().load(image).placeholder(R.drawable.emp_placeholder)
                                .centerCrop().fit().into(adminImg);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.emp_placeholder).centerCrop().fit().into(adminImg);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void showEditProfileDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_update_password, null);
        final EditText oldpassword = view.findViewById(R.id.admin_old_pw);
        final EditText newpassword = view.findViewById(R.id.admin_new_pw);
        Button updatePwordBtn = view.findViewById(R.id.btn_updatePw);
        Button cancelPwordBtn = view.findViewById(R.id.btn_cancelPw);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        updatePwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate data
                String oPassword = oldpassword.getText().toString().trim();
                String nPassword = newpassword.getText().toString().trim();
                if (oPassword.isEmpty()){
                    Toast.makeText(getContext(), "Enter current password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (nPassword.isEmpty()){
                    Toast.makeText(getContext(), "Enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (nPassword.length()<6){
                    Toast.makeText(getContext(), "Password length must atleast 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                updatePassword(oPassword, nPassword);
            }
        });
        cancelPwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void updatePassword(String oPassword, final String nPassword) {
        //get current user
        final FirebaseUser user = mAuth.getCurrentUser();
        //before changing password re-authenticate the user
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //successful authenticated begin update
                        user.updatePassword(nPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //password updated
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Password Updated", Toast.LENGTH_SHORT ).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //authentication failed
                        pd.dismiss();
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}