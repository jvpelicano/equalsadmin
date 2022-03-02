package com.philcode.equalsadmin.fragments;

import static android.app.Activity.RESULT_OK;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.models.Candidate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class RegisterCandidateStep1_Fragment extends Fragment {
    //models
    private Candidate candidate;
    //layout
    private View view;

    private ImageView imageView_selectPWD_id;
    private TextInputEditText editText_PWDfirstName, editText_PWDlastName,
            editText_PWDcontactNumber, editText_PWDaddress;
    private AppCompatSpinner spinner_PWDcity;
    private MaterialButton btnNext_fragment1;
    private Boolean valid = true;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pwdDbRef;
    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private String storagePath1 = "PWD_pfp/";

    //request codes
    private int Image_Request_Code = 7;

    //uri of picked image
    private Uri filePathUri1;

    //context
    private Context context;

    //results
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register_candidate_step1, container, false);


        imageView_selectPWD_id = view.findViewById(R.id.imageView_selectPWD_id);
        editText_PWDfirstName = view.findViewById(R.id.editText_PWDfirstName);
        editText_PWDlastName = view.findViewById(R.id.editText_PWDlastName);
        editText_PWDcontactNumber = view.findViewById(R.id.editText_PWDcontactNumber);
        editText_PWDaddress = view.findViewById(R.id.editText_PWDaddress);
        spinner_PWDcity = view.findViewById(R.id.spinner_PWDcity);
        btnNext_fragment1 = view.findViewById(R.id.btnNext_fragment1);

        //firebase auth instance
        firebaseDatabase = FirebaseDatabase.getInstance();
        pwdDbRef = firebaseDatabase.getReference("PWD");
        storageReference = getInstance().getReference();

        context = requireActivity();

        btnNext_fragment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageToString();
            }
        });

        imageView_selectPWD_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });


        resultLauncher();
        return view;
    }


    private void resultLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {

                        }
                    }
                });
    }

    private void imageToString(){
        if (filePathUri1 != null) {
            final StorageReference ref = storageReference.child(storagePath1+ System.currentTimeMillis() + "." + GetFileExtension(filePathUri1));
            ref.putFile(filePathUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            String firstName = editText_PWDfirstName.getText().toString();
                            String lastName = editText_PWDlastName.getText().toString();
                            String contactNumber = editText_PWDcontactNumber.getText().toString();
                            String address = editText_PWDaddress.getText().toString();
                            String city = spinner_PWDcity.getSelectedItem().toString();
//                            imgId = task.getResult().toString();
                            Bundle fragment1_bundle_sendToFragment2 = new Bundle();

                            if(firstName.isEmpty() || lastName.isEmpty() || contactNumber.isEmpty() ||
                                    address.isEmpty() || city.isEmpty()){
                                Toast.makeText(context, "Please fill out the form completely.", Toast.LENGTH_SHORT).show();
                            }else{

                                fragment1_bundle_sendToFragment2.putString("firstName", firstName);
                                fragment1_bundle_sendToFragment2.putString("lastName", lastName);
                                fragment1_bundle_sendToFragment2.putString("contactNumber", contactNumber);
                                fragment1_bundle_sendToFragment2.putString("address", address);
                                fragment1_bundle_sendToFragment2.putString("city", city);

                                //Send data to the next fragment
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                RegisterCandidateStep2_Fragment fragment2 = new RegisterCandidateStep2_Fragment();
                                fragment2.setArguments(fragment1_bundle_sendToFragment2);
                                fm.beginTransaction()
                                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                        .replace(R.id.addPWD_frameLayout, fragment2)
                                        .addToBackStack(null)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .commit();

                            }

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                }
            });
        }
        else{
            valid = false;
            Toast.makeText(context, "Please fill up the form completely.", Toast.LENGTH_SHORT).show();
        }

    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = context.getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePathUri1 = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePathUri1);
                imageView_selectPWD_id.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}