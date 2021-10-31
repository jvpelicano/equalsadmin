package com.philcode.equalsadmin.fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.philcode.equalsadmin.R;

public class ForgotPasswordBottomSheet extends BottomSheetDialogFragment {
    TextInputLayout forgotPasswordLayout;
    TextInputEditText forgotPasswordText;
    MaterialButton forgotPasswordButton;
    ImageButton closeButton;
    FirebaseAuth mAuth;

    public ForgotPasswordBottomSheet() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgot_password_email, container, false);

        mAuth = FirebaseAuth.getInstance();

        forgotPasswordLayout = view.findViewById(R.id.forgot_pword_layout);
        forgotPasswordText = view.findViewById(R.id.forgot_pword_txt);

        forgotPasswordButton = view.findViewById(R.id.forgot_pword_phone_btn);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = forgotPasswordText.getText().toString();
                if (email.isEmpty()) {
                    forgotPasswordLayout.setError("Enter your email address");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    forgotPasswordLayout.setError("Please enter a valid email address");
                    return;
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Link sent to your email address", Toast.LENGTH_LONG).show();
                            } else {
                                forgotPasswordLayout.setError("Email address doesn't exist");
                                return;
                                //Toast.makeText(getActivity(), "The email address did not match our record", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        closeButton = view.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
}
