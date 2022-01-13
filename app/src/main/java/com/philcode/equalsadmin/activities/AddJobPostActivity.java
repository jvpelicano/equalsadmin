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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private Spinner spinner_postDuration, spinner_skillCategory;
    private ArrayAdapter<String> spinner_skillCategory_adapter;

    //Arrays
    private Integer[] secondary_skills_checkboxIDs;
    private CheckBox[] checkBoxes;
    private HashMap<String, String> checked_secondary_skills;
    private ArrayList<String> skillCategory_contentList;

    //request codes
    private int Image_Request_Code = 7;
    //database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobPostNode, categoryNode;
    private StorageReference ref, jobOffersNode;
    private String storage_path = "Job_Offers/";

    private Uri FilePathUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job_post);

        imageView = findViewById(R.id.add_job_post_img);
        btn_post = findViewById(R.id.btn_job_post);
        spinner_skillCategory = findViewById(R.id.spinner_skillCategory);
        skillCategory_contentList = new ArrayList<>();
        spinner_skillCategory_adapter = new ArrayAdapter<String>(AddJobPostActivity.this, android.R.layout.simple_spinner_dropdown_item, skillCategory_contentList);


        progressDialog = new ProgressDialog(AddJobPostActivity.this);


        //initialize database
        firebaseDatabase = FirebaseDatabase.getInstance();

        jobPostNode = firebaseDatabase.getReference().child("Job_Offers");
        categoryNode = firebaseDatabase.getReference().child("Category");
        jobOffersNode = FirebaseStorage.getInstance().getReference();

        spinner_skillCategory.setAdapter(spinner_skillCategory_adapter);
        setSpinner();


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

    private void setSpinner(){
        categoryNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    skillCategory_contentList.add(ds.child("skill").getValue().toString());
                }
                spinner_skillCategory_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //this method is optimized version for checking if checkBox is checked.
    private void selectedSecondarySkills(){
        secondary_skills_checkboxIDs = new Integer[]{R.id.typeOfSkills1, R.id.typeOfSkills2,
                R.id.typeOfSkills3, R.id.typeOfSkills4, R.id.typeOfSkills5, R.id.typeOfSkills6, R.id.typeOfSkills7
        ,R.id.typeOfSkills8, R.id.typeOfSkills9, R.id.typeOfSkills10};

        checkBoxes = new CheckBox[secondary_skills_checkboxIDs.length];
        checked_secondary_skills = new HashMap<>();

        for(int i = 0; i < secondary_skills_checkboxIDs.length; i++){
            checkBoxes[i] = (CheckBox) findViewById(secondary_skills_checkboxIDs[i]);
            if(checkBoxes[i].isChecked()){
                checked_secondary_skills.put("typeOfSkills" + i, checkBoxes[i].getText().toString().trim());
            }
        }
    }

    //this method is for uploading data once the user tapped the post button.
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

    // this method is to get the selected image file Extension from File Path URI.
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