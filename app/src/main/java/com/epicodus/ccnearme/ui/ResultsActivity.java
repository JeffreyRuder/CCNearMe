package com.epicodus.ccnearme.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    @Bind(R.id.recyclerView) RecyclerView mCollegeRecyclerView;
    @Bind(R.id.fab) FloatingActionButton mFloatingActionButton;

    private ArrayList<College> mNearbyColleges;
    private CollegeAdapterShort mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mFloatingActionButton.setOnClickListener(this);
        mNearbyColleges = Parcels.unwrap(getIntent().getParcelableExtra("colleges"));

        mAdapter = new CollegeAdapterShort(getApplicationContext(), mNearbyColleges);
        mCollegeRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(ResultsActivity.this);
        mCollegeRecyclerView.setLayoutManager(layoutManager);
        mCollegeRecyclerView.setHasFixedSize(true);

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
