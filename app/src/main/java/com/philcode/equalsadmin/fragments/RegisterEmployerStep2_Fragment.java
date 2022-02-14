package com.philcode.equalsadmin.fragments;

import static android.app.Activity.RESULT_OK;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.UpdateEmployerPersonalInfo;
import com.philcode.equalsadmin.apis.EmployerAPI;
import com.philcode.equalsadmin.apis.UserAPI;
import com.philcode.equalsadmin.models.Employer;
import com.philcode.equalsadmin.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterEmployerStep2_Fragment extends Fragment {
    private View view;
    private MaterialButton buttonSave;
    private TextInputEditText editText_email, editText_password, editText_confirmPassword;
    private ImageView emp_CompanyLogo;

    private Bundle bundleFromFragment1;
    private Boolean valid = true;
    private String emp_firstName, emp_lastName, company_Name, company_ContactNumber, company_Address, company_City,
    company_Overview, emp_company_id;
    private Context context;
    private String imgProfile;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference employerDbRef;
    //Storage
    private StorageReference storageReference;
    //path where images of traveler profile will be stored
    private final String storagePath = "Employer_Reg_Form/";

    private ProgressDialog progressDialog ;

    //request codes
    private int Image_Request_Code = 7;

    //uri of picked image
    private Uri filePathUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register_employer_step2, container, false);
        editText_email = view.findViewById(R.id.edit_EMPEmail);
        editText_password = view.findViewById(R.id.edit_EMPPassword);
        editText_confirmPassword = view.findViewById(R.id.edit_EMPConfirmPassword);
        emp_CompanyLogo = view.findViewById(R.id.add_emp_company_logo);
        buttonSave = view.findViewById(R.id.btnSave_EMP);

        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        employerDbRef = firebaseDatabase.getReference("Employers");
        storageReference = getInstance().getReference(); // firebase storage

        bundleFromFragment1 = new Bundle();

        //init progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Creating new Employer..");

        context = getActivity().getApplicationContext();

        //getData from previous Fragment
        bundleFromFragment1 = getArguments();
        if(bundleFromFragment1 != null){
        //----------------------------------GET EMP ID IMAGE URL FROM BUNDLE ---------------------------------------
              emp_firstName = bundleFromFragment1.getString("EMP_FirstName");
              emp_lastName = bundleFromFragment1.getString("EMP_LastName");
              company_Name = bundleFromFragment1.getString("Company_Name");
              company_ContactNumber = bundleFromFragment1.getString("Company_ContactNumber");
              company_Address = bundleFromFragment1.getString("Company_Address");
              company_City = bundleFromFragment1.getString("Company_City");
              company_Overview = bundleFromFragment1.getString("Company_Overview");
              emp_company_id = bundleFromFragment1.getString("EMP_ID");
        }

        emp_CompanyLogo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate Fields
                checkTextFieldValidation(editText_email);
                checkTextFieldValidation(editText_password);
                checkTextFieldValidation(editText_confirmPassword);
        //-------------------------------------VALIDATE COMPANY LOGO-----------------------------------------------
                final Boolean match = editText_password.getText().toString().trim().equals(editText_confirmPassword.getText().toString().trim());

                if(valid){
                    if(match){

                        getImageProfile();
                    }else{
                        Toast.makeText(context, "Password doesn't match.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Please fill up the form completely.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void getImageProfile() {
        //---save data to firebase realtime db and auth from here...
        if (filePathUri != null) {

            progressDialog.show();

            final StorageReference ref = storageReference.child(storagePath + System.currentTimeMillis() + "." + GetFileExtension(filePathUri));
            ref.putFile(filePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            progressDialog.dismiss();


                             imgProfile = task.getResult().toString();
                             saveUser(imgProfile, emp_company_id);

                            Toast.makeText(getActivity(),  "New Employer has been published successfully", Toast.LENGTH_LONG).show();
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
    
    private void saveUser(String empProfile, String empId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EmployerAPI employerAPI = retrofit.create(EmployerAPI.class);

        Employer newEmployer = new Employer();

        newEmployer.setEmail(editText_email.getText().toString().trim());
        newEmployer.setFirstname(emp_firstName);
        newEmployer.setLastname(emp_lastName);
        newEmployer.setPassword(editText_password.getText().toString().trim());
        newEmployer.setContact(company_ContactNumber);
        newEmployer.setFullname(company_Name);
        newEmployer.setCompanyaddress(company_Address);
        newEmployer.setCompanycity(company_City);
        newEmployer.setCompanybg(company_Overview);
        newEmployer.setEmpValidID(empId);
        newEmployer.setAvatar(empProfile);


        Call<Employer> call = employerAPI.createEmployer(newEmployer);

        call.enqueue(new Callback<Employer>() {
            @Override
            public void onResponse(Call<Employer> call, Response<Employer> response) {
                if (response.code() != 200) {

                    Employer responseFromApi = response.body();
//                    uid = responseFromApi.getUid();
//                    String email = responseFromApi.getEmail();
//                    String displayName = responseFromApi.getDisplayName();
//                    Log.d("Email", "here's the email" + email);
//                    Log.d("DisplayName", "here's the displayName" + displayName);


                    return;

                }
            }

            @Override
            public void onFailure(Call<Employer> call, Throwable t) {
                t.getMessage();
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
                emp_CompanyLogo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}