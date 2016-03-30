package com.epicodus.ccnearme.ui;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.services.GeolocateService;
import com.epicodus.ccnearme.services.StaticMapsService;
import com.epicodus.ccnearme.views.FontAwesomeIconTextView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

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
//    @Bind(R.id.staticMapView) ImageView mStaticMapView;
    @Bind(R.id.collegeNameTextView) TextView mCollegeNameTextView;
    @Bind(R.id.collegeLocationTextView) TextView mCollegeLocationTextView;
    @Bind(R.id.saveCollegeButton) Button mSaveCollegeButton;
    @Bind(R.id.websiteIcon) FontAwesomeIconTextView mWebsiteIcon;
    @Bind(R.id.priceCalculatorIcon) FontAwesomeIconTextView mPriceCalculatorIcon;
    @Bind(R.id.websiteTextView) TextView mWebsiteText;
    @Bind(R.id.priceCalculatorTextView) TextView mPriceCalculatorText;
    @Bind(R.id.mapView) MapView mMapView;

    private College mCollege;
    GoogleMap mMap = null;

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

        mCollegeNameTextView.setText(mCollege.getName());
        mCollegeLocationTextView.setText(String.format(Locale.US, getString(R.string.city_state_zip), mCollege.getCity(), mCollege.getState(), mCollege.getZip()));
        mWebsiteIcon.setOnClickListener(this);
        mPriceCalculatorIcon.setOnClickListener(this);

        mMapView.onCreate(savedInstanceState);
        initializeGoogleMap();

//        Picasso.with(view.getContext())
//                .load(StaticMapsService.buildImageURL(mCollege, getString(R.string.GOOGLE_MAPS_KEY)))
//                .fit()
//                .centerCrop()
//                .into(mStaticMapView);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == mWebsiteIcon) {
            Uri website = Uri.parse("http://" + mCollege.getMainUrl());
            Log.v("SITE", website.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW, website);
            if (isSafe(intent)) {
                startActivity(intent);
            }
        } else if (v == mPriceCalculatorIcon) {
            Uri website = Uri.parse("http://" + mCollege.getPriceCalculatorUrl());
            Log.v("SITE", website.toString());
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
        mMap = mMapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        MapsInitializer.initialize(this.getActivity());

        if (mCollege.getLatLng() != null) {
            //add marker for college
            mMap.addMarker(new MarkerOptions()
                    .position(mCollege.getLatLng()).title(mCollege.getName()));

            //center map on marker
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCollege.getLatLng(), 10);
            mMap.moveCamera(cameraUpdate);
        }

    }

}
