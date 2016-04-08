package com.epicodus.ccnearme.ui;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.ccnearme.CollegeApplication;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
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

import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CollegeDetailFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.collegeNameTextView) TextView mCollegeNameTextView;
    @Bind(R.id.collegeLocationTextView) TextView mCollegeLocationTextView;
    @Bind(R.id.saveCollegeButton) Button mSaveCollegeButton;
    @Bind(R.id.shareCollegeButton) Button mShareCollegeButton;
    @Bind(R.id.priceCalculatorButton) Button mPriceCalculatorButton;

    @Bind(R.id.collegeDescription) TextView mCollegeDescriptionTextView;
    @Bind(R.id.admissionPercentage) TextView mAdmissionPercentageTextView;

    @Bind(R.id.mapView) MapView mMapView;

    private College mCollege;
    private Firebase mFirebaseRef;
    private String mCurrentUser;
    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 333555;

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
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_college_detail, container, false);
        ButterKnife.bind(this, view);

        mFirebaseRef = CollegeApplication.getAppInstance().getFirebaseRef();
        if (mFirebaseRef.getAuth() != null) {
            mCurrentUser = mFirebaseRef.getAuth().getUid();
        } else {
            mSaveCollegeButton.setEnabled(false);
        }

        mCollegeNameTextView.setText(mCollege.getName());
        mCollegeLocationTextView.setText(String.format(Locale.US, getString(R.string.city_state_zip), mCollege.getCity(), mCollege.getState(), mCollege.getZip()));
        if (!mCollege.getAdmissionPercentage().equals("null")) {
            mAdmissionPercentageTextView.setText(String.format(Locale.US, getString(R.string.admission_percent), mCollege.getName(), formattedAdmissionsPercentage(mCollege.getAdmissionPercentage())));
        } else {
            mAdmissionPercentageTextView.setVisibility(View.GONE);
        }
        mCollegeDescriptionTextView.setText(getCollegeDescription());

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
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                } else {
                    showMessageOKCancel("To see where you are on the map, allow College Near Me to use your location.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_COARSE_LOCATION);
                                }
                            });
                    return;
                }

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

    private String formattedAdmissionsPercentage(String rawPercentage) {
        Double doublePercentage = Double.parseDouble(rawPercentage);
        int roundedPercentage = (int) Math.round(doublePercentage * 100);
        return roundedPercentage + "%.";
    }

    private String getCollegeDescription() {
        switch (mCollege.getCarnegie_size_setting()) {
            case 1:
                return String.format(getString(R.string.carnegie_description_1), mCollege.getName());
            case 2:
                return String.format(getString(R.string.carnegie_description_2), mCollege.getName());
            case 3:
                return String.format(getString(R.string.carnegie_description_3), mCollege.getName());
            case 4:
                return String.format(getString(R.string.carnegie_description_4), mCollege.getName());
            case 5:
                return String.format(getString(R.string.carnegie_description_5), mCollege.getName());
            case 6:
                return String.format(getString(R.string.carnegie_description_6), mCollege.getName());
            case 7:
                return String.format(getString(R.string.carnegie_description_7), mCollege.getName());
            case 8:
                return String.format(getString(R.string.carnegie_description_8), mCollege.getName());
            case 9:
                return String.format(getString(R.string.carnegie_description_9), mCollege.getName());
            case 10:
                return String.format(getString(R.string.carnegie_description_10), mCollege.getName());
            case 11:
                return String.format(getString(R.string.carnegie_description_11), mCollege.getName());
            case 12:
                return String.format(getString(R.string.carnegie_description_12), mCollege.getName());
            case 13:
                return String.format(getString(R.string.carnegie_description_13), mCollege.getName());
            case 14:
                return String.format(getString(R.string.carnegie_description_14), mCollege.getName());
            case 15:
                return String.format(getString(R.string.carnegie_description_15), mCollege.getName());
            case 16:
                return String.format(getString(R.string.carnegie_description_16), mCollege.getName());
            case 17:
                return String.format(getString(R.string.carnegie_description_17), mCollege.getName());
            case 18:
                return String.format(getString(R.string.carnegie_description_18), mCollege.getName());
            default:
                return "";
        }
    }

    //boiler plate function to handle request result, uses dummy int constant
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Thank You", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getActivity(), "Location Use Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                // other 'case' lines to check for other permissions this app might request go below here
        }
    }
    //custom dialog message to notify user
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
