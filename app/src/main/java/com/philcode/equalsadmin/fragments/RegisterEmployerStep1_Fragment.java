package com.philcode.equalsadmin.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.philcode.equalsadmin.R;

import java.io.IOException;
import java.net.URL;

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
    private URL empID_url;

    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private String storagePath = "Employee_IDs/";

    //request codes
    private int Image_Request_Code = 7;

    //uri of picked image
    private Uri filePathUri;

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

        buttonNext = view.findViewById(R.id.btnNext_EMPFragment2);

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

                checkTextFieldValidation(editText_FirstName);
                checkTextFieldValidation(editText_LastName);
                checkTextFieldValidation(editText_CompanyName);
                checkTextFieldValidation(editText_ContactNumber);
                checkTextFieldValidation(editText_CompanyAddress);
                checkTextFieldValidation(editText_CompanyOverview);

                if(valid){
    //-----------------------------------------------------ADD EMP ID IMAGE URL TO BUNDLE--------------------------------------------------------------------
                    //Send data to the next fragment
                    fragment1_bundle_sendToFragment2.putString("EMP_FirstName", editText_FirstName.getText().toString().trim());
                    fragment1_bundle_sendToFragment2.putString("EMP_LastName", editText_LastName.getText().toString().trim());
                    fragment1_bundle_sendToFragment2.putString("Company_Name", editText_CompanyName.getText().toString().trim());
                    fragment1_bundle_sendToFragment2.putString("Company_ContactNumber", editText_ContactNumber.getText().toString().trim());
                    fragment1_bundle_sendToFragment2.putString("Company_Address", editText_CompanyAddress.getText().toString().trim());
                    fragment1_bundle_sendToFragment2.putString("Company_Overview", editText_CompanyOverview.getText().toString().trim());
                    fragment1_bundle_sendToFragment2.putString("Company_City", spinner_companyCity.getSelectedItem().toString().trim());

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
                }
            }
        });
        return view;
    }

    private Boolean checkTextFieldValidation(TextInputEditText textInputEditText) {
        if(textInputEditText.getText().toString().isEmpty()){
            Toast.makeText(context, "Please fill up the form completely.", Toast.LENGTH_SHORT).show();
            valid = false;
        }else{
            valid = true;
        }

        return valid;
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
            filePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePathUri);
                imageView_empID.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}