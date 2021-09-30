package com.philcode.equalsadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignIn, btnforgotpass;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvSignup;

    //progress dialog
    private ProgressDialog progressDialog;

    //firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializing views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignin);
        btnforgotpass = findViewById(R.id.btnForgotpw);
        tvSignup  = findViewById(R.id.tvSignUp);

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        btnSignIn.setOnClickListener(this);
        btnforgotpass.setOnClickListener(this);
        tvSignup.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }


    public static class CheckNetwork {

        private static final String TAG = CheckNetwork.class.getSimpleName();

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
                    return true;
                }
                else
                {
                    Log.d(TAG," Internet Connection");
                    return true;
                }

            }
        }
    }


    @Override
    public void onClick(View view) {
        if(view == btnSignIn){
            if(CheckNetwork.isInternetAvailable(LoginActivity.this)) //returns true if internet available
            {
                userLogin();
            }
            else
            {
                AlertDialog.Builder alert =  new AlertDialog.Builder(LoginActivity.this);
                alert.setMessage("Make sure that your Wi-Fi or mobile data is turned on, then try again.").setCancelable(false)
                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.setTitle("No Internet Connection");
                alertDialog.show();
            }

        }

        if(view == tvSignup){
            finish();
//            startActivity(new Intent(this, RegisterActivity_emp.class));
        }

        if(view == btnforgotpass){
            finish();
            startActivity(new Intent(this, MainActivity.class));

        }
    }


    private String email, password;
    private void userLogin() {
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if (email.isEmpty()){
            etEmail.setError("Enter email");
            etEmail.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Invalid Email Pattern");
            etEmail.requestFocus();
        }
        else if (password.isEmpty()){
            etPassword.setError("Enter password");
            etPassword.requestFocus();
        }
        else if (password.isEmpty() && email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
        }
        else if (!(password.isEmpty() && email.isEmpty())) {
            progressDialog.setMessage("Logging In..");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //logged in successfully
                            checkUserType();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to login
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(LoginActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkUserType() {
        //if user is admin, manager or traveler
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Admin");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            String accountType = "" + ds.child("accountType").getValue();
                            if (accountType.equals("Admin")){
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Unauthorized Account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        // Toast.makeText(LoginActivity.this, ""+i.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}