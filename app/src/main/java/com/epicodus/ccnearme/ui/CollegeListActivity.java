package com.epicodus.ccnearme.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.adapters.CollegeRecyclerAdapter;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.views.DividerItemDecoration;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CollegeListActivity extends AppCompatActivity {
    @Bind(R.id.recyclerView) RecyclerView mCollegeRecyclerView;

    private ArrayList<College> mNearbyColleges;
    private CollegeRecyclerAdapter mAdapter;
    private JSONObject mJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mNearbyColleges = Parcels.unwrap(getIntent().getParcelableExtra("colleges"));

        try {
            mJson = new JSONObject(loadJSONFromAsset());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter = new CollegeRecyclerAdapter(getApplicationContext(), mNearbyColleges);
        mCollegeRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(CollegeListActivity.this);
        mCollegeRecyclerView.setLayoutManager(layoutManager);
        mCollegeRecyclerView.setHasFixedSize(true);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (College college : mNearbyColleges) {
            setCollegeLocation(college);
        }
    }

    private void setCollegeLocation(College college) {
        int id = college.getId();
        try {
            JSONObject collegeObject = mJson.getJSONObject("locations").getJSONObject(id + "");
            String lat = collegeObject.getString("latitude");
            String lon = collegeObject.getString("longitude");
            LatLng latLng =  new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            college.setLatLng(latLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = this.getAssets().open("locations.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
