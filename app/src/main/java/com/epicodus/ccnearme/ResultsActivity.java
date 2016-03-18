package com.epicodus.ccnearme;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity {
    @Bind(R.id.collegeListView) ListView mCollegeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<College> arrayOfColleges = new ArrayList<College>();
        CollegeAdapterShort adapter = new CollegeAdapterShort(this, arrayOfColleges);
        mCollegeListView.setAdapter(adapter);

        //TODO: get this working with data from a JSON array
        for (int i = 0; i < 12; i++) {
            College sampleCollege = new College("Linn-Benton Community College", "Albany", "OR", "po.linnbenton.edu/institutionalresearch/netpricecalculator/npcalc.htm");
            adapter.add(sampleCollege);
        }
    }

}
