package com.philcode.equalsadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class JobDetailsActivity extends AppCompatActivity {

    ImageView jobDetailsImg;
    RelativeLayout jobDetail;
    private TextInputEditText jobDetailsCompany, jobDetailsAddress, jobDetailsDescription;
    private TextView jobDetailsTitle, jobDetailsPermission, jobDetailsCategory, jobDetailsPrimary1, jobDetailsPrimary2,jobDetailsPrimary3,
            jobDetailsPrimary4,jobDetailsPrimary5,jobDetailsPrimary6,jobDetailsPrimary7, jobDetailsPrimary8, jobDetailsPrimary9,
            jobDetailsPrimary10, jobDetailsPrimaryOther, jobDetailsSkill1, jobDetailsSkill2, jobDetailsSkill3, jobDetailsSkill4,
            jobDetailsSkill5, jobDetailsSkill6, jobDetailsSkill7, jobDetailsSkill8, jobDetailsSkill9, jobDetailsSkill10, jobDetailsEduc,
            jobDetailsWorkxp, jobDetailsDisability1, jobDetailsDisability2, jobDetailsDisability3, jobDetailsDisability4, jobDetailsExpDate;
    private Button updateJobStatus;
    private String postJobId;

    private ProgressDialog pd;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getSupportActionBar().setTitle("Job Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        Intent intent = getIntent();
        postJobId = intent.getStringExtra("postJobId");

        jobDetailsImg = findViewById(R.id.job_details_img);
        jobDetailsTitle = findViewById(R.id.job_details_title);
        jobDetailsCompany = findViewById(R.id.job_details_company);
        jobDetailsAddress = findViewById(R.id.job_details_address);
        jobDetailsDescription = findViewById(R.id.job_details_description);
        jobDetailsPermission = findViewById(R.id.job_details_permission);
        jobDetailsCategory = findViewById(R.id.job_details_category);
        jobDetailsPrimary1 = findViewById(R.id.job_details_primary_skill1);
        jobDetailsPrimary2 = findViewById(R.id.job_details_primary_skill2);
        jobDetailsPrimary3 = findViewById(R.id.job_details_primary_skill3);
        jobDetailsPrimary4 = findViewById(R.id.job_details_primary_skill4);
        jobDetailsPrimary5 = findViewById(R.id.job_details_primary_skill5);
        jobDetailsPrimary6 = findViewById(R.id.job_details_primary_skill6);
        jobDetailsPrimary7 = findViewById(R.id.job_details_primary_skill7);
        jobDetailsPrimary8 = findViewById(R.id.job_details_primary_skill8);
        jobDetailsPrimary9 = findViewById(R.id.job_details_primary_skill9);
        jobDetailsPrimary10 = findViewById(R.id.job_details_primary_skill10);
        jobDetailsPrimaryOther = findViewById(R.id.job_details_primary_skill_other);
        jobDetailsSkill1 = findViewById(R.id.job_details_skill1);
        jobDetailsSkill2 = findViewById(R.id.job_details_skill2);
        jobDetailsSkill3 = findViewById(R.id.job_details_skill3);
        jobDetailsSkill4 = findViewById(R.id.job_details_skill4);
        jobDetailsSkill5 = findViewById(R.id.job_details_skill5);
        jobDetailsSkill6 = findViewById(R.id.job_details_skill6);
        jobDetailsSkill7 = findViewById(R.id.job_details_skill7);
        jobDetailsSkill8 = findViewById(R.id.job_details_skill8);
        jobDetailsSkill9 = findViewById(R.id.job_details_skill9);
        jobDetailsSkill10 = findViewById(R.id.job_details_skill10);
        jobDetailsEduc = findViewById(R.id.job_details_educ);
        jobDetailsWorkxp = findViewById(R.id.job_details_work_xp);
        jobDetailsDisability1 = findViewById(R.id.job_details_disability1);
        jobDetailsDisability2 = findViewById(R.id.job_details_disability2);
        jobDetailsDisability3 = findViewById(R.id.job_details_disability3);
        jobDetailsDisability4 = findViewById(R.id.job_details_disability4);
        jobDetailsExpDate = findViewById(R.id.job_details_exp_date);
        updateJobStatus = findViewById(R.id.job_status_btn);
        jobDetail = findViewById(R.id.job_details_layout);




        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Updating Status..");

        //Click Update Button
        updateJobStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        jobsDbRef = firebaseDatabase.getReference("Job_Offers");

        Query jobQuery = jobsDbRef.orderByChild("postJobId").equalTo(postJobId);
        jobQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    //get data
                    String image = "" + ds.child("imageURL").getValue();
                    String title = "" + ds.child("postTitle").getValue();
                    String company = "" + ds.child("companyName").getValue();
                    String description = "" + ds.child("postDescription").getValue();
                    String coAdd1 = "" + ds.child("postLocation").getValue();
                    String coAdd2 = "" + ds.child("city").getValue();
                    String educ = "" + ds.child("educationalAttainment").getValue();
                    String experience = "" + ds.child("workExperience").getValue();
                    String expDate = "" + ds.child("expDate").getValue();
                    String companyId = "" + ds.child("empValidID").getValue();


                    String status = "" + ds.child("typeStatus").getValue();
//
//                    if(status.equals("EMPApproved")){
//                        empBadgeIcon.setVisibility(View.VISIBLE);
//                        empBadge.setText("Verified Account");
//                        empBadge.setTextColor(Color.parseColor("#008000"));
//                        updateEmpStatus.setVisibility(View.GONE);
//                    }
//                    else if (status.equals("EMPPending")){
//                        empBadgeIcon.setVisibility(View.GONE);
//                        empBadge.setText("For Verification");
//                        empBadge.setTextColor(Color.parseColor("#FF1414"));
//                        updateEmpStatus.setVisibility(View.VISIBLE);
//                    }
//                    else{
//                        empBadgeIcon.setVisibility(View.GONE);
//                        empBadge.setText("Cancelled");
//                        empBadge.setTextColor(Color.parseColor("#808080"));
//                        updateEmpStatus.setVisibility(View.VISIBLE);
//                    }
                    //set data
                    jobDetailsTitle.setText(title);
                    jobDetailsCompany.setText(company);
                    jobDetailsDescription.setText(description);
                    jobDetailsAddress.setText(coAdd1 + " " + coAdd2);
                    jobDetailsEduc.setText(educ);
                    jobDetailsExpDate.setText(expDate);
                    jobDetailsWorkxp.setText(experience);


                    try {
                        Picasso.get().load(image).placeholder(R.drawable.equalsplaceholder)
                                .centerCrop().fit().into(jobDetailsImg);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.equalsplaceholder).centerCrop().fit().into(jobDetailsImg);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void showDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        mBuilder.setTitle("Update Permission: ");
        final Spinner mSpinner = mView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(JobDetailsActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.JobStatus));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose Status")){

                    pd.show();
                    String selectStatus = mSpinner.getSelectedItem().toString();
                    HashMap<String, Object > choice = new HashMap<>();
                    choice.put("permission", selectStatus);

                    jobsDbRef.child(postJobId).updateChildren(choice)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //updated dismiss progress
                                    pd.dismiss();
                                    Snackbar.make(jobDetail, "Permission has been updated successfully", Snackbar.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Snackbar.make(jobDetail, ""+e.getMessage(), Snackbar.LENGTH_LONG).show();
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