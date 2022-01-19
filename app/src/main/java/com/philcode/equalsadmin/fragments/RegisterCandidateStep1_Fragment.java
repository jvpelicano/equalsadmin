package com.philcode.equalsadmin.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.models.Candidate;

import java.io.ByteArrayOutputStream;


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

    //request codes
    private int Image_Request_Code = 7;

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

        context = requireActivity();

        //onClickListeners
        /*imageView_selectPWD_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Builder with = ImagePicker.with((Activity) context).
                        createIntent(new Function1<Intent, ActivityResultLauncher>(){

                        });

            }
        });*/

        btnNext_fragment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = editText_PWDfirstName.getText().toString();
                String lastName = editText_PWDlastName.getText().toString();
                String contactNumber = editText_PWDcontactNumber.getText().toString();
                String address = editText_PWDaddress.getText().toString();
                String city = spinner_PWDcity.getSelectedItem().toString();
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

        resultLauncher();
        return view;
    }


    //converts URI from Bitmap variable
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
}