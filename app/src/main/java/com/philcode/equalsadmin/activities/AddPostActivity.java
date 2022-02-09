package com.philcode.equalsadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.UploadTask;
import com.philcode.equalsadmin.R;

import java.io.IOException;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {



    String currentDate = "" + System.currentTimeMillis();


    private View viewEndAnimation;
    private ImageView viewAnimation;
    RelativeLayout addPostLayout;
    
    private ImageView addPostImg;
    private TextInputEditText addPostTitle, addPostDesc;
    private Button addPostButton;
    private ProgressDialog progressDialog;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private StorageReference storageReference, postNode;


    private String storagePath = "Posts/";

    //request codes
    private int Image_Request_Code = 7;

    //uri of picked image
    private Uri filePathUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        viewEndAnimation = findViewById(R.id.view_end_animation);
        viewAnimation = findViewById(R.id.view_animation);

        addPostLayout = findViewById(R.id.post_add_layout);
        addPostImg = findViewById(R.id.add_post_img);
        addPostTitle = findViewById(R.id.add_title_post);
        addPostDesc = findViewById(R.id.add_desc_post);
        addPostButton = findViewById(R.id.btn_post_publish);

        postNode = FirebaseStorage.getInstance().getReference();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("home_content");

        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting..");



        //clicking the imageview for profile picture
        addPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                uploadDataWithOrWithOutImage();
            }
        });

    }

    private void uploadDataOnly(){

        String postTitle =  addPostTitle.getText().toString().trim();
        String postDesc = addPostDesc.getText().toString().trim();
        String postImg = "";
        String dateFormat = currentDate;

        DatabaseReference blankRef = reference ;
        DatabaseReference db_ref = blankRef.push();
        String postUid = db_ref.getKey();


        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("postContentTitle", postTitle);
        hashMap.put("postDescription", postDesc);
        hashMap.put("formattedDate", dateFormat);
        hashMap.put("postUid", postUid);
        hashMap.put("postImage", postImg);

        db_ref.setValue( hashMap);


        Toast.makeText(AddPostActivity.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
        finish();
    }

    private void uploadDataWithOrWithOutImage(){
        if (filePathUri != null){

            progressDialog.show();

            storageReference = postNode.child(storagePath + System.currentTimeMillis() + "." + getFileExtension(filePathUri));
            storageReference.putFile(filePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    progressDialog.dismiss();

                                    DatabaseReference blankRef = reference ;
                                    DatabaseReference db_ref = blankRef.push();
                                    String postUid = db_ref.getKey();

                                    final String postImg = task.getResult().toString();
                                    String postTitle =  addPostTitle.getText().toString().trim();
                                    String postDesc = addPostDesc.getText().toString().trim();
                                    String dateFormat = currentDate;

                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    hashMap.put("postContentTitle", postTitle);
                                    hashMap.put("postDescription", postDesc);
                                    hashMap.put("formattedDate", dateFormat);
                                    hashMap.put("postUid", postUid);
                                    hashMap.put("postImage", postImg);

                                    reference.child(postUid).setValue( hashMap);


                                    Toast.makeText(AddPostActivity.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddPostActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        else{
            uploadDataOnly();
        }
    }

    // this method is to get the selected image file Extension from File Path URI.
    public String getFileExtension(Uri uri) {
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
                addPostImg.setImageBitmap(bitmap);
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