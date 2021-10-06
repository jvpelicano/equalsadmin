package com.philcode.equalsadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.philcode.equalsadmin.R;
import com.philcode.equalsadmin.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    RelativeLayout mainLayout;
    private AHBottomNavigationViewPager ahBottomNavigationViewPager;
    private AHBottomNavigation ahBottomNavigation;


    private View viewEndAnimation;
    private ImageView viewAnimation;

    public FirebaseDatabase firebaseDatabase;
    public DatabaseReference reference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.main_layout);

        ahBottomNavigationViewPager = findViewById(R.id.bottom_navigation_viewpager);
        ahBottomNavigation = findViewById(R.id.bottom_navigation);

        viewEndAnimation = findViewById(R.id.view_end_animation);
        viewAnimation = findViewById(R.id.view_animation);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        ahBottomNavigationViewPager.setAdapter(viewPagerAdapter);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.home, R.color.btnColor);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.jobs, R.color.btnColor);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.pwd, R.color.btnColor);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.emp, R.color.btnColor);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem(R.string.tab_5, R.drawable.user, R.color.btnColor);

        // Add items
        ahBottomNavigation.addItem(item1);
        ahBottomNavigation.addItem(item2);
        ahBottomNavigation.addItem(item3);
        ahBottomNavigation.addItem(item4);
        ahBottomNavigation.addItem(item5);

        // Manage titles
        ahBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        ahBottomNavigation.setAccentColor(Color.parseColor("#035297"));
        ahBottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        ahBottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            ahBottomNavigationViewPager.setCurrentItem(position);
            return true;

        });

        ahBottomNavigation.setCurrentItem(0);

        ahBottomNavigationViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                ahBottomNavigation.setCurrentItem(position);
                ahBottomNavigation.setNotification("", position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //get notification badge for Pending Job Offers
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Job_Offers");
        Query query3 = reference.orderByChild("permission").equalTo("pending");
        query3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    dataSnapshot.getChildrenCount();
                    ahBottomNavigation.setNotification(""+ dataSnapshot.getChildrenCount(), 1);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(mainLayout, "Network ERROR. Please check your internet connection", Snackbar.LENGTH_LONG).show();
            }
        });


        //get notification badge for pending PWD
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("PWD");
        Query query2 = reference.orderByChild("typeStatus").equalTo("PWDPending");
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    dataSnapshot.getChildrenCount();
                    ahBottomNavigation.setNotification(""+ dataSnapshot.getChildrenCount(), 2);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(mainLayout, "Network ERROR. Please check your internet connection", Snackbar.LENGTH_LONG).show();
            }
        });

        //get notification badge for pending EMP
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Employers");
        Query query = reference.orderByChild("typeStatus").equalTo("EMPPending");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0){
                    dataSnapshot.getChildrenCount();
                    ahBottomNavigation.setNotification(""+ dataSnapshot.getChildrenCount(), 3);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(mainLayout, "Network ERROR. Please check your internet connection", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void createNavItems() {

        // Create items
        AHBottomNavigationItem item0 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.home, R.color.btnColor);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.jobs, R.color.btnColor);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.pwd, R.color.btnColor);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.emp, R.color.btnColor);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_5, R.drawable.user, R.color.btnColor);

        // Add items
        ahBottomNavigation.addItem(item0);
        ahBottomNavigation.addItem(item1);
        ahBottomNavigation.addItem(item2);
        ahBottomNavigation.addItem(item3);
        ahBottomNavigation.addItem(item4);

        // Manage titles
        ahBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        ahBottomNavigation.setAccentColor(Color.parseColor("#035297"));
        ahBottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        ahBottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            ahBottomNavigationViewPager.setCurrentItem(position);
            return true;
        });

        ahBottomNavigation.setCurrentItem(0);

    }

//    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
//        @Override
//        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//            if (firebaseUser == null) {
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//            }
//        }
//    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
//
//    public View getViewEndAnimation() {
//        return viewEndAnimation;
//    }
//
//    public ImageView getViewAnimation() {
//        return viewAnimation;
//    }

}