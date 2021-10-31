package com.philcode.equalsadmin.activities;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

public class PostDetailsActivity extends AppCompatActivity {

    RelativeLayout postDetail;
    private TextInputEditText viewPostTitle, viewPostDesc;
    private ImageView viewPostImg;
    private Button updatePost;

    private String postId;
    private ProgressDialog progressDialog ;
    //firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference postDbRef;

    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private String storagePath = "Posts/";

    //permissions constants
    int PICK_IMAGE_REQUEST = 7;

    //arrays of permissions to be requested
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //uri of picked image
    private Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        getSupportActionBar().setTitle("Post Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        Intent intent = getIntent();
        postId = intent.getStringExtra("postUid");

        viewPostTitle = findViewById(R.id.post_title_details);
        viewPostDesc = findViewById(R.id.post_desc_details);
        viewPostImg = findViewById(R.id.post_details_img);
        updatePost = findViewById(R.id.post_btn_edit);
        postDetail = findViewById(R.id.post_details_layout);

        updatePost.setVisibility(View.INVISIBLE);
        viewPostTitle.setEnabled(false);
        viewPostDesc.setEnabled(false);
        viewPostImg.setEnabled(false);

        //firebase instance
        firebaseDatabase = FirebaseDatabase.getInstance();
        postDbRef = firebaseDatabase.getReference("home_content");
        storageReference = getInstance().getReference(); // firebase storage

        //init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Image..");

        viewPostImg.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                chooseImgGallery();
            }
        });

        updatePost.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                saveEditedPost();
            }
        });


        Query postQuery = postDbRef.orderByChild("postUid").equalTo(postId);
        postQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String title = "" + ds.child("postContentTitle").getValue();
                    String description = "" + ds.child("postDescription").getValue();
                    String image = "" + ds.child("postImage").getValue();

                    //set data
                    viewPostTitle.setText(title);
                    viewPostDesc.setText(description);


                    try {
                        Picasso.get().load(image).placeholder(R.drawable.camera)
                                .centerCrop().fit().into(viewPostImg);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.camera).centerCrop().fit().into(viewPostImg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void saveEditedPost() {

        final StorageReference ref = storageReference.child(storagePath + System.currentTimeMillis() + "." + GetFileExtension(filePath));
        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        progressDialog.dismiss();


                        final String imgPost = task.getResult().toString();
                        final String title = viewPostTitle.getText().toString().trim();
                        final String desc = viewPostDesc.getText().toString().trim();


                        HashMap<String, Object> hashMap2 = new HashMap<>();
                        hashMap2.put("postContentTitle", title);
                        hashMap2.put("postDescription", desc);
                        if(imgPost != null){

                            hashMap2.put("postImage", imgPost);
                            postDbRef.child(postId).updateChildren(hashMap2);

                        }
                        else{
                            hashMap2.put("postImage", imgPost);
                            postDbRef.child(postId).updateChildren(hashMap2);
                            Toast.makeText(PostDetailsActivity.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
                            finish();

                        }

                        Toast.makeText(PostDetailsActivity.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
                        finish();

                    }

                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PostDetailsActivity.this, "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void chooseImgGallery() {
        // Creating intent.
        Intent intent = new Intent();

        // Setting intent type as image to select image from phone storage.
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Please Select Image"), PICK_IMAGE_REQUEST);

        final StorageReference ref = storageReference.child(storagePath + System.currentTimeMillis() + "." + GetFileExtension(filePath));
        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        progressDialog.dismiss();


                        final String imgPost = task.getResult().toString();
//                        final String title = viewPostTitle.getText().toString().trim();
//                        final String desc = viewPostDesc.getText().toString().trim();


                        HashMap<String, Object> hashMap2 = new HashMap<>();
//                        hashMap2.put("postContentTitle", title);
//                        hashMap2.put("postDescription", desc);
//                        if(imgPost != null){

//                            hashMap2.put("postImage", imgPost);
//                            postDbRef.child(postId).updateChildren(hashMap2);

//                        }
//                        else{
                            hashMap2.put("postImage", imgPost);
                            postDbRef.child(postId).updateChildren(hashMap2);
                            Toast.makeText(PostDetailsActivity.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
//                            finish();

//                        }
//
//                        Toast.makeText(PostDetailsActivity.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
//                        finish();

                    }

                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PostDetailsActivity.this, "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data !=null && data.getData() != null){
            filePath = data.getData();
            try{
                Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                viewPostImg.setImageBitmap(bitmap1);
            }
            catch (IOException e){
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
            case R.id.post_edit:
                updatePost.setVisibility(View.VISIBLE);
                viewPostTitle.setEnabled(true);
                viewPostDesc.setEnabled(true);
                viewPostImg.setEnabled(true);
                getSupportActionBar().setTitle("Edit Details");
                Snackbar.make(postDetail, "You can now update the post, Tap to edit", Snackbar.LENGTH_LONG).show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        menu.findItem(R.id.post_edit);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}