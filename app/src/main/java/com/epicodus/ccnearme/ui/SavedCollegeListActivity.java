package com.epicodus.ccnearme.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.epicodus.ccnearme.CollegeApplication;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.adapters.FirebaseCollegeListAdapter;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.views.DividerItemDecoration;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SavedCollegeListActivity extends AppCompatActivity implements View.OnClickListener {
    private Query mQuery;
    private Firebase mFirebaseRef;
    private FirebaseCollegeListAdapter mAdapter;

    @Bind(R.id.recyclerView) RecyclerView mCollegeRecyclerView;
    @Bind(R.id.floatingActionButton) FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_colleges);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Firebase.setAndroidContext(this);
        mFirebaseRef = CollegeApplication.getAppInstance().getFirebaseRef();
        mFloatingActionButton.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        if (v == mFloatingActionButton) {
            mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String names = "";
                    for (DataSnapshot college : dataSnapshot.getChildren()) {
                        String name = (String) college.child("name").getValue();
                        names += name + "\n";
                    }
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.my_college_list));
                    intent.putExtra(Intent.EXTRA_TEXT, names);
                    startActivity(Intent.createChooser(intent, getString(R.string.how_to_share)));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
    }
}