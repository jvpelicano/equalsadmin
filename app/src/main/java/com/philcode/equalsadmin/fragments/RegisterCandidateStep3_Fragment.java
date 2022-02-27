package com.philcode.equalsadmin.fragments;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.activities.MainActivity;
import com.philcode.equalsadmin.apis.UserAPI;
import com.philcode.equalsadmin.models.User;

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

    // layouts
    private MaterialButton btn_Submit;
    private Bundle bundle = new Bundle();

    //data from previous activity
    private HashMap<String, String> hashMap_disability, hashMap_secondary_skills;
    private String firstName, lastName, contactNumber,
            address, city, yearsOfExperience, educationalAttainment, workExperience,
            skill, email, password, confirmPass;

    //Boolean
    private Boolean valid = true;

    //database
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pwdNode;

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

        context = getActivity().getApplicationContext();

        //Initialize classes
            //database
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        pwdNode = firebaseDatabase.getReference().child("PWD");

        //Initialize hashmaps
        hashMap_disability = new HashMap<>();
        hashMap_secondary_skills = new HashMap<>();

        bundle = this.getArguments();

        getExtras();

        //layouts
        btn_Submit = view.findViewById(R.id.btnNext_submit);
        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               uploadData();
            }
        });

        return view;
    }

    private void getExtras() {
        if(bundle!= null){
            firstName = bundle.getString("firstName");
            lastName = bundle.getString("lastName");
            contactNumber = bundle.getString(contactNumber);
            address = bundle.getString("address");
            city = bundle.getString("city");
            yearsOfExperience = bundle.getString("yearsOfExperience");
            educationalAttainment = bundle.getString("educationalAttainment");
            workExperience = bundle.getString("workExperience");
            skill = bundle.getString("skill");

            hashMap_disability = (HashMap<String, String>) bundle.getSerializable("hashMap_disabilities");
            hashMap_secondary_skills = (HashMap<String, String>) bundle.getSerializable("hashMap_secondary_skills");
        }else{
            Toast.makeText(context, "Bundle is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadData() {

       Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserAPI userAPI = retrofit.create(UserAPI.class);

        User newUser = new User();
        newUser.setEmail(tv_email.getText().toString().trim());
        newUser.setDisplayName(firstName + lastName);

        Call<User> call = userAPI.createUser(newUser);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() != 200) {

                    User responseFromApi = response.body();
                    String email = responseFromApi.getEmail();
                    String displayName = responseFromApi.getDisplayName();
                    Log.d("Email", "here's the email" + email);
                    Log.d("DisplayName", "here's the displayName" + displayName);

                    return;

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
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
}