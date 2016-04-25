package com.epicodus.ccnearme.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.epicodus.ccnearme.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.aboutPoweredByText) TextView mPoweredByTextView;
    @Bind(R.id.fab) FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mPoweredByTextView.setOnClickListener(this);
        mFab.setOnClickListener(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mPoweredByTextView) {
            Uri website = Uri.parse("https://collegescorecard.ed.gov/data/");
            Intent intent = new Intent(Intent.ACTION_VIEW, website);
            if (isSafe(intent)) {
                startActivity(intent);
            }
        } else if (v == mFab) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"collegenearmeapp@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback));
            if (isSafe(intent)) {
                startActivity(intent);
            }
        }
    }

    private boolean isSafe(Intent intent) {
        PackageManager packageManager = this.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }
}
