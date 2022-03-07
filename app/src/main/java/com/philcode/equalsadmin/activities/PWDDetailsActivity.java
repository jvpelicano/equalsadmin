package com.philcode.equalsadmin.activities;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.apis.PwdAPI;
import com.philcode.equalsadmin.apis.UserAPI;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PWDDetailsActivity extends AppCompatActivity {

    private CircleImageView pwdDetailsImg;
    private String email;
    RelativeLayout pwdDetail;
    private TextView viewPWDFname, viewPWDLname, pwdBadge, resumeLink;
    private TextInputEditText viewPWDEmail, viewPWDPhone, viewPWDAdd;
    private TextView pwdDisability1, editDoc, editQualificationInfo, editPersonalInfo, jobDetailsSkill1, pwdDetailCategory, pwdEduc, pwdWorkExp;
    private ImageView viewPWDId, pwdBadgeIcon;
    private Button updatePWDStatus, updatePwdIdBtn;
    private ProgressDialog pd;
    private String uid;
    private String resume;

    private FloatingActionButton fab_main, fab1_call, fab2_mail;
    private Animation fab_open, fab_close, fab_clock, fab_antiClock;
    TextView textview_call, textview_mail;
    Boolean isOpen = false;


    //firebase auth
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pwdDbRef;

    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private String storagePath = "PWD_Reg_Form/";

    //request codes
    private int Image_Request_Code = 7;

    //uri of picked image
    private Uri filePathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwddetails);

//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

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
        pwdDetailCategory = findViewById(R.id.pwd_details_category);
        pwdEduc = findViewById(R.id.pwd_details_educ);
        pwdWorkExp = findViewById(R.id.pwd_details_work_xp);
        resumeLink = findViewById(R.id.pwd_resume_link);
        pwdDisability1 = findViewById(R.id.pwd_details_disability1);
        jobDetailsSkill1 = findViewById(R.id.pwd_details_skill1);
        editPersonalInfo = findViewById(R.id.pwd_personal_info_edit);
        editQualificationInfo = findViewById(R.id.pwd_qualification_info_edit);
        editDoc = findViewById(R.id.pwd_docs_edit);
        updatePwdIdBtn = findViewById(R.id.pwd_id_img_btn_edit);

        updatePwdIdBtn.setVisibility(View.GONE);

        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        pwdDbRef = firebaseDatabase.getReference("PWD");
        storageReference = getInstance().getReference(); // firebase storage

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Updating Status..");

        //animation
        fab_main = findViewById(R.id.fab);
        fab1_call = findViewById(R.id.fab1);
        fab2_mail = findViewById(R.id.fab2);
        textview_call = findViewById(R.id.textview_call);
        textview_mail = findViewById(R.id.textview_sendEmail);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_antiClock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpen) {

                    textview_mail.setVisibility(View.INVISIBLE);
                    textview_call.setVisibility(View.INVISIBLE);
                    fab2_mail.startAnimation(fab_close);
                    fab1_call.startAnimation(fab_close);
                    fab_main.startAnimation(fab_antiClock);
                    fab2_mail.setClickable(false);
                    fab1_call.setClickable(false);
                    isOpen = false;
                } else {
                    textview_mail.setVisibility(View.VISIBLE);
                    textview_call.setVisibility(View.VISIBLE);
                    fab2_mail.startAnimation(fab_open);
                    fab1_call.startAnimation(fab_open);
                    fab_main.startAnimation(fab_clock);
                    fab2_mail.setClickable(true);
                    fab1_call.setClickable(true);
                    isOpen = true;
                }

            }
        });

        fab2_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setData(Uri.parse("email"));
                TextView email = findViewById(R.id.pwd_profile_email);
                String [] emailP = {email.getText().toString()};
                i.putExtra(Intent.EXTRA_EMAIL, emailP);
                i.putExtra(Intent.EXTRA_SUBJECT, "Invitation for Job Interview");
                i.putExtra(Intent.EXTRA_TEXT, "Put your details here.");
                i.setType("message/rfc822");
                Intent chooser = Intent.createChooser(i,"Choose Application");
                startActivity(chooser);

            }
        });

        fab1_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_CALL);
                TextView contact = findViewById(R.id.pwd_profile_phone);
                String contactNum = contact.getText().toString();
                i.setData(Uri.parse("tel:"+contactNum));
                if(ActivityCompat.checkSelfPermission(PWDDetailsActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    requestPermission();
                }else{
                    startActivity(i);
                }
            }
            private void requestPermission(){
                ActivityCompat.requestPermissions(PWDDetailsActivity.this, new String[] {Manifest.permission.CALL_PHONE},1);
            }

        });

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
                    String education = "" + ds.child("educationalAttainment").getValue();
                    String workExp = "" + ds.child("workExperience").getValue();
                    resume = "" + ds.child("resumeFile").getValue();
                    String category = "" + ds.child("skill").getValue();
                    ArrayList<String> jobSkillList = new ArrayList<>();
                    ArrayList<String> typeOfDisabilityList = new ArrayList<>();

                    for(int counter = 0; counter <= 9; counter++){
                        if(ds.hasChild("jobSkills" + counter) && !ds.child("jobSkills" + counter).getValue().toString().equals("")){
                            jobSkillList.add(ds.child("jobSkills" + counter).getValue(String.class));
                        }
                    }

                    for(int counter_a = 0; counter_a <= 2; counter_a++){
                        if(ds.hasChild("typeOfDisability" + counter_a) && !ds.child("typeOfDisability" + counter_a).getValue().toString().equals("")){
                            typeOfDisabilityList.add(ds.child("typeOfDisability" + counter_a).getValue(String.class));
                        }

                    }


                    if(status.equals("PWDApproved")){
                        pwdBadgeIcon.setVisibility(View.VISIBLE);
                        pwdBadge.setText("Verified Account");
                        pwdBadge.setTextColor(Color.parseColor("#008000"));
                        updatePWDStatus.setVisibility(View.VISIBLE);
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
                    pwdDetailCategory.setText(category);
                    pwdEduc.setText(education);
                    pwdWorkExp.setText(workExp);
                    resumeLink.setText(R.string.resume);

                    StringBuilder typeOfDisability_builder = new StringBuilder();
                    for(String typeOfDisabilityList1 : typeOfDisabilityList) {
                        typeOfDisability_builder.append(typeOfDisabilityList1 + "\n");
                    }
                    pwdDisability1.setText(typeOfDisability_builder.toString());

                    StringBuilder jobSkillList_builder = new StringBuilder();
                    for(String jobSkillList1 : jobSkillList){
                        jobSkillList_builder.append(jobSkillList1 + "\n");
                    }
                    jobDetailsSkill1.setText(jobSkillList_builder.toString());

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

        editPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(PWDDetailsActivity.this, UpdatePWDPersonalInfo.class);
                intent2.putExtra("email", email );
                startActivity(intent2);
            }
        });

        editQualificationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(PWDDetailsActivity.this, UpdatePWDQualificationInfo.class);
                intent3.putExtra("email", email );
                startActivity(intent3);
            }
        });

        editDoc.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

                updatePwdIdBtn.setVisibility(View.VISIBLE);
            }
        });

        updatePwdIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePwdId();
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
            case R.id.post_delete:
                androidx.appcompat.app.AlertDialog.Builder alert =  new androidx.appcompat.app.AlertDialog.Builder(PWDDetailsActivity.this);
                alert.setMessage("Are you sure to delete this PWD?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseDatabase.getReference().child("PWD").child(uid).removeValue();

                                deleteUser(uid);

                                Snackbar.make(pwdDetail, "PWD has been deleted" + uid, Snackbar.LENGTH_LONG).show();
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                androidx.appcompat.app.AlertDialog alertDialog = alert.create();
                alertDialog.setTitle("Delete PWD");
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updatePwdId(){
        if (filePathUri != null) {

            pd.show();

            final StorageReference ref = storageReference.child(storagePath + System.currentTimeMillis() + "." + GetFileExtension(filePathUri));
            ref.putFile(filePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            pd.dismiss();


                            final String pwdCardId = task.getResult().toString();

                            HashMap<String, Object> hashMap2 = new HashMap<>();

                            if (pwdCardId != null) {

                                hashMap2.put("pwdIdCardNum", pwdCardId);
                                pwdDbRef.child(uid).updateChildren(hashMap2);

                            } else {
                                Toast.makeText(PWDDetailsActivity.this, "Company Id has been updated successfully", Toast.LENGTH_LONG).show();
                                finish();

                            }

                            Toast.makeText(PWDDetailsActivity.this, "Company Id has been updated successfully", Toast.LENGTH_LONG).show();
                            finish();

                        }

                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(PWDDetailsActivity.this, "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    updatePwdIdBtn.setVisibility(View.GONE);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    pd.setMessage("Loading " + (int) progress + "%");
                    pd.setCancelable(false);
                }
            });
        }
        else{
            updatePwdIdBtn.setVisibility(View.GONE);
            Toast.makeText(PWDDetailsActivity.this, "Walang laman", Toast.LENGTH_SHORT).show();
            return;

        }

    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathUri);
                viewPWDId.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        menu.findItem(R.id.post_delete);
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void deleteUser(String id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PwdAPI pwdAPI = retrofit.create(PwdAPI.class);

        Call<Void> call = pwdAPI.deleteCandidate(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Snackbar.make(pwdDetail, "User has been deleted", Snackbar.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.getMessage();
            }
        });
    }
}