package com.philcode.equalsadmin.fragments;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.LoginActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class ProfileFragment extends Fragment {

    private ImageView adminImg;
    private TextInputEditText adminEmail;
    private TextView adminName;
    private Button logoutBtn, changePasswordBtn;
    private ProgressDialog pd;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private String storagePath = "AdminProfile/";

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
    String profilePhoto = "profile";

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Admin");
        storageReference = getInstance().getReference(); // firebase storage

        //init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        adminImg = root.findViewById(R.id.admin_img);
        adminName = root.findViewById(R.id.admin_name);
        adminEmail = root.findViewById(R.id.admin_email);
        logoutBtn = root.findViewById(R.id.btnLogout);
        changePasswordBtn = root.findViewById(R.id.btn_change_pw);

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        //init progress dialog
        pd = new ProgressDialog(getActivity());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        adminImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        Query query = databaseReference.orderByChild("uid").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String email = "" + ds.child("email").getValue();
                    String name = "" + ds.child("name").getValue();
                    String image = "" + ds.child("image").getValue();

                    //set data
                    adminEmail.setText(email);
                    adminName.setText(name);

                    try {
                        Picasso.get().load(image).placeholder(R.drawable.emp_placeholder)
                                .centerCrop().fit().into(adminImg);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.emp_placeholder).centerCrop().fit().into(adminImg);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    private void showChangePasswordDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_update_password, null);
        final EditText oldpassword = view.findViewById(R.id.admin_old_pw);
        final EditText newpassword = view.findViewById(R.id.admin_new_pw);
        Button updatePwordBtn = view.findViewById(R.id.btn_updatePw);
        Button cancelPwordBtn = view.findViewById(R.id.btn_cancelPw);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        updatePwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate data
                String oPassword = oldpassword.getText().toString().trim();
                String nPassword = newpassword.getText().toString().trim();
                if (oPassword.isEmpty()){
                    Toast.makeText(getContext(), "Enter current password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (nPassword.isEmpty()){
                    Toast.makeText(getContext(), "Enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (nPassword.length()<6){
                    Toast.makeText(getContext(), "Password length must atleast 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                updatePassword(oPassword, nPassword);
            }
        });
        cancelPwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void updatePassword(String oPassword, final String nPassword) {
        //get current user
        final FirebaseUser user = mAuth.getCurrentUser();
        //before changing password re-authenticate the user
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //successful authenticated begin update
                        user.updatePassword(nPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //password updated
                                        pd.dismiss();
                                        Toast.makeText(getContext(), "Password Updated", Toast.LENGTH_SHORT ).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //authentication failed
                        pd.dismiss();
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkCameraPermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_DENIED);
        return result && result1;
    }

    private boolean checkStoragePermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(),cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {

        String editProfilePic [] = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        Toast.makeText(getContext(), "Please enable camera & storage permissions", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Please enable storage permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //This method will be called after picking image from Camera or Gallery
        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery , get uri of image
                image_uri = data.getData();
                uploadProfilePhoto(image_uri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera , get uri of image
                uploadProfilePhoto(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(final Uri uri) {
        pd.show();
        /*Instead of creating separate function for Profile Picture and Cover Photo
         *To add check ill add a string variable ad assign it value "image" when user click
         * "Edit Profile Pic"
         * Here: image is the key in each user containing uri of user's profile picture
         * The parameter "image_uri" contains the uri of image picked either camera of gallery
         * We will use UID of the currently signed in user as name of the image so there will be only one image
         * profile for each user */

        //path and name of image to be stored in firebase storage
        String filePathAndName = storagePath + "" + profilePhoto + "_" + user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to storage, now get it's url and store in user's database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //check if image is uploaded or not and uri is received
                        if (uriTask.isSuccessful()){
                            //image uploaded
                            //add / update url in user's database
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(profilePhoto, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //url in database of user is added successfully
                                            //dismiss progress bar
                                            pd.dismiss();
                                            Toast.makeText(getContext(), "Image Updated..", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //error adding url in database of user
                                            //dismiss progress bar
                                            pd.dismiss();
                                            Toast.makeText(getContext(), "Error Updating Image..", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            //error
                            pd.dismiss();
                            Toast.makeText(getContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //there were some error(s) get and show error message, dismiss progress dialog
                        pd.dismiss();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void pickFromGallery() {

        //Creating intent.
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//        //Intent of picking image from gallery
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
//        galleryIntent.setType("image/*");
//        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);


    }

    private void pickFromCamera(){
        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");

        //put image uri
        image_uri = getActivity().getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }


}