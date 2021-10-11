package com.philcode.equalsadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PWDDetailsActivity extends AppCompatActivity {

    private CircleImageView pwdDetailsImg;
    private String email;
    RelativeLayout pwdDetail;
    private TextView viewPWDFname, viewPWDLname, pwdBadge, resumeLink;
    private TextInputEditText viewPWDEmail, viewPWDPhone, viewPWDAdd;
    private TextView pwdDisability1, pwdDisability2, pwdDisability3, pwdDisability4;
    private ImageView viewPWDId, pwdBadgeIcon;
    private Button updatePWDStatus;
    private ProgressDialog pd;
    private String uid;
    private String resume;


    //firebase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pwdDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwddetails);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getSupportActionBar().setTitle("Candidate Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        pwdDetailsImg = findViewById(R.id.pwd_profile_img);
        viewPWDFname = findViewById(R.id.pwd_profile_fname);
        viewPWDLname = findViewById(R.id.pwd_profile_lname);
        viewPWDEmail = findViewById(R.id.pwd_profile_email);
        viewPWDPhone = findViewById(R.id.pwd_profile_phone);
        viewPWDAdd = findViewById(R.id.pwd_profile_address);
        viewPWDId = findViewById(R.id.pwd_profile_id);
        pwdBadge = findViewById(R.id.pwd_verified_acc);
        pwdBadgeIcon = findViewById(R.id.pwd_verified_icon);
        updatePWDStatus = findViewById(R.id.pwd_update_status_btn);
        pwdDetail = findViewById(R.id.pwd_details_layout);
        resumeLink = findViewById(R.id.pwd_resume_link);
        pwdDisability1 = findViewById(R.id.pwd_details_disability1);
        pwdDisability2 = findViewById(R.id.pwd_details_disability2);
        pwdDisability3 = findViewById(R.id.pwd_details_disability3);
        pwdDisability4 = findViewById(R.id.pwd_details_disability4);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Updating Status..");

        //Click Update Button
        updatePWDStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        resumeLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               showLink();
            }
        });

        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        pwdDbRef = firebaseDatabase.getReference("PWD");

        Query pwdQuery = pwdDbRef.orderByChild("email").equalTo(email);
        pwdQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    uid = ds.getKey();

                    //get data
                    String emailAdd = "" + ds.child("email").getValue();
                    String lName = "" + ds.child("lastName").getValue();
                    String fName = "" + ds.child("firstName").getValue();
                    String image = "" + ds.child("pwdProfilePic").getValue();
                    String phone = "" + ds.child("contact").getValue();
                    String pwdId = "" + ds.child("pwdIdCardNum").getValue();
                    String homeAdd1 = "" + ds.child("address").getValue();
                    String homeAdd2 = "" + ds.child("city").getValue();
                    String status = "" + ds.child("typeStatus").getValue();
                    resume = "" + ds.child("resumeFile").getValue();
//                    String disability1 = "" + ds.child("typeOfDisability1").getValue();
//                    String disability2 = "" + ds.child("typeOfDisability2").getValue();
//                    String disability3 = "" + ds.child("typeOfDisability3").getValue();
//                    String disability4 = "" + ds.child("typeOfDisability4").getValue();

                    if(status.equals("PWDApproved")){
                        pwdBadgeIcon.setVisibility(View.VISIBLE);
                        pwdBadge.setText("Verified Account");
                        pwdBadge.setTextColor(Color.parseColor("#008000"));
                        updatePWDStatus.setVisibility(View.GONE);
                    }
                    else if (status.equals("PWDPending")){
                        pwdBadgeIcon.setVisibility(View.GONE);
                        pwdBadge.setText("For Verification");
                        pwdBadge.setTextColor(Color.parseColor("#FF1414"));
                        updatePWDStatus.setVisibility(View.VISIBLE);
                    }
                    else{
                        pwdBadgeIcon.setVisibility(View.GONE);
                        pwdBadge.setText("Cancelled");
                        pwdBadge.setTextColor(Color.parseColor("#808080"));
                        updatePWDStatus.setVisibility(View.VISIBLE);
                    }
                    //set data
                    viewPWDFname.setText(fName);
                    viewPWDLname.setText(lName);
                    viewPWDEmail.setText(emailAdd);
                    viewPWDPhone.setText(phone);
                    viewPWDAdd.setText(homeAdd1 + " " + homeAdd2);
                    resumeLink.setText(R.string.resume);

//                    if(disability1.equals("")) {
//                        pwdDisability1.setVisibility(View.GONE);
//                    }else{
//                        pwdDisability1.setText(disability1);
//                    }
//                    if(typeOfDisability2.equals(d2)) {
//                        displayTypeOfDisability2.setText(typeOfDisability2);
//                    }else{
//                        displayTypeOfDisability2.setVisibility(View.GONE);
//                    }
//                    if(typeOfDisability3.equals(d3)) {
//                        displayTypeOfDisability3.setText(typeOfDisability3);
//                    }else{
//                        displayTypeOfDisability3.setVisibility(View.GONE);
//                    }
//                    if(typeOfDisabilityMore.equals(d4)) {
//                        displayTypeOfDisabilityMore.setText(typeOfDisabilityMore);
//                    }else{
//                        displayTypeOfDisabilityMore.setVisibility(View.GONE);
//                    }



                    try {
                        Picasso.get().load(image).placeholder(R.drawable.emp_placeholder)
                                .centerCrop().fit().into(pwdDetailsImg);
                        Picasso.get().load(pwdId).placeholder(R.drawable.equalsplaceholder)
                                .centerCrop().fit().into(viewPWDId);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.emp_placeholder).centerCrop().fit().into(pwdDetailsImg);
                        Picasso.get().load(R.drawable.equalsplaceholder).centerCrop().fit().into(viewPWDId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showLink() {
        Intent Resume = new Intent(android.content.Intent.ACTION_VIEW);
        Resume.setData(Uri.parse(resume));
        startActivity(Resume);
    }

    private void showDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        mBuilder.setTitle("Update Status: ");
        final Spinner mSpinner = mView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PWDDetailsActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.PWDStatus));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose Status")){

                    pd.show();
                    String selectStatus = mSpinner.getSelectedItem().toString();
                    HashMap<String, Object > choice = new HashMap<>();
                    choice.put("typeStatus", selectStatus);

                    pwdDbRef.child(uid).updateChildren(choice)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //updated dismiss progress
                                    pd.dismiss();
                                    Snackbar.make(pwdDetail, "Status has been updated successfully", Snackbar.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Snackbar.make(pwdDetail, ""+e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            });
                    dialog.dismiss();
                }
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}