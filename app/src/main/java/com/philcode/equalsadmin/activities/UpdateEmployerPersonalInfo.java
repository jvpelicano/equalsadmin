package com.philcode.equalsadmin.activities;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateEmployerPersonalInfo extends AppCompatActivity {

    private String email;
    private String uid;
    private CircleImageView empEditImg;
    private TextView empFnameEdit, empLnameEdit, empPhoneEdit;
    private MaterialButton empInfoBtn;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference employerDbRef;
    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private String storagePath = "Employer_Reg_Form/";

    private ProgressDialog progressDialog ;

    //request codes
    private int Image_Request_Code = 7;

    //uri of picked image
    private Uri filePathUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_employer_personal_info);

        getSupportActionBar().setTitle("Update Employer Personal Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        empEditImg = findViewById(R.id.emp_img_edit);
        empFnameEdit = findViewById(R.id.emp_firstname_edit);
        empLnameEdit = findViewById(R.id.emp_lastname_edit);
        empPhoneEdit = findViewById(R.id.emp_phone_edit);
        empInfoBtn = findViewById(R.id.emp_info_btn_edit);

        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        employerDbRef = firebaseDatabase.getReference("Employers");
        storageReference = getInstance().getReference(); // firebase storage

        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Personal Information..");


        Query empQuery = employerDbRef.orderByChild("email").equalTo(email);
        empQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    uid = ds.getKey();

                    //get data
                    String lName = "" + ds.child("lastname").getValue();
                    String fName = "" + ds.child("firstname").getValue();
                    String image = "" + ds.child("empProfilePic").getValue();
                    String phone = "" + ds.child("contact").getValue();

                    //set data
                    empFnameEdit.setText(fName);
                    empLnameEdit.setText(lName);
                    empPhoneEdit.setText(phone);

                    try {
                        Picasso.get().load(image).placeholder(R.drawable.emp_placeholder)
                                .centerCrop().fit().into(empEditImg);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.emp_placeholder).centerCrop().fit().into(empEditImg);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        empInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedInfo();
            }
        });

        empEditImg.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });
    }

    private void saveEditedInfo(){
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


                            final String imgProfile = task.getResult().toString();
                            final String firstName = empFnameEdit.getText().toString().trim();
                            final String lastName = empLnameEdit.getText().toString().trim();
                            final String phone = empPhoneEdit.getText().toString().trim();


                            HashMap<String, Object> hashMap2 = new HashMap<>();
                            hashMap2.put("firstname", firstName);
                            hashMap2.put("lastname", lastName);
                            hashMap2.put("contact", phone);

                            if (imgProfile != null) {

                                hashMap2.put("empProfilePic", imgProfile);
                                employerDbRef.child(uid).updateChildren(hashMap2);

                            } else {
                                employerDbRef.child(uid).updateChildren(hashMap2);
                                Toast.makeText(UpdateEmployerPersonalInfo.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
                                finish();

                            }

                            Toast.makeText(UpdateEmployerPersonalInfo.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
                            finish();

                        }

                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateEmployerPersonalInfo.this, "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            updateDataOnly();
        }

    }

    private void updateDataOnly(){

        final String firstName = empFnameEdit.getText().toString().trim();
        final String lastName = empLnameEdit.getText().toString().trim();
        final String phone = empPhoneEdit.getText().toString().trim();


        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("firstname", firstName);
        hashMap2.put("lastname", lastName);
        hashMap2.put("contact", phone);

            employerDbRef.child(uid).updateChildren(hashMap2);
            Toast.makeText(UpdateEmployerPersonalInfo.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
            finish();
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
                empEditImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}