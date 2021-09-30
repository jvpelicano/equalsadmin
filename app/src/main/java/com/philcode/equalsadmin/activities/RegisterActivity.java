package com.philcode.equalsadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.philcode.equalsadmin.R;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    RelativeLayout mainLayout;
    Button btnSignup;
    TextInputLayout textNameLayout, textEmailSignupLayout, textPasswordSignupLayout, textConfirmPasswordSignupLayout;
    TextInputEditText textNameSignup, textEmailSignup, textPasswordSignup, textConfirmPasswordSignup;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=\\S+$)" +
                    ".{6,}" + //at least 6 characters
                    "$");
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //init
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Admin..");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnSignup = findViewById(R.id.btnRegister);
        textNameSignup = findViewById(R.id.txtNameSignup);
        textNameSignup.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        textEmailSignup = findViewById(R.id.txtEmailSignup);
        textPasswordSignup = findViewById(R.id.txtPasswordSignup);
        textConfirmPasswordSignup = findViewById(R.id.txtConfirmPasswordSignup);
        textNameLayout = findViewById(R.id.txtnameLayout);
        textEmailSignupLayout = findViewById(R.id.txtemailsignupLayout);
        textPasswordSignupLayout = findViewById(R.id.txtpasswordsignupLayout);
        textConfirmPasswordSignupLayout = findViewById(R.id.txtconfirmpasswordsignupLayout);
        mainLayout = findViewById(R.id.signup_layout);

        btnSignup.setOnClickListener(view -> {
            if (!validateName() | !validateEmail() | !validatePassword() | !validateConfirmPassword()){

            }
            else {
                registerAdmin();
            }
        });

    }

    private void registerAdmin(){

        if(RegisterActivity.CheckNetwork.isInternetAvailable(RegisterActivity.this)) {

            progressDialog.show();
            final String nameSignup = textNameSignup.getEditableText().toString().trim();
            final String emailSignup = textEmailSignup.getEditableText().toString().trim();
            final String passwordSignup = textPasswordSignup.getEditableText().toString().trim();
            final String accountType = "Admin";

            mAuth.createUserWithEmailAndPassword(emailSignup, passwordSignup)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            //Sign in success, dismiss dialog and start register activity
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String emailSignup1 = user.getEmail();
                            String uid = user.getUid();


                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put info in hashmap
                            hashMap.put("uid", uid);
                            hashMap.put("email", emailSignup1);
                            hashMap.put("name", nameSignup);
                            hashMap.put("image", "");
                            hashMap.put("accountType", accountType);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Admin");
                            //put data within hashmap in database
                            reference.child(uid).setValue(hashMap);


                            Snackbar.make(mainLayout, "Account successfully registered", Snackbar.LENGTH_LONG).show();
                            finish();
                        } else {
                            //if sign in fails, display a message
                            progressDialog.dismiss();
                            Snackbar.make(mainLayout, "Failed to register. Please try again", Snackbar.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(e -> {
                //error dismiss progress dialog and get and show the error
                progressDialog.dismiss();
                Snackbar.make(mainLayout, "" + e.getMessage(), Snackbar.LENGTH_LONG).show();
            });

        }
        AlertDialog.Builder alert =  new AlertDialog.Builder(RegisterActivity.this);
        alert.setMessage("Make sure that your Wi-Fi or mobile data is turned on, then try again.").setCancelable(false)
                .setPositiveButton("Dismiss", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alert.create();
        alertDialog.setTitle("No Internet Connection");
        alertDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    private boolean validateName(){
        String nameInput = textNameSignup.getEditableText().toString().trim();

        if (nameInput.isEmpty()) {
            textNameLayout.setError("Enter your name");
            return false;
        } else {
            textNameLayout.setError(null);
            return true;
        }
    }

    private boolean validateEmail(){
        String emailInput = textEmailSignup.getEditableText().toString().trim();

        if (emailInput.isEmpty()) {
            textEmailSignupLayout.setError("Enter your email address");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            textEmailSignupLayout.setError("Please enter a valid email address");
            return false;
        } else {
            textEmailSignupLayout.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = textPasswordSignup.getEditableText().toString().trim();

        if (passwordInput.isEmpty()) {
            textPasswordSignupLayout.setError("Enter your password");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textPasswordSignupLayout.setError("Password must be at least 6 characters long");
            return false;
        } else {
            textPasswordSignupLayout.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String passwordInput = textPasswordSignup.getEditableText().toString().trim();
        String confirmPasswordInput = textConfirmPasswordSignup.getEditableText().toString().trim();

        if (confirmPasswordInput.isEmpty()) {
            textConfirmPasswordSignupLayout.setError("Enter your password");
            return false;
        } else if (!passwordInput.equals(confirmPasswordInput)) {
            textConfirmPasswordSignupLayout.setError("Password do not match");
            return false;
        } else {
            textConfirmPasswordSignupLayout.setError(null);
            return true;
        }
    }

    public static class CheckNetwork {

        private static final String TAG = LoginActivity.CheckNetwork.class.getSimpleName();
        public static boolean isInternetAvailable(Context context)
        {
            NetworkInfo info = ((ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (info == null)
            {
                Log.d(TAG,"No Internet Connection");
                return false;
            }
            else
            {
                if(info.isConnected())
                {
                    Log.d(TAG," Internet Connection Available...");
                }
                else
                {
                    Log.d(TAG," Internet Connection");
                }
                return true;

            }
        }
    }

    //transition when back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}