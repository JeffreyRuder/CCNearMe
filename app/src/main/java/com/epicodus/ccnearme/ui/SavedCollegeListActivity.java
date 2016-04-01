package com.epicodus.ccnearme.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.epicodus.ccnearme.CollegeApplication;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.adapters.FirebaseCollegeListAdapter;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.views.DividerItemDecoration;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SavedCollegeListActivity extends AppCompatActivity {
    private Query mQuery;
    private Firebase mFirebaseRef;
    private FirebaseCollegeListAdapter mAdapter;

    @Bind(R.id.recyclerView)
    RecyclerView mCollegeRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Firebase.setAndroidContext(this);
        mFirebaseRef = CollegeApplication.getAppInstance().getFirebaseRef();

        setUpFirebaseQuery();
        setUpRecyclerView();
    }

    private void setUpFirebaseQuery() {
        mQuery = mFirebaseRef.child("savedcolleges/" + mFirebaseRef.getAuth().getUid());
    }

    private void setUpRecyclerView() {
       mAdapter = new FirebaseCollegeListAdapter(mQuery, College.class);
       RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SavedCollegeListActivity.this);
       mCollegeRecyclerView.setLayoutManager(layoutManager);
       mCollegeRecyclerView.setHasFixedSize(true);
       mCollegeRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.divider_shadow));
       mCollegeRecyclerView.setAdapter(mAdapter);
    }
}