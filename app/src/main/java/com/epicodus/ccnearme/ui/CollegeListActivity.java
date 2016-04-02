package com.epicodus.ccnearme.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.epicodus.ccnearme.CollegeApplication;
import com.epicodus.ccnearme.adapters.CollegeListAdapter;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.services.GeocodeService;
import com.epicodus.ccnearme.views.DividerItemDecoration;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Queue;

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
    private JSONObject mJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mFloatingActionButton.setOnClickListener(this);
        mNearbyColleges = Parcels.unwrap(getIntent().getParcelableExtra("colleges"));

        try {
            mJson = new JSONObject(loadJSONFromAsset());
        } catch (JSONException e) {
            e.printStackTrace();
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
    protected void onStart() {
        super.onStart();
        for (College college : mNearbyColleges) {
            setCollegeLocation(college);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mFloatingActionButton) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void setCollegeLocation(College college) {
        int id = college.getId();
        try {
            JSONObject colobj = mJson.getJSONObject("locations").getJSONObject(id + "");
            String lat = colobj.getString("latitude");
            String lon = colobj.getString("longitude");
            LatLng latLng =  new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            college.setLatLng(latLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
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
