package com.philcode.equalsadmin.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equalsadmin.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddJobPostActivity extends AppCompatActivity {
    //layout
    private ImageView imageView;
    private MaterialButton btn_post;
    private ProgressDialog progressDialog;


    //Arrays
    private Integer[] secondary_skills_checkboxIDs;
    private CheckBox[] checkBoxes;
    private HashMap<String, String> checked_secondary_skills;
    //request codes
    private int Image_Request_Code = 7;
    //database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobPostNode;
    private StorageReference ref, jobOffersNode;
    private FirebaseStorage firebaseStorage;
    private String storage_path = "Job_Offers/";

    private Uri FilePathUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job_post);

        imageView = findViewById(R.id.add_job_post_img);
        btn_post = findViewById(R.id.btn_job_post);
        progressDialog = new ProgressDialog(AddJobPostActivity.this);


        //initialize database
        firebaseDatabase = FirebaseDatabase.getInstance();

        jobPostNode = firebaseDatabase.getReference().child("Job_Offers");
        jobOffersNode = FirebaseStorage.getInstance().getReference();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSecondarySkills();
                uploadData();
            }
        });
    }

    private void selectedSecondarySkills(){
        secondary_skills_checkboxIDs = new Integer[]{R.id.typeOfSkills1, R.id.typeOfSkills2,
                R.id.typeOfSkills3, R.id.typeOfSkills4, R.id.typeOfSkills5, R.id.typeOfSkills6, R.id.typeOfSkills7
        ,R.id.typeOfSkills8, R.id.typeOfSkills9, R.id.typeOfSkills10};

        checkBoxes = new CheckBox[secondary_skills_checkboxIDs.length];
        checked_secondary_skills = new HashMap<>();

        for(int i = 0; i < secondary_skills_checkboxIDs.length; i++){
            checkBoxes[i] = (CheckBox) findViewById(secondary_skills_checkboxIDs[i]);
            if(checkBoxes[i].isChecked()){
                //not working, checkboxes returning null values instead of string.
                //checked_secondary_skills is a Hashmap<String, String>
                checked_secondary_skills.put("typeOfSkills" + i, checkBoxes[i].getText().toString().trim());

                //checked_secondary_skills.put("Pakistan", "Grape!");
            }
        }
    }

    private void uploadData() {
        progressDialog.setTitle("Posting...");
        progressDialog.show();
        ref = jobOffersNode.child(storage_path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
        ref.putFile(FilePathUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                progressDialog.dismiss();
                                final String pushKey = jobPostNode.push().getKey();

                                jobPostNode.child(pushKey).setValue(checked_secondary_skills);

                                startActivity(new Intent(AddJobPostActivity.this, MainActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AddJobPostActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                progressDialog.setCancelable(false);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}