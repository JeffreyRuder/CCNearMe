package com.epicodus.ccnearme.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.epicodus.ccnearme.adapters.CollegeListAdapter;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.services.GeolocateService;
import com.epicodus.ccnearme.views.DividerItemDecoration;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CollegeListActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.recyclerView) RecyclerView mCollegeRecyclerView;
    @Bind(R.id.fab) FloatingActionButton mFloatingActionButton;

    private ArrayList<College> mNearbyColleges;
    private CollegeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mFloatingActionButton.setOnClickListener(this);
        mNearbyColleges = Parcels.unwrap(getIntent().getParcelableExtra("colleges"));

        for (College college : mNearbyColleges) {
            setCollegeLocation(college);
        }

        mAdapter = new CollegeListAdapter(getApplicationContext(), mNearbyColleges);
        mCollegeRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(CollegeListActivity.this);
        mCollegeRecyclerView.setLayoutManager(layoutManager);
        mCollegeRecyclerView.setHasFixedSize(true);
        mCollegeRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.divider_shadow));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mFloatingActionButton) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void setCollegeLocation(final College college) {
        final GeolocateService geolocateService = new GeolocateService(this);
        geolocateService.getLocation(college, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                geolocateService.setLocation(college, response);
            }
        });
    }

}
