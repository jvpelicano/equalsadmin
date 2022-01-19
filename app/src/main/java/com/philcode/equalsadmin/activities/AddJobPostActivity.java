package com.philcode.equalsadmin.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philcode.equalsadmin.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddJobPostActivity extends AppCompatActivity {
    //layout
    private TextInputLayout layout_yearsOfExp;
    private TextView tv_jobTitle, tv_jobDescription, tv_yearsOfExp;
    private ImageView imageView;
    private RadioGroup rg_educAttainment, rg_workExp;
    private RadioButton rb_educAttainment;
    private MaterialButton btn_post;
    private ProgressDialog progressDialog;
    private CheckBox checkBox_typeOfDisability_Other, checkBox_toggle_educRequired;
    private Spinner spinner_postDuration, spinner_skillCategory;

    private ArrayAdapter<String> spinner_skillCategory_adapter, spinner_postDuration_adapter;
    Calendar cal;
    SimpleDateFormat format;

    //Arrays
    private String[] spinnerExpDate_contentList;
    private Integer[] type_of_disabilities_checkboxIDs;
    private Integer[] secondary_skills_checkboxIDs;
    private CheckBox[] secondary_skills_checkBoxes;
    private CheckBox[] type_of_disabilities_checkBoxes;
    private HashMap<String, String> hashmap_all_data;
    private ArrayList<String> skillCategory_contentList;

    //strings
    private String calculated_postExpDate;
    private String postDate;

    //int
    private int selected_workExpRg_ID, selected_educAttainment_ID;
    private int rb_withWorkExp_ID, rb_withoutWorkExpID;

    //request codes
    private int Image_Request_Code = 7;

    //database
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobPostNode, categoryNode;
    private StorageReference ref, jobOffersNode;
    private String storage_path = "Job_Offers/";
    private String currentAdmin_uID;

    private Uri FilePathUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job_post);

        getSupportActionBar().setTitle("Add Job Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        //Layout
            //views
        tv_jobTitle = findViewById(R.id.add_jobtitle_post);
        tv_jobDescription = findViewById(R.id.add_jobdesc_post);
        tv_yearsOfExp = findViewById(R.id.yearsOfExp);
        imageView = findViewById(R.id.add_job_post_img);
        layout_yearsOfExp = findViewById(R.id.add_yearsOfExp_layout);
            //layout components
        btn_post = findViewById(R.id.btn_job_post);
        spinner_skillCategory = findViewById(R.id.spinner_skillCategory);
        spinner_postDuration = findViewById(R.id.spinner_postDuration);
        checkBox_typeOfDisability_Other = findViewById(R.id.typeOfDisabilityOther);
        checkBox_toggle_educRequired = findViewById(R.id.educAttainmentRequirement);
        progressDialog = new ProgressDialog(AddJobPostActivity.this);
            //view groups
        rg_educAttainment = findViewById(R.id.rg_educ);
        rg_workExp = findViewById(R.id.rg_work);

        //Integer for IDs
        rb_withoutWorkExpID = R.id.radio_10;
        rb_withWorkExp_ID = R.id.radio_11;

        //Initialize Arrays for Spinners
        skillCategory_contentList = new ArrayList<>();
        spinnerExpDate_contentList = new String[]{"1 week","2 weeks", "3 weeks","1 month", "2 months",
                "3 months","4 months","5 months","6 months","7 months","8 months","9 months","10 months",
                "11 months","1 year", "Unlimited"};
        spinner_skillCategory_adapter = new ArrayAdapter<String>(AddJobPostActivity.this, android.R.layout.simple_spinner_dropdown_item, skillCategory_contentList);
        spinner_postDuration_adapter = new ArrayAdapter<String>(AddJobPostActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerExpDate_contentList);

        //Dates
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
        postDate = df.format(currentDate);
        cal = Calendar.getInstance();
        format  = new SimpleDateFormat("MMMM dd, yyyy");

        //Initialize Database
            //classes
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
            //nodes
        jobPostNode = firebaseDatabase.getReference().child("Job_Offers");
        categoryNode = firebaseDatabase.getReference().child("Category");
        jobOffersNode = FirebaseStorage.getInstance().getReference();
        currentAdmin_uID = firebaseUser.getUid();

        //Initialize hashmaps
        hashmap_all_data = new HashMap<>();

        //Check the selected radio button in radio group work experience
        rg_workExp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == rb_withWorkExp_ID){
                    layout_yearsOfExp.setVisibility(View.VISIBLE);
                    hashmap_all_data.put("workExperience", "With Experience");

                }else if(checkedId == rb_withoutWorkExpID){
                    layout_yearsOfExp.setVisibility(View.GONE);
                    hashmap_all_data.put("workExperience", "Without Experience");
                    tv_yearsOfExp.setText("0");
                }

            }
        });
        spinner_skillCategory.setAdapter(spinner_skillCategory_adapter);
        spinner_postDuration.setAdapter(spinner_postDuration_adapter);
        setSpinner();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSecondarySkills();
                selectedTypeOfDisabilities();
                uploadData();
            }
        });
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
            secondary_skills_checkBoxes[i] = (CheckBox) findViewById(secondary_skills_checkboxIDs[i]);
            if(secondary_skills_checkBoxes[i].isChecked()){
                int i2 = i+1;
                hashmap_all_data.put("jobSkill" + i2, secondary_skills_checkBoxes[i].getText().toString().trim());
            }
        }
    }
    private void selectedTypeOfDisabilities() {
        type_of_disabilities_checkboxIDs = new Integer[]{R.id.typeOfDisability1, R.id.typeOfDisability2, R.id.typeOfDisability3};

        type_of_disabilities_checkBoxes = new CheckBox[type_of_disabilities_checkboxIDs.length];

        for (int count_typeOfDisabilities = 0; count_typeOfDisabilities < type_of_disabilities_checkboxIDs.length; count_typeOfDisabilities++) {
            type_of_disabilities_checkBoxes[count_typeOfDisabilities] = (CheckBox) findViewById(type_of_disabilities_checkboxIDs[count_typeOfDisabilities]);
            if (type_of_disabilities_checkBoxes[count_typeOfDisabilities].isChecked()) {
                int j2 = count_typeOfDisabilities + 1;
                hashmap_all_data.put("typeOfDisability" + j2, type_of_disabilities_checkBoxes[count_typeOfDisabilities].getText().toString().trim());
            }
        }
        if(checkBox_typeOfDisability_Other.isChecked()){
            hashmap_all_data.put("typeOfDisabilityMore", "Other Disabilities");
        }
    }

    //This method is for uploading data once the user tapped the post button.
    private void uploadData() {

        if(spinner_skillCategory.getSelectedItem().toString().equals("Click to select value")){
            Toast.makeText(AddJobPostActivity.this, "Please select a skill category.", Toast.LENGTH_SHORT).show();
        }else{
            if(FilePathUri == null){
                Toast.makeText(AddJobPostActivity.this, "Please fill in all the fields.", Toast.LENGTH_LONG).show();
            }else{
                progressDialog.setTitle("Posting...");
                progressDialog.show();
                ref = jobOffersNode.child(storage_path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
                ref.putFile(FilePathUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        progressDialog.dismiss();
                                        final String imageURL = task.getResult().toString();
                                        final String pushKey = jobPostNode.push().getKey();
                                        final String skill = spinner_skillCategory.getSelectedItem().toString();
                                        final String selected_postExpDate = spinner_postDuration.getSelectedItem().toString();
                                        selected_workExpRg_ID = rg_workExp.getCheckedRadioButtonId();
                                        selected_educAttainment_ID = rg_educAttainment.getCheckedRadioButtonId();
                                        rb_educAttainment = findViewById(selected_educAttainment_ID);

                                        hashmap_all_data.put("postTitle", tv_jobTitle.getText().toString().trim());
                                        hashmap_all_data.put("postDescription", tv_jobDescription.getText().toString().trim());
                                        hashmap_all_data.put("educationalAttainment", rb_educAttainment.getText().toString());
                                        hashmap_all_data.put("yearsOfExperience", tv_yearsOfExp.getText().toString().trim());
                                        hashmap_all_data.put("uid", currentAdmin_uID);
                                        hashmap_all_data.put("postDate", postDate);
                                        hashmap_all_data.put("imageURL", imageURL);
                                        hashmap_all_data.put("postJobId", pushKey);
                                        hashmap_all_data.put("skill", skill);
                                        hashmap_all_data.put("permission", "Approved");
                                        hashmap_all_data.put("city", "Manila");
                                        hashmap_all_data.put("postLocation", "Blk 1 Lot 2 Brngy. San Jose, Manila City Philippines");
                                        hashmap_all_data.put("companyName", "PhilCode");

                                        //check if educational attainment requirement is on
                                        if(checkBox_toggle_educRequired.isChecked()){
                                            hashmap_all_data.put("educationalAttainmentRequirement", "true");
                                        }else if(!checkBox_toggle_educRequired.isChecked()){
                                            hashmap_all_data.put("educationalAttainmentRequirement", "false");
                                        }

                                        if((hashmap_all_data.containsKey("jobSkill1") || hashmap_all_data.containsKey("jobSkill2") || hashmap_all_data.containsKey("jobSkill3")
                                                ||hashmap_all_data.containsKey("jobSkill4") || hashmap_all_data.containsKey("jobSkill5") || hashmap_all_data.containsKey("jobSkill6")
                                                ||hashmap_all_data.containsKey("jobSkill7") || hashmap_all_data.containsKey("jobSkill8") || hashmap_all_data.containsKey("jobSkill9") || hashmap_all_data.containsKey("jobSkill10"))

                                                && (hashmap_all_data.containsKey("typeOfDisability1") || hashmap_all_data.containsKey("typeOfDisability2") || hashmap_all_data.containsKey("typeOfDisability3")
                                                || hashmap_all_data.containsKey("typeOfDisabilityMore"))

                                                && hashmap_all_data.containsKey("postTitle") && hashmap_all_data.containsKey("postDescription")
                                                && hashmap_all_data.containsKey("imageURL")){

                                            jobPostNode.child(pushKey).setValue(hashmap_all_data);
                                            calculateExpDate(selected_postExpDate);
                                            jobPostNode.child(pushKey).child("expDate").setValue(calculated_postExpDate);

                                        }else
                                            Toast.makeText(AddJobPostActivity.this, "Please fill in all the fields.", Toast.LENGTH_LONG).show();

                                        startActivity(new Intent(AddJobPostActivity.this, MainActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddJobPostActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        progressDialog.setCancelable(false);
                    }
                });
            }


        }

    }

    //Calculates selected post duration and save the calculated date to hashmap.
    private void calculateExpDate(String selected_postExpDate) {
        if(selected_postExpDate.equals("1 week")) { // working
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        }else if(selected_postExpDate.equals("2 weeks")) {
            cal.add(Calendar.WEEK_OF_YEAR, 2);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        }else if(selected_postExpDate.equals("3 weeks")) {
            cal.add(Calendar.WEEK_OF_YEAR, 3);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        }else if(selected_postExpDate.equals("1 month")) {
            cal.add(Calendar.MONTH, 1);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        }else if(selected_postExpDate.equals("2 months")) {
            cal.add(Calendar.MONTH, 2);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        } else if(selected_postExpDate.equals("3 months")) {
            cal.add(Calendar.MONTH, 3);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        } else if(selected_postExpDate.equals("4 months")) {
            cal.add(Calendar.MONTH, 4);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        } else if(selected_postExpDate.equals("5 months")) {
            cal.add(Calendar.MONTH, 5);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        } else if(selected_postExpDate.equals("6 months")) {
            cal.add(Calendar.MONTH, 6);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        } else if(selected_postExpDate.equals("7 months")) {
            cal.add(Calendar.MONTH, 7);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        } else if(selected_postExpDate.equals("8 months")) {
            cal.add(Calendar.MONTH, 8);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        } else if(selected_postExpDate.equals("9 months")) {
            cal.add(Calendar.MONTH, 9);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        } else if(selected_postExpDate.equals("10 months")) {
            cal.add(Calendar.MONTH, 10);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        } else if(selected_postExpDate.equals("11 months")) {
            cal.add(Calendar.MONTH, 11);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        }else if(selected_postExpDate.equals("1 year")) {
            cal.add(Calendar.MONTH, 12);
            format.format(cal.getTime());
            calculated_postExpDate = format.format(cal.getTime());

        }else if(selected_postExpDate.equals("Unlimited")) {
            calculated_postExpDate = "unlimited";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            FilePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // this method is to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}