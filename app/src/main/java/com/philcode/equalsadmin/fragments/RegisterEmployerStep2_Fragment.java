package com.philcode.equalsadmin.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.philcode.equalsadmin.R;

import java.net.URL;

public class RegisterEmployerStep2_Fragment extends Fragment {
    private View view;
    private MaterialButton buttonSave;
    private TextInputEditText editText_email, editText_password, editText_confirmPassword;
    private ImageView emp_CompanyLogo;

    private Bundle bundleFromFragment1;
    private Boolean valid = true;
    private String emp_firstName, emp_lastName, company_Name, company_ContactNumber, company_Address, company_City,
    company_Overview, emp_IDImageURL;
    private URL company_Logo;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register_employer_step2, container, false);
        editText_email = view.findViewById(R.id.edit_EMPEmail);
        editText_password = view.findViewById(R.id.edit_EMPPassword);
        editText_confirmPassword = view.findViewById(R.id.edit_EMPConfirmPassword);
        buttonSave = view.findViewById(R.id.btnSave_EMP);

        bundleFromFragment1 = new Bundle();

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
        }


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
                        saveDate();
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

    private void saveDate() {
        //---save data to firebase realtime db and auth from here...
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