package com.epicodus.ccnearme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.epicodus.ccnearme.CollegeApplication;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.adapters.FirebaseCollegeListAdapter;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.util.OnStartDragListener;
import com.epicodus.ccnearme.util.SimpleItemTouchHelperCallback;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SavedCollegeListActivity extends AppCompatActivity implements View.OnClickListener, OnStartDragListener {
    private Query mQuery;
    private Firebase mFirebaseRef;
    private FirebaseCollegeListAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

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

    @Override
    protected void onPause() {
        super.onPause();
        saveUserOrder();
    }

    private void setUpFirebaseQuery() {
        mQuery = mFirebaseRef.child("savedcolleges/" + mFirebaseRef.getAuth().getUid()).orderByChild("index");
    }

    private void setUpRecyclerView() {
       mAdapter = new FirebaseCollegeListAdapter(mQuery, College.class, this);
       RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SavedCollegeListActivity.this);
       mCollegeRecyclerView.setLayoutManager(layoutManager);
       mCollegeRecyclerView.setHasFixedSize(true);
       mCollegeRecyclerView.setAdapter(mAdapter);
       ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
       mItemTouchHelper = new ItemTouchHelper(callback);
       mItemTouchHelper.attachToRecyclerView(mCollegeRecyclerView);
    }

    @Override
    public void onClick(View v) {
        if (v == mFloatingActionButton) {
            saveUserOrder();
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    protected void saveUserOrder() {
        for (College college : mAdapter.getItems()) {
            college.setIndex(Integer.toString(mAdapter.getItems().indexOf(college)));
            mFirebaseRef.child("savedcolleges/" + mFirebaseRef.getAuth().getUid() + "/"
                    + college.getId())
                    .setValue(college);
        }
    }
}