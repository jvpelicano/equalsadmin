package com.philcode.equalsadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.adapters.ResumeListAdapter;
import com.philcode.equalsadmin.listeners.ViewResumeListItemClickListener;
import com.philcode.equalsadmin.models.Resume;

import java.util.ArrayList;
import java.util.Collections;

public class ViewResumeListActivity extends AppCompatActivity {

    Context context;
    DatabaseReference dbRef;
    ResumeListAdapter adapter;


    private DrawerLayout pDrawerLayout;
    private ActionBarDrawerToggle pToggle;

    private FirebaseAuth firebaseAuth;


    private Toolbar pToolbar;
    private NavigationView eNavigation;

    TextView fullname, pwd_email;
    ImageView imgProfile = null;
    String dp1;
    String companyName;
    private TextView profile_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_resume_list);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentFirebaseUser.getUid();
        final String email = currentFirebaseUser.getEmail().toString();

        final ArrayList<Resume> list = new ArrayList<>();
        final RecyclerView recyclerView;
        dbRef = FirebaseDatabase.getInstance().getReference("Job_Offers");


        String postJobID = getIntent().getStringExtra("POST_ID"); //putExtra from previous activity
        recyclerView = findViewById(R.id.pwd_sentResume_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(
                new ViewResumeListItemClickListener(ViewResumeListActivity.this, recyclerView, new ViewResumeListItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(ViewResumeListActivity.this);
                        alert.setMessage("Let them know you are interested to reach them.").setCancelable(false)
                                .setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(Intent.ACTION_SEND);
                                        i.setData(Uri.parse("email"));
                                        TextView email = findViewById(R.id.editEmail);
                                        String[] emailP = {email.getText().toString()};
                                        i.putExtra(Intent.EXTRA_EMAIL, emailP);
                                        i.putExtra(Intent.EXTRA_SUBJECT, "Invitation for Interview");
                                        i.putExtra(Intent.EXTRA_TEXT, "Put your details here.");
                                        i.setType("message/rfc822");
                                        Intent chooser = Intent.createChooser(i, "Launch Email");
                                        startActivity(chooser);

                                    }
                                }).setNegativeButton("Call", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Intent.ACTION_CALL);
                                TextView contact = findViewById(R.id.editContact);
                                String contactNum = contact.getText().toString();
                                i.setData(Uri.parse("tel:" + contactNum));
                                if (ActivityCompat.checkSelfPermission(ViewResumeListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermission();
                                } else {
                                    startActivity(i);
                                }
                            }

                            private void requestPermission() {
                                ActivityCompat.requestPermissions(ViewResumeListActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                            }

                        }).setNeutralButton("View Resume", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ViewResumeListActivity.this, ViewResumePDFActivity.class);
                                intent.putExtra("PDF_Uri", list.get(position).getResumeFile());
                                startActivity(intent);
                                String resumeKey = list.get(position).getUserID();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Job_Offers/" + postJobID).child("Resume").child(resumeKey);
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild("oldResumeFile")){
                                            ref.child("oldResumeFile").removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                finish();
                            }
                        });
                        AlertDialog alertDialog = alert.create();
                        alertDialog.setTitle("Choose Action");
                        alertDialog.show();
                        alertDialog.setCancelable(true);


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Intent i = new Intent(ViewResumeListActivity.this, PWDDetailsActivity.class);
                        String email = list.get(position).getEmail();
                        i.putExtra("email", email);
                        startActivity(i);
                    }
                })

        );

        dbRef.child(postJobID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Resumes > Resume ; Deleted for loop
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Job_Offers/" + postJobID).child("Resume");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                            Resume p = dataSnapshot2.getValue(Resume.class);
                            list.add(p);
                        }
                        Collections.reverse(list);
                        adapter = new ResumeListAdapter(ViewResumeListActivity.this, list, postJobID);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ViewResumeListActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}