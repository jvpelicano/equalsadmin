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

import java.util.ArrayList;
import java.util.HashMap;

public class JobDetailsActivity extends AppCompatActivity {

    ImageView jobDetailsImg;
    RelativeLayout jobDetail;
    private TextInputEditText jobDetailsCompany, jobDetailsAddress, jobDetailsDescription;
    private TextView jobDetailsTitle, jobDetailsPermission, jobDetailsEduc,
            jobDetailsWorkxp, jobDetailsDisability1, jobDetailsExpDate, jobDetailsCategory, jobDetailsSkill1;
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
        jobDetailsSkill1 = findViewById(R.id.job_details_skill1);
        jobDetailsEduc = findViewById(R.id.job_details_educ);
        jobDetailsWorkxp = findViewById(R.id.job_details_work_xp);
        jobDetailsDisability1 = findViewById(R.id.job_details_disability1);
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
                    String skill = "" + ds.child("skill").getValue();
                    ArrayList<String> jobSkillList = new ArrayList<>();
                    ArrayList<String> typeOfDisabilityList = new ArrayList<>();

                    for(int counter = 1; counter <= 10; counter++){
                        if(ds.hasChild("jobSkill" + counter) && !ds.child("jobSkill" + counter).getValue().toString().equals("")){
                            jobSkillList.add(ds.child("jobSkill" + counter).getValue(String.class));
                        }
                    }

                    for(int counter_a = 1; counter_a <= 3; counter_a++){
                        if(ds.hasChild("typeOfDisability" + counter_a) && !ds.child("typeOfDisability" + counter_a).getValue().toString().equals("")){
                            typeOfDisabilityList.add(ds.child("typeOfDisability" + counter_a).getValue(String.class));
                        }

                    }
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
                    jobDetailsCategory.setText(skill);

                    StringBuilder jobSkillList_builder = new StringBuilder();
                    for(String jobSkillList1 : jobSkillList){
                        jobSkillList_builder.append(jobSkillList1 + "\n");
                    }
                    jobDetailsSkill1.setText(jobSkillList_builder.toString());

                    StringBuilder typeOfDisability_builder = new StringBuilder();
                    for(String typeOfDisabilityList1 : typeOfDisabilityList) {
                        typeOfDisability_builder.append(typeOfDisabilityList1 + "\n");
                    }
                    jobDetailsDisability1.setText(typeOfDisability_builder.toString());

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