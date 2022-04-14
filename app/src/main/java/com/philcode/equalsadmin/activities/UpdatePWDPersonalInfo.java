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
import android.view.MenuItem;
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

public class UpdatePWDPersonalInfo extends AppCompatActivity {

    private String email;
    private String uid;
    private CircleImageView pwdEditImg;
    private TextView pwdFnameEdit, pwdLnameEdit, pwdPhoneEdit;
    private MaterialButton pwdInfoBtn;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pwdDbRef;
    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private String storagePath = "PWD_pfp/";

    private ProgressDialog progressDialog ;

    //request codes
    private int Image_Request_Code = 7;

    //uri of picked image
    private Uri filePathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwdpersonal_info);

        getSupportActionBar().setTitle("Update PWD Personal Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        pwdEditImg = findViewById(R.id.pwd_img_edit);
        pwdFnameEdit = findViewById(R.id.pwd_firstname_edit);
        pwdLnameEdit = findViewById(R.id.pwd_lastname_edit);
        pwdPhoneEdit = findViewById(R.id.pwd_phone_edit);
        pwdInfoBtn = findViewById(R.id.pwd_info_btn_edit);

        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        pwdDbRef = firebaseDatabase.getReference("PWD");
        storageReference = getInstance().getReference(); // firebase storage

        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Personal Information..");

        Query empQuery = pwdDbRef.orderByChild("email").equalTo(email);
        empQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    uid = ds.getKey();

                    //get data
                    String lName = "" + ds.child("lastName").getValue();
                    String fName = "" + ds.child("firstName").getValue();
                    String image = "" + ds.child("pwdProfilePic").getValue();
                    String phone = "" + ds.child("contact").getValue();

                    //set data
                    pwdFnameEdit.setText(fName);
                    pwdLnameEdit.setText(lName);
                    pwdPhoneEdit.setText(phone);

                    try {
                        Picasso.get().load(image).placeholder(R.drawable.emp_placeholder)
                                .centerCrop().fit().into(pwdEditImg);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.emp_placeholder).centerCrop().fit().into(pwdEditImg);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        pwdInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedInfo();
            }
        });

        pwdEditImg.setOnClickListener(new View.OnClickListener(){

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
                            final String firstName = pwdFnameEdit.getText().toString().trim();
                            final String lastName = pwdLnameEdit.getText().toString().trim();
                            final String phone = pwdPhoneEdit.getText().toString().trim();


                            HashMap<String, Object> hashMap2 = new HashMap<>();
                            hashMap2.put("firstName", firstName);
                            hashMap2.put("lastName", lastName);
                            hashMap2.put("contact", phone);

                            if (imgProfile != null) {

                                hashMap2.put("pwdProfilePic", imgProfile);
                                pwdDbRef.child(uid).updateChildren(hashMap2);

                            } else {
                                pwdDbRef.child(uid).updateChildren(hashMap2);
                                Toast.makeText(UpdatePWDPersonalInfo.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
                                finish();

                            }

                            Toast.makeText(UpdatePWDPersonalInfo.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
                            finish();

                        }

                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdatePWDPersonalInfo.this, "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        final String firstName = pwdFnameEdit.getText().toString().trim();
        final String lastName = pwdLnameEdit.getText().toString().trim();
        final String phone = pwdPhoneEdit.getText().toString().trim();


        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("firstName", firstName);
        hashMap2.put("lastName", lastName);
        hashMap2.put("contact", phone);

        pwdDbRef.child(uid).updateChildren(hashMap2);
        Toast.makeText(UpdatePWDPersonalInfo.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
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
               pwdEditImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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