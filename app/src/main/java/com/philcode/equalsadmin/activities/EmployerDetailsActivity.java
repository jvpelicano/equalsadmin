package com.philcode.equalsadmin.activities;

import static android.content.ContentValues.TAG;

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
import android.util.Log;
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
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.philcode.equalsadmin.apis.EmployerAPI;
import com.philcode.equalsadmin.apis.UserAPI;
import com.philcode.equalsadmin.fragments.EmpFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmployerDetailsActivity extends AppCompatActivity {

    private CircleImageView empDetailsImg;
    private String email;
    RelativeLayout empDetail;
    private TextView viewEmpFname, viewEmpLname, empBadge, editDoc, editCompanyInfo, editPersonalInfo;
    private TextInputEditText viewEmpEmail, viewEmpPhone, viewEmpCompany, viewEmpAdd, viewCoOverview;
    private ImageView viewEmpId, empBadgeIcon;
    private Button updateEmpStatus;
    MaterialButton updateEmpIdBtn;
    private ProgressDialog pd, progressDialog;
    private String uid;

    private FloatingActionButton fab_main, fab1_call, fab2_mail;
    private Animation fab_open, fab_close, fab_clock, fab_antiClock;
    TextView textview_call, textview_mail;
    Boolean isOpen = false;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference employerDbRef;
    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private String storagePath = "Employee_IDs/";

    //request codes
    private int Image_Request_Code = 7;

    //uri of picked image
    private Uri filePathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_details);

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
        updateEmpIdBtn = findViewById(R.id.emp_id_img_btn_edit);
        editDoc = findViewById(R.id.emp_docs_edit);
        editCompanyInfo = findViewById(R.id.emp_company_info_edit);
        editPersonalInfo = findViewById(R.id.emp_personal_info_edit);

        updateEmpIdBtn.setVisibility(View.GONE);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Updating Status..");

        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Image..");


        //Click Update Button
        updateEmpStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        employerDbRef = firebaseDatabase.getReference("Employers");
        storageReference = getInstance().getReference(); // firebase storage

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
                TextView email = findViewById(R.id.emp_profile_email);
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
                TextView contact = findViewById(R.id.emp_profile_phone);
                String contactNum = contact.getText().toString();
                i.setData(Uri.parse("tel:"+contactNum));
                if(ActivityCompat.checkSelfPermission(EmployerDetailsActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    requestPermission();
                }else{
                    startActivity(i);
                }
            }
            private void requestPermission(){
                ActivityCompat.requestPermissions(EmployerDetailsActivity.this, new String[] {Manifest.permission.CALL_PHONE},1);
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

                updateEmpIdBtn.setVisibility(View.VISIBLE);
            }
        });

        updateEmpIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCompanyId();
            }
        });

        editPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(EmployerDetailsActivity.this, UpdateEmployerPersonalInfo.class);
                intent2.putExtra("email", email );
                startActivity(intent2);
            }
        });

        editCompanyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(EmployerDetailsActivity.this, UpdateEmployerCompanyInfo.class);
                intent3.putExtra("email", email );
                startActivity(intent3);
            }
        });

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
            case R.id.post_delete:
                androidx.appcompat.app.AlertDialog.Builder alert =  new androidx.appcompat.app.AlertDialog.Builder(EmployerDetailsActivity.this);
                alert.setMessage("Are you sure to delete this Employer?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                firebaseDatabase.getReference().child("Employers").child(uid).removeValue();
                                deleteUser(uid);

                                Snackbar.make(empDetail, "Employer has been deleted", Snackbar.LENGTH_LONG).show();
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                androidx.appcompat.app.AlertDialog alertDialog = alert.create();
                alertDialog.setTitle("Delete Employer");
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateCompanyId(){
        if (filePathUri != null) {

            progressDialog.show();

            final StorageReference ref = storageReference.child(storagePath + System.currentTimeMillis() + "." + GetFileExtension(filePathUri));
            ref.putFile(filePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            progressDialog.dismiss();


                            final String imgCoID = task.getResult().toString();

                            HashMap<String, Object> hashMap2 = new HashMap<>();

                            if (imgCoID != null) {

                                hashMap2.put("empValidID", imgCoID);
                                employerDbRef.child(uid).updateChildren(hashMap2);

                            } else {
                                Toast.makeText(EmployerDetailsActivity.this, "Company Id has been updated successfully", Toast.LENGTH_LONG).show();
                                finish();

                            }

                            Toast.makeText(EmployerDetailsActivity.this, "Company Id has been updated successfully", Toast.LENGTH_LONG).show();
                            finish();

                        }

                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(EmployerDetailsActivity.this, "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    updateEmpIdBtn.setVisibility(View.GONE);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Loading " + (int) progress + "%");
                    progressDialog.setCancelable(false);
                }
            });
        }
        else{
            updateEmpIdBtn.setVisibility(View.GONE);
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
                viewEmpId.setImageBitmap(bitmap);
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
        EmployerAPI employerAPI = retrofit.create(EmployerAPI.class);

        Call<Void> call = employerAPI.deleteEmployer(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Snackbar.make(empDetail, "User has been deleted", Snackbar.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.getMessage();
            }
        });
    }
}