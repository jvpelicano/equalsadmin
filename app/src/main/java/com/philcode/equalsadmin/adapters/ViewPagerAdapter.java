package com.philcode.equalsadmin.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.philcode.equalsadmin.fragments.EmpFragment;
import com.philcode.equalsadmin.fragments.HomeFragment;
import com.philcode.equalsadmin.fragments.JobFragment;
import com.philcode.equalsadmin.fragments.PWDFragment;
import com.philcode.equalsadmin.fragments.ProfileFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new JobFragment();
            case 2:
                return new PWDFragment();
            case 3:
                return new EmpFragment();
            case 4:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}

