package com.epicodus.ccnearme.ui;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.ccnearme.CollegeApplication;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.services.GeocodeService;
import com.epicodus.ccnearme.views.FontAwesomeIconTextView;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CollegeDetailFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.collegeNameTextView) TextView mCollegeNameTextView;
    @Bind(R.id.collegeLocationTextView) TextView mCollegeLocationTextView;
    @Bind(R.id.saveCollegeButton) Button mSaveCollegeButton;
    @Bind(R.id.shareCollegeButton) Button mShareCollegeButton;
    @Bind(R.id.priceCalculatorButton) Button mPriceCalculatorButton;
    @Bind(R.id.mapView) MapView mMapView;

    private College mCollege;
    private Firebase mFirebaseRef;
    private String mCurrentUser;

    private GeocodeService mGeocodeService;

    public static CollegeDetailFragment newInstance(College college) {
        CollegeDetailFragment collegeDetailFragment = new CollegeDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("college", Parcels.wrap(college));
        collegeDetailFragment.setArguments(args);
        return collegeDetailFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCollege = Parcels.unwrap(getArguments().getParcelable("college"));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_college_detail, container, false);
        ButterKnife.bind(this, view);

        mFirebaseRef = CollegeApplication.getAppInstance().getFirebaseRef();
        mCurrentUser = mFirebaseRef.getAuth().getUid();

        mCollegeNameTextView.setText(mCollege.getName());
        mCollegeLocationTextView.setText(String.format(Locale.US, getString(R.string.city_state_zip), mCollege.getCity(), mCollege.getState(), mCollege.getZip()));

        mGeocodeService = new GeocodeService(getContext());

        mPriceCalculatorButton.setOnClickListener(this);
        mShareCollegeButton.setOnClickListener(this);
        mSaveCollegeButton.setOnClickListener(this);
        mCollegeNameTextView.setOnClickListener(this);

        mMapView.onCreate(savedInstanceState);
        initializeGoogleMap();

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == mPriceCalculatorButton) {
            Uri website = Uri.parse("http://" + mCollege.getPriceCalculatorUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, website);
            if (isSafe(intent)) {
                startActivity(intent);
            }
        } else if (v == mShareCollegeButton) {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Intent.EXTRA_SUBJECT, mCollege.getName());
            intent.putExtra(Intent.EXTRA_TEXT, "I looked up " + mCollege.getName() + " on College Near Me.");
            startActivity(Intent.createChooser(intent, "How do you want to share?"));
        } else if (v == mSaveCollegeButton) {
            mFirebaseRef.child("savedcolleges/" + mCurrentUser + "/" + mCollege.getId()).setValue(mCollege);
            Toast.makeText(getContext(), "Saved " + mCollege.getName(), Toast.LENGTH_LONG).show();
        } else if (v == mCollegeNameTextView) {
            Uri website = Uri.parse("http://" + mCollege.getCollegeMainUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, website);
            if (isSafe(intent)) {
                startActivity(intent);
            }
        }
    }

    private boolean isSafe(Intent intent) {
        PackageManager packageManager = getActivity().getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return activities.size() > 0;
    }

    private void initializeGoogleMap() {

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.setMyLocationEnabled(true);

                MapsInitializer.initialize(getActivity());

                if (mCollege.getLat() != null && mCollege.getLng() != null ) {
                    //add marker for college
                    LatLng latLng = new LatLng(mCollege.getLat(), mCollege.getLng());
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng).title(mCollege.getName()));

                    //center map on marker
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                    googleMap.moveCamera(cameraUpdate);
                }
            }
        });
    }
}
