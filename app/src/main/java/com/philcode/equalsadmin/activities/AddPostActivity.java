package com.philcode.equalsadmin.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.models.Announcement;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    //Date object
    Date date= new Date();
    //getTime() returns current time in milliseconds
    long time = date.getTime();
    //Passed the milliseconds to constructor of Timestamp class
    Timestamp ts = new Timestamp(time);


    private View viewEndAnimation;
    private ImageView viewAnimation;
    RelativeLayout addPostLayout;
    
    private ImageView addPostImg;
    private TextInputEditText addPostTitle, addPostDesc;
    private Button addPostButton;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getSupportActionBar().setTitle("Add Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().show();

        viewEndAnimation = findViewById(R.id.view_end_animation);
        viewAnimation = findViewById(R.id.view_animation);

        addPostLayout = findViewById(R.id.post_add_layout);
        addPostImg = findViewById(R.id.add_post_img);
        addPostTitle = findViewById(R.id.add_title_post);
        addPostDesc = findViewById(R.id.add_desc_post);
        addPostButton = findViewById(R.id.btn_post_publish);

        addPostButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("home_content");

                String postTitle =  addPostTitle.getText().toString().trim();
                String postDesc = addPostDesc.getText().toString().trim();
                String postImg = "";
                String dateFormat ="";
                
                DatabaseReference blankRef = reference ;
                DatabaseReference db_ref = blankRef.push();
                String postUid = db_ref.getKey();


                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("postContentTitle", postTitle);
                hashMap.put("postDescription", postDesc);
                hashMap.put("formattedDate", dateFormat);
                hashMap.put("postUid", postUid);
                hashMap.put("postImage", postImg);

                db_ref.setValue( hashMap);

                Toast.makeText(AddPostActivity.this, "Post has been published successfully", Toast.LENGTH_LONG).show();
                finish();



            }
        });

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