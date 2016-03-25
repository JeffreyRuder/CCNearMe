package com.epicodus.ccnearme.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.ui.CollegeDetailFragment;

import java.util.ArrayList;

/**
 * Created by Jeffrey on 3/25/2016.
 */
public class CollegePagerAdapter extends FragmentPagerAdapter {
    private ArrayList<College> mColleges;

    public CollegePagerAdapter(FragmentManager fm, ArrayList<College> colleges) {
        super(fm);
        mColleges = colleges;
    }

    @Override
    public Fragment getItem(int position) {
        return CollegeDetailFragment.newInstance(mColleges.get(position));
    }

    @Override
    public int getCount() {
        return mColleges.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mColleges.get(position).getName();
    }
}
