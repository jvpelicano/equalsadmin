package com.philcode.equalsadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.philcode.equalsadmin.R;
import com.rosemaryapp.amazingspinner.AmazingSpinner;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateEmployerCompanyInfo extends AppCompatActivity {

    private String email;
    private String uid;
    private TextView empCompanyNameEdit, empCompanyAddressEdit, empCompanyOverviewEdit;
    private Spinner empCityEdit;
    private MaterialButton empInfoBtn;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference employerDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_employer_company_info);

        getSupportActionBar().setTitle("Update Employer Company Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        empCompanyNameEdit = findViewById(R.id.emp_company_name_edit);
        empCompanyAddressEdit = findViewById(R.id.emp_company_address_edit);
        empCompanyOverviewEdit = findViewById(R.id.emp_company_overview_edit);
        empCityEdit = findViewById(R.id.emp_company_city_edit);
        empInfoBtn = findViewById(R.id.emp_company_btn_edit);

        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        employerDbRef = firebaseDatabase.getReference("Employers");

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        Query empQuery = employerDbRef.orderByChild("email").equalTo(email);
        empQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    uid = ds.getKey();

                    //get data
                    String company = "" + ds.child("fullname").getValue();
                    String overView = "" + ds.child("companybg").getValue();
                    String coAddress = "" + ds.child("companyaddress").getValue();
//                    String coCity = "" + ds.child("companycity").getValue();

                    //set data
                    empCompanyNameEdit.setText(company);
                    empCompanyOverviewEdit.setText(overView);
                    empCompanyAddressEdit.setText(coAddress);
//                    empCityEdit.setText(coAdd1 + " " + coAdd2);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        empInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCompanyInfo();
            }
        });
    }

    private void updateCompanyInfo(){
        final String companyName = empCompanyNameEdit.getText().toString().trim();
        final String address = empCompanyAddressEdit.getText().toString().trim();
        final String overView = empCompanyOverviewEdit.getText().toString().trim();
        final String city = empCityEdit.getSelectedItem().toString().trim();


        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("fullname", companyName);
        hashMap2.put("companyaddress", address);
        hashMap2.put("companybg", overView);
        hashMap2.put("companycity", city);

        employerDbRef.child(uid).updateChildren(hashMap2);
        Toast.makeText(UpdateEmployerCompanyInfo.this, "Company Information has been published successfully", Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}