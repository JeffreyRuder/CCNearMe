package com.epicodus.ccnearme.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.epicodus.ccnearme.adapters.CollegeAdapterShort;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.collegeListView) ListView mCollegeListView;
    @Bind(R.id.fab) FloatingActionButton mFloatingActionButton;

    private ArrayList<College> mNearbyColleges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mFloatingActionButton.setOnClickListener(this);
        mNearbyColleges = Parcels.unwrap(getIntent().getParcelableExtra("colleges"));
        CollegeAdapterShort adapter = new CollegeAdapterShort(this, mNearbyColleges);
        mCollegeListView.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if (view == mFloatingActionButton) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

}
