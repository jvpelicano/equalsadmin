package com.philcode.equalsadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

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

public class PostDetailsActivity extends AppCompatActivity {

    RelativeLayout postDetail;
    private TextInputEditText viewPostTitle, viewPostDesc;

    private String postId;

    //firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference postDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getSupportActionBar().setTitle("Post Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        Intent intent = getIntent();
        postId = intent.getStringExtra("postUid");

        viewPostTitle = findViewById(R.id.post_title_details);
        viewPostDesc = findViewById(R.id.post_desc_details);

        //firebase instance
        firebaseDatabase = FirebaseDatabase.getInstance();
        postDbRef = firebaseDatabase.getReference("home_content");


        Query postQuery = postDbRef.orderByChild("postUid").equalTo(postId);
        postQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String title = "" + ds.child("postContentTitle").getValue();
                    String description = "" + ds.child("postDescription").getValue();

                    //set data
                    viewPostTitle.setText(title);
                    viewPostDesc.setText(description);

//
//                    try {
//                        Picasso.get().load(image).placeholder(R.drawable.emp_placeholder)
//                                .centerCrop().fit().into(empDetailsImg);
//                    }
//                    catch (Exception e){
//                        Picasso.get().load(R.drawable.emp_placeholder).centerCrop().fit().into(empDetailsImg);
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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