package com.philcode.equalsadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.models.Announcement;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {



    String currentDate = "" + System.currentTimeMillis();


    private View viewEndAnimation;
    private ImageView viewAnimation;
    RelativeLayout addPostLayout;
    
    private ImageView addPostImg;
    private TextInputEditText addPostTitle, addPostDesc;
    private Button addPostButton;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private StorageReference storageReference;

    private String storagePath = "Posts/";

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    //arrays of permissions to be requested
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //uri of picked image
    Uri image_uri;

    //for checking profile
    String postImg = "image";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

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

        //init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //clicking the imageview for profile picture
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddImgDialog();
            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("home_content");

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
        });

    }

    private boolean checkStoragePermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }


    private void requestStoragePermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(AddPostActivity.this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(AddPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_DENIED);
        return result && result1;
    }


    private void requestCameraPermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(AddPostActivity.this,cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showAddImgDialog() {
        String editProfilePic [] = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle("Pick Image From");
        //set item to dialog
        builder.setItems(editProfilePic, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0){
                    //pick image from Camera
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{

                    }
                }
                else{
                    //pick image from Gallery
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }
                }


            }
        });

        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       /*This method called when user press Allow deny from permission request dialog
            here we will handle permission cases (allowed & denied) */
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                //picking from camera, first check if camera and storage permissions allowed or not
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //permissions enabled
                        pickFromCamera();
                    }
                    else{
                        //permissions denied
                        Toast.makeText(this, "Please enable camera & storage permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                //picking from gallery, first check if gallery and storage permissions allowed or not
                if (grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //permissions enabled
                        pickFromGallery();
                    }
                    else{
                        //permissions denied
                        Toast.makeText(this, "Please enable storage permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        //This method will be called after picking image from Camera or Gallery
//        if (resultCode == RESULT_OK){
//
//            if (requestCode == IMAGE_PICK_GALLERY_CODE){
//                //image is picked from gallery , get uri of image
//                image_uri = data.getData();
//                uploadPostPhoto(image_uri);
//            }
//            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
//                //image is picked from camera , get uri of image
//                uploadPostPhoto(image_uri);
//            }
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }

//    private void uploadPostPhoto(final Uri uri) {
//
//        //path and name of image to be stored in firebase storage
//        String filePathAndName = storagePath + "" + postImg + "_" + user.getUid();
//
//        StorageReference storageReference2nd = storageReference.child(filePathAndName);
//        storageReference2nd.putFile(uri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        //image is uploaded to storage, now get it's url and store in user's database
//                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                        while (!uriTask.isSuccessful());
//                        Uri downloadUri = uriTask.getResult();
//
//                        //check if image is uploaded or not and uri is received
//                        if (uriTask.isSuccessful()){
//                            //image uploaded
//                            //add / update url in user's database
//                            HashMap<String, Object> results = new HashMap<>();
//                            results.put(postImg, downloadUri.toString());
//
//                            databaseReference.child(user.getUid()).updateChildren(results)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            //url in database of user is added successfully
//                                            //dismiss progress bar
////                                            pd.dismiss();
//                                            Toast.makeText(AddPostActivity.this, "Image Updated..", Toast.LENGTH_SHORT).show();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            //error adding url in database of user
//                                            //dismiss progress bar
////                                            pd.dismiss();
//                                            Toast.makeText(AddPostActivity.this, "Error Updating Image..", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }
//                        else{
//                            //error
////                            pd.dismiss();
//                            Toast.makeText(AddPostActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //there were some error(s) get and show error message, dismiss progress dialog
////                        pd.dismiss();
//                        Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//    }

    private void pickFromGallery() {
        //Intent of picking image from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");

        //put image uri
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
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