package com.philcode.equalsadmin.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.philcode.equalsadmin.fragments.EmpFragment;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployerDetailsActivity extends AppCompatActivity {

    private CircleImageView empDetailsImg;
    private String email;
    RelativeLayout empDetail;
    private TextView viewEmpFname, viewEmpLname, empBadge;
    private TextInputEditText viewEmpEmail, viewEmpPhone, viewEmpCompany, viewEmpAdd, viewCoOverview;
    private ImageView viewEmpId, empBadgeIcon;
    private Button updateEmpStatus;
    private ProgressDialog pd;
    private String uid;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference employerDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_details);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getSupportActionBar().setTitle("Employer Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        empDetailsImg = findViewById(R.id.emp_details_img);
        viewEmpFname = findViewById(R.id.emp_profile_fname);
        viewEmpLname = findViewById(R.id.emp_profile_lname);
        viewEmpEmail = findViewById(R.id.emp_profile_email);
        viewEmpPhone = findViewById(R.id.emp_profile_phone);
        viewEmpCompany = findViewById(R.id.emp_profile_company);
        viewEmpAdd = findViewById(R.id.emp_profile_address);
        viewCoOverview = findViewById(R.id.emp_profile_overview);
        viewEmpId = findViewById(R.id.emp_profile_id);
        empBadge = findViewById(R.id.emp_verified_acc);
        empBadgeIcon = findViewById(R.id.emp_verified_icon);
        updateEmpStatus = findViewById(R.id.update_status_btn);
        empDetail = findViewById(R.id.emp_details_layout);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Updating Status..");


        //Click Update Button
        updateEmpStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        employerDbRef = firebaseDatabase.getReference("Employers");

        Query empQuery = employerDbRef.orderByChild("email").equalTo(email);
        empQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    uid = ds.getKey();

                    //get data
                    String emailAdd = "" + ds.child("email").getValue();
                    String lName = "" + ds.child("lastname").getValue();
                    String fName = "" + ds.child("firstname").getValue();
                    String image = "" + ds.child("empProfilePic").getValue();
                    String phone = "" + ds.child("contact").getValue();
                    String company = "" + ds.child("fullname").getValue();
                    String overView = "" + ds.child("companybg").getValue();
                    String companyId = "" + ds.child("empValidID").getValue();
                    String coAdd1 = "" + ds.child("companyaddress").getValue();
                    String coAdd2 = "" + ds.child("companycity").getValue();

                    String status = "" + ds.child("typeStatus").getValue();
                    
                    if(status.equals("EMPApproved")){
                        empBadgeIcon.setVisibility(View.VISIBLE);
                        empBadge.setText("Verified Account");
                        empBadge.setTextColor(Color.parseColor("#008000"));
                        updateEmpStatus.setVisibility(View.GONE);
                    }
                    else if (status.equals("EMPPending")){
                        empBadgeIcon.setVisibility(View.GONE);
                        empBadge.setText("For Verification");
                        empBadge.setTextColor(Color.parseColor("#FF1414"));
                        updateEmpStatus.setVisibility(View.VISIBLE);
                    }
                    else{
                        empBadgeIcon.setVisibility(View.GONE);
                        empBadge.setText("Cancelled");
                        empBadge.setTextColor(Color.parseColor("#808080"));
                        updateEmpStatus.setVisibility(View.VISIBLE);
                    }
                     //set data
                      viewEmpFname.setText(fName);
                      viewEmpLname.setText(lName);
                      viewEmpEmail.setText(emailAdd);
                      viewEmpPhone.setText(phone);
                      viewEmpCompany.setText(company);
                      viewCoOverview.setText(overView);
                      viewEmpAdd.setText(coAdd1 + " " + coAdd2);


                    try {
                        Picasso.get().load(image).placeholder(R.drawable.emp_placeholder)
                                .centerCrop().fit().into(empDetailsImg);
                        Picasso.get().load(companyId).placeholder(R.drawable.equalsplaceholder)
                                .centerCrop().fit().into(viewEmpId);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.emp_placeholder).centerCrop().fit().into(empDetailsImg);
                        Picasso.get().load(R.drawable.equalsplaceholder).centerCrop().fit().into(viewEmpId);
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
        mBuilder.setTitle("Update Status: ");
        final Spinner mSpinner = mView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EmployerDetailsActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.EMPStatus));
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

                    employerDbRef.child(uid).updateChildren(choice)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //updated dismiss progress
                                    pd.dismiss();
                                    Snackbar.make(empDetail, "Status has been updated successfully", Snackbar.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Snackbar.make(empDetail, ""+e.getMessage(), Snackbar.LENGTH_LONG).show();
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