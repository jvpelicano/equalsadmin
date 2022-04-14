package com.philcode.equalsadmin.fragments;

import static android.app.Activity.RESULT_OK;
import static android.net.wifi.WifiEnterpriseConfig.Eap.PWD;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.MainActivity;
import com.philcode.equalsadmin.apis.PwdAPI;
import com.philcode.equalsadmin.apis.UserAPI;
import com.philcode.equalsadmin.models.Candidate;
import com.philcode.equalsadmin.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterCandidateStep3_Fragment extends Fragment {
    //views
    private View view;
    private TextInputEditText tv_email, tv_password, tv_confirmPass;
    private TextInputLayout pwd_enterEmail_layout, pwd_enterPassword_layout;
    private ImageView pwdImage;

    // layouts
    private MaterialButton btn_Submit;
    private Bundle bundle = new Bundle();

    //data from previous activity
    private HashMap<String, String> hashMap_disability, hashMap_secondary_skills;
    private String firstName, lastName, contactNumber,
            address, city, yearsOfExperience, educationalAttainment, workExperience,
            skill, imgId, email, password, confirmPass;

    //Boolean
    private Boolean valid = true;

    //database
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pwdNode;

    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private String storagePath1 = "PWD_Reg_Form/";
    private String pwdAvatar;

    private ProgressDialog progressDialog ;

    //request codes
    private int Image_Request_Code = 7;

    //uri of picked image
    private Uri filePathUri;

    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_register_candidate_step3, container, false);
        tv_email = view.findViewById(R.id.editText_PWDemail);
        tv_password = view.findViewById(R.id.editText_PWDpassword);
        tv_confirmPass = view.findViewById(R.id.editText_PWDconfirmPassword);
        pwd_enterEmail_layout = view.findViewById(R.id.pwd_enterEmail_layout);
        pwd_enterPassword_layout = view.findViewById(R.id.pwd_enterPassword_layout);
        pwdImage = view.findViewById(R.id.pwd_image_profile);

        context = getActivity().getApplicationContext();

        //Initialize classes
            //database
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        pwdNode = firebaseDatabase.getReference().child("PWD");
        storageReference = getInstance().getReference(); // firebase storage

        //Initialize hashmaps
        hashMap_disability = new HashMap<>();
        hashMap_secondary_skills = new HashMap<>();

        //init progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Creating new Candidate..");

        bundle = this.getArguments();

        getExtras();

        pwdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });

        //layouts
        btn_Submit = view.findViewById(R.id.btnNext_submit);
        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getImageProfile();
            }
        });

        return view;
    }

    private void getExtras() {
        if(bundle!= null){
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            contactNumber = bundle.getString("contactNumber");
            address = bundle.getString("address");
            city = bundle.getString("city");
            yearsOfExperience = bundle.getString("yearsOfExperience");
            educationalAttainment = bundle.getString("educationalAttainment");
            workExperience = bundle.getString("workExperience");
            skill = bundle.getString("skill");
            imgId = bundle.getString("pwd_Id");

            hashMap_disability = (HashMap<String, String>) bundle.getSerializable("hashMap_disabilities");
            hashMap_secondary_skills = (HashMap<String, String>) bundle.getSerializable("hashMap_secondary_skills");
        }else{
            Toast.makeText(context, "Bundle is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void getImageProfile() {
        //---save data to firebase realtime db and auth from here...
        if (filePathUri != null) {

            progressDialog.show();

            final StorageReference ref = storageReference.child(storagePath1 + System.currentTimeMillis() + "." + GetFileExtension(filePathUri));
            ref.putFile(filePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            progressDialog.dismiss();


                            pwdAvatar = task.getResult().toString();
                            uploadData(pwdAvatar, imgId);

                            Toast.makeText(getActivity(),  "New Candidate has been created successfully", Toast.LENGTH_LONG).show();
                            getActivity().finish();

                        }

                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        }
    }

    private void uploadData(String pwdProfile, String pwdId) {

       Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PwdAPI pwdAPI = retrofit.create(PwdAPI.class);

        Candidate newPwd = new Candidate();
        newPwd.setEmail(tv_email.getText().toString().trim());
        newPwd.setPassword(tv_password.getText().toString().trim());
        newPwd.setFirstName(firstName);
        newPwd.setLastName(lastName);
        newPwd.setAddress(address);
        newPwd.setCity(city);
        newPwd.setContact(contactNumber);
        newPwd.setEducationalAttainment(educationalAttainment);
        newPwd.setWorkExperience(workExperience);
        newPwd.setTotalYears(yearsOfExperience);
        newPwd.setSkill(skill);
        newPwd.setPwdProfilePic(pwdProfile);
        newPwd.setPwdIdCardNum(pwdId);

        if(hashMap_disability.containsKey("typeOfDisability1")){
            newPwd.setTypeOfDisability1("Orthopedic Disability");
        }
        if(hashMap_disability.containsKey("typeOfDisability2")){
            newPwd.setTypeOfDisability2("Partial Vision");
        }
        if(hashMap_disability.containsKey("typeOfDisability3")){
            newPwd.setTypeOfDisability3("Hearing Disability");
        }
        if(hashMap_disability.containsKey("typeOfDisabilityMore")){
            newPwd.setTypeOfDisabilityMore("Other Disability");
        }

        if (hashMap_secondary_skills.containsKey("jobSkill1")){
            newPwd.setJobSkill1("Active Listening");
        }
        if (hashMap_secondary_skills.containsKey("jobSkill2")){
            newPwd.setJobSkill2("Communication");
        }
        if (hashMap_secondary_skills.containsKey("jobSkill3")){
            newPwd.setJobSkill3("Computer Skills");
        }
        if (hashMap_secondary_skills.containsKey("jobSkill4")){
            newPwd.setJobSkill4("Customer Service");
        }
        if (hashMap_secondary_skills.containsKey("jobSkill5")){
            newPwd.setJobSkill5("Interpersonal Skills");
        }
        if (hashMap_secondary_skills.containsKey("jobSkill6")){
            newPwd.setJobSkill6("Leadership");
        }
        if (hashMap_secondary_skills.containsKey("jobSkill7")){
            newPwd.setJobSkill7("Management Skills");
        }
        if (hashMap_secondary_skills.containsKey("jobSkill8")){
            newPwd.setJobSkill8("Problem Solving");
        }
        if (hashMap_secondary_skills.containsKey("jobSkill9")){
            newPwd.setJobSkill9("Time Management");
        }
        if (hashMap_secondary_skills.containsKey("jobSkill10")){
            newPwd.setJobSkill10("Transferable Skills");
        }

        Call<Candidate> call = pwdAPI.createCandidate(newPwd);

        call.enqueue(new Callback<Candidate>() {
            @Override
            public void onResponse(Call<Candidate> call, Response<Candidate> response) {

            }

            @Override
            public void onFailure(Call<Candidate> call, Throwable t) {

            }
        });
    }

    private Boolean checkTextFieldValidation(TextInputEditText textInputEditText) {
        if(textInputEditText.getText().toString().isEmpty()){
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
                pwdImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}