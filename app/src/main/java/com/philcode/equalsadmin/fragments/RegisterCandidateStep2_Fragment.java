package com.philcode.equalsadmin.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterCandidateStep2_Fragment extends Fragment {
    private View view;
    private TextInputLayout layout_yearsOfExp;
    private TextInputEditText tv_yearsOfExp;
    private MaterialButton btnNext_fragment2;
    private RadioGroup rg_educAttainment, rg_workExp;
    private RadioButton rb_educAttainment, rb_workExp;
    private CheckBox checkBox_typeOfDisability_Other;
    private ProgressDialog progressDialog;
    private Spinner spinner_skillCategory;
    private ArrayAdapter<String> spinner_skillCategory_adapter;

    //Arrays
    private Integer[] type_of_disabilities_checkboxIDs;
    private Integer[] secondary_skills_checkboxIDs;
    private CheckBox[] secondary_skills_checkBoxes;
    private CheckBox[] type_of_disabilities_checkBoxes;
    private ArrayList<String> skillCategory_contentList;
    private HashMap<String, String> hashMap_disability, hashMap_secondary_skills;

    private Context context;

    //Int
    private int selected_workExpRg_ID, selected_educAttainment_ID;
    private int rb_withWorkExp_ID, rb_withoutWorkExpID;

    //String
    private String workExperience;

    //Boolean
    private Boolean valid = true;

    //Database
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pwdNode, categoryNode;

    Bundle bundlefromFragment1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register_candidate_step2, container, false);


        //Initialize layout
        checkBox_typeOfDisability_Other = view.findViewById(R.id.typeOfDisabilityOther);
        tv_yearsOfExp = view.findViewById(R.id.yearsOfExp);
        layout_yearsOfExp = view.findViewById(R.id.add_yearsOfExp_layout);
        spinner_skillCategory = view.findViewById(R.id.spinner_skillCategory);
        btnNext_fragment2 = view.findViewById(R.id.btnNext_fragment2);

        rg_educAttainment = view.findViewById(R.id.rg_educ);
        rg_workExp = view.findViewById(R.id.rg_work);

        //Integer for IDs
        rb_withoutWorkExpID = R.id.radio_10;
        rb_withWorkExp_ID = R.id.radio_11;

        //Initialize classes
        context = requireActivity();
        skillCategory_contentList = new ArrayList<>();
        hashMap_disability = new HashMap<>();
        hashMap_secondary_skills = new HashMap<>();

        //Set ArrayAdapter
        spinner_skillCategory_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, skillCategory_contentList);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        pwdNode = firebaseDatabase.getReference().child("PWD");
        categoryNode = firebaseDatabase.getReference().child("Category");
        final String currentAdmin_uID = firebaseUser.getUid();

        //Listeners
        rg_workExp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == rb_withWorkExp_ID){
                    layout_yearsOfExp.setVisibility(View.VISIBLE);
                    workExperience = "With Experience";

                }else if(checkedId == rb_withoutWorkExpID){
                    layout_yearsOfExp.setVisibility(View.GONE);
                    workExperience = "Without Experience";
                    tv_yearsOfExp.setText("0");
                }

            }
        });
        btnNext_fragment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get checked checkboxes
                selectedSecondarySkills();
                selectedTypeOfDisabilities();
                if(hashMap_secondary_skills.isEmpty() && hashMap_disability.isEmpty()){
                    Toast.makeText(context, "Hashmaps are empty.", Toast.LENGTH_LONG).show();
                }else{
                    sendData();
                }

            }
        });


        spinner_skillCategory.setAdapter(spinner_skillCategory_adapter);
        setSpinner();


        return view;
    }

    private void setSpinner(){
        categoryNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    skillCategory_contentList.add(ds.child("skill").getValue().toString());
                }
                spinner_skillCategory_adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //These methods are for checking if checkBox is checked.
    private void selectedSecondarySkills(){
        secondary_skills_checkboxIDs = new Integer[]{R.id.typeOfSkills1, R.id.typeOfSkills2,
                R.id.typeOfSkills3, R.id.typeOfSkills4, R.id.typeOfSkills5, R.id.typeOfSkills6, R.id.typeOfSkills7
                ,R.id.typeOfSkills8, R.id.typeOfSkills9, R.id.typeOfSkills10};

        secondary_skills_checkBoxes = new CheckBox[secondary_skills_checkboxIDs.length];

        for(int i = 0; i < secondary_skills_checkboxIDs.length; i++){
            secondary_skills_checkBoxes[i] = (CheckBox) view.findViewById(secondary_skills_checkboxIDs[i]);
            if(secondary_skills_checkBoxes[i].isChecked()){
                int i2 = i+1;
                hashMap_secondary_skills.put("jobSkill" + i2, secondary_skills_checkBoxes[i].getText().toString().trim());
            }
        }
        //return hashMap_secondary_skills;
    }
    private void selectedTypeOfDisabilities() {
        type_of_disabilities_checkboxIDs = new Integer[]{R.id.typeOfDisability1, R.id.typeOfDisability2, R.id.typeOfDisability3};

        type_of_disabilities_checkBoxes = new CheckBox[type_of_disabilities_checkboxIDs.length];

        for (int count_typeOfDisabilities = 0; count_typeOfDisabilities < type_of_disabilities_checkboxIDs.length; count_typeOfDisabilities++) {
            type_of_disabilities_checkBoxes[count_typeOfDisabilities] = (CheckBox) view.findViewById(type_of_disabilities_checkboxIDs[count_typeOfDisabilities]);
            if (type_of_disabilities_checkBoxes[count_typeOfDisabilities].isChecked()) {
                int j2 = count_typeOfDisabilities + 1;
                hashMap_disability.put("typeOfDisability" + j2, type_of_disabilities_checkBoxes[count_typeOfDisabilities].getText().toString().trim());
            }
        }
        if(checkBox_typeOfDisability_Other.isChecked()){
            hashMap_disability.put("typeOfDisabilityMore", "Other Disabilities");
        }
    }

    private void sendData(){
        //Send data to the next fragment
        Bundle fragment2_bundle_sendToFragment3 = new Bundle();
        selected_educAttainment_ID = rg_educAttainment.getCheckedRadioButtonId();
        rb_educAttainment = view.findViewById(selected_educAttainment_ID);

        //from fragment 1 to bundle fragment 2 sending to fragment 3
        bundlefromFragment1 = getArguments();
        if(bundlefromFragment1!= null){
            final String firstName = bundlefromFragment1.getString("firstName");
            final String lastName = bundlefromFragment1.getString("lastName");
            final String contactNumber = bundlefromFragment1.getString("contactNumber");
            final String address = bundlefromFragment1.getString("address");
            final String city = bundlefromFragment1.getString("city");

            fragment2_bundle_sendToFragment3.putString("firstName", firstName);
            fragment2_bundle_sendToFragment3.putString("lastName", lastName);
            fragment2_bundle_sendToFragment3.putString("contactNumber", contactNumber);
            fragment2_bundle_sendToFragment3.putString("address", address);
            fragment2_bundle_sendToFragment3.putString("city", city);
        }
        checkTextFieldValidation(tv_yearsOfExp);
        checkTextFieldValidation(tv_yearsOfExp);

        if(valid){
            final String skill = spinner_skillCategory.getSelectedItem().toString();
            final String yearsOfExp = tv_yearsOfExp.getText().toString().trim();
            final String educAttainment = tv_yearsOfExp.getText().toString().trim();
            fragment2_bundle_sendToFragment3.putSerializable("hashMap_disabilities", hashMap_disability);
            fragment2_bundle_sendToFragment3.putSerializable("hashMap_secondary_skills", hashMap_secondary_skills);
            fragment2_bundle_sendToFragment3.putString("yearsOfExperience", yearsOfExp);
            fragment2_bundle_sendToFragment3.putString("educationalAttainment",educAttainment);
            fragment2_bundle_sendToFragment3.putString("workExperience", workExperience);
            fragment2_bundle_sendToFragment3.putString("skill", skill);

            FragmentManager fm = getActivity().getSupportFragmentManager();
            Fragment fragment3 = new RegisterCandidateStep3_Fragment();

            fragment3.setArguments(fragment2_bundle_sendToFragment3);
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.addPWD_frameLayout, fragment3)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();


        }else{
            Toast.makeText(context, "Please fill out the form completely.", Toast.LENGTH_SHORT).show();
        }
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