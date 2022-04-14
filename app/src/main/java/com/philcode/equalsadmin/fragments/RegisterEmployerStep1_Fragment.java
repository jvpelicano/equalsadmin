package com.philcode.equalsadmin.fragments;

import static android.app.Activity.RESULT_OK;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.UpdateEmployerPersonalInfo;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class RegisterEmployerStep1_Fragment extends Fragment {

    private View view;
    private MaterialButton buttonNext;
    private TextInputEditText editText_FirstName, editText_LastName, editText_ContactNumber,
            editText_CompanyOverview, editText_CompanyAddress, editText_CompanyName;
    private Spinner spinner_companyCity;
    private ImageView imageView_empID;
    private Boolean valid = false;

    private Context context;
    private Bundle fragment1_bundle_sendToFragment2;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference employerDbRef;
    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private String storagePath1 = "Employee_IDs/";
    private String imgId;

    //request codes
    private int Image_Request_Code = 7;

    //uri of picked image
    private Uri filePathUri1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register_employer_step1, container, false);


        //layout
        editText_FirstName = view.findViewById(R.id.editEmployerFirstName);
        editText_LastName = view.findViewById(R.id.editEmployerLastName);
        editText_CompanyName = view.findViewById(R.id.editCompanyName);
        editText_ContactNumber = view.findViewById(R.id.editCompanyContact);
        editText_CompanyAddress = view.findViewById(R.id.editCompanyTextAddress);
        editText_CompanyOverview = view.findViewById(R.id.editCompanyBackground);
        spinner_companyCity = view.findViewById(R.id.spinner_EMPcity);
        imageView_empID = view.findViewById(R.id.emp_ID);

        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        employerDbRef = firebaseDatabase.getReference("Employers");
        storageReference = getInstance().getReference();

        buttonNext = view.findViewById(R.id.btnNext_EMPFragment2);
        fragment1_bundle_sendToFragment2 = new Bundle();

        context = getActivity().getApplicationContext();

        imageView_empID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    imageToString();

            }
        });
        return view;
    }

    private Boolean checkTextFieldValidation(TextInputEditText textInputEditText) {
        if(textInputEditText.getText().toString().isEmpty()){
            valid = false;
        }else{
            valid = true;
        }
        return valid;
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



                            checkTextFieldValidation(editText_FirstName);
                            checkTextFieldValidation(editText_LastName);
                            checkTextFieldValidation(editText_CompanyName);
                            checkTextFieldValidation(editText_ContactNumber);
                            checkTextFieldValidation(editText_CompanyAddress);
                            checkTextFieldValidation(editText_CompanyOverview);

                            if(valid){
                                imgId = task.getResult().toString();

                                //-----------------------------------------------------ADD EMP ID IMAGE URL TO BUNDLE--------------------------------------------------------------------
                                //Send data to the next fragment
                                fragment1_bundle_sendToFragment2.putString("EMP_FirstName", editText_FirstName.getText().toString().trim());
                                fragment1_bundle_sendToFragment2.putString("EMP_LastName", editText_LastName.getText().toString().trim());
                                fragment1_bundle_sendToFragment2.putString("Company_Name", editText_CompanyName.getText().toString().trim());
                                fragment1_bundle_sendToFragment2.putString("Company_ContactNumber", editText_ContactNumber.getText().toString().trim());
                                fragment1_bundle_sendToFragment2.putString("Company_Address", editText_CompanyAddress.getText().toString().trim());
                                fragment1_bundle_sendToFragment2.putString("Company_Overview", editText_CompanyOverview.getText().toString().trim());
                                fragment1_bundle_sendToFragment2.putString("Company_City", spinner_companyCity.getSelectedItem().toString().trim());
                                fragment1_bundle_sendToFragment2.putString("EMP_ID", imgId.trim());

                                Log.d("employee Id img", imgId);

                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                RegisterEmployerStep2_Fragment fragment2 = new RegisterEmployerStep2_Fragment();
                                fragment2.setArguments(fragment1_bundle_sendToFragment2);
                                fm.beginTransaction()
                                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                        .replace(R.id.addEMP_frameLayout, fragment2)
                                        .addToBackStack(null)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .commit();
                            }else{
                                Toast.makeText(context, "Please fill up the form completely.", Toast.LENGTH_SHORT).show();
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
                imageView_empID.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}