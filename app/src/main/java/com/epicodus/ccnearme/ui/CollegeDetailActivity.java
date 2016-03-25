package com.epicodus.ccnearme.ui;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.adapters.CollegePagerAdapter;
import com.epicodus.ccnearme.models.College;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CollegeDetailActivity extends AppCompatActivity {
    @Bind(R.id.viewPager) ViewPager mViewPager;
    private CollegePagerAdapter adapterViewPager;
    ArrayList<College> mColleges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_detail);
        ButterKnife.bind(this);
        mColleges = Parcels.unwrap(getIntent().getParcelableExtra("colleges"));
        int startingPosition = Integer.parseInt(getIntent().getStringExtra("position"));
        adapterViewPager = new CollegePagerAdapter(getSupportFragmentManager(), mColleges);
        mViewPager.setAdapter(adapterViewPager);
        mViewPager.setCurrentItem(startingPosition);
    }
}
