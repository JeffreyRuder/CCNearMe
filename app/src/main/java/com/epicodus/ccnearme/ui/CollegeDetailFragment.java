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

    @Bind(R.id.admissionPercentage) TextView mAdmissionPercentageTextView;
    @Bind(R.id.admissionPercentageLabel) TextView mAdmissionPercentageLabelTextView;
    @Bind(R.id.enrolledStudents) TextView mEnrollmentTextView;
    @Bind(R.id.enrolledStudentsLabel) TextView mEnrolledStudentsLabel;
    @Bind(R.id.degreeAwarded) TextView mDegreeAwardedTextView;
    @Bind(R.id.degreeAwardedLabel) TextView mDegreeAwardedLabelTextView;
    @Bind(R.id.residentialIcon) TextView mResidentialIconTextView;
    @Bind(R.id.residentialLabel) TextView mResidentialLabelTextView;
    @Bind(R.id.partTimeIcon) TextView mPartTimeIconTextView;
    @Bind(R.id.partTimeIconLabel) TextView mPartTimeIconLabelTextView;

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
            mAdmissionPercentageLabelTextView.setText(String.format(Locale.US, getString(R.string.admission_percent), getFormattedAdmissionsPercentage(mCollege.getAdmissionPercentage())));
        } else {
            mAdmissionPercentageTextView.setVisibility(View.GONE);
            mAdmissionPercentageLabelTextView.setVisibility(View.GONE);
        }
        setDegreeAwardText();
        setEnrollmentText();
        setResidentialText();
        setPartTimeText();

        mPriceCalculatorButton.setOnClickListener(this);
        mShareCollegeButton.setOnClickListener(this);
        mSaveCollegeButton.setOnClickListener(this);
        mCollegeNameTextView.setOnClickListener(this);

        mMapView.onCreate(savedInstanceState);
        initializeGoogleMap();

        return view;
    }

    /////HANDLE CLICKS

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

    /////GOOGLE MAP

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
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /////FORMATTING COLLEGE DATA FOR USER

    private String getFormattedAdmissionsPercentage(String rawPercentage) {
        Double percentageAsDouble = Double.parseDouble(rawPercentage);
        int roundedPercentage = (int) Math.round(percentageAsDouble * 100);
        return roundedPercentage + "%";
    }

    private void setEnrollmentText() {
        switch (mCollege.getCarnegie_size_setting()) {
            case 1:case 6:case 7:case 8:
                mEnrolledStudentsLabel.setText(getString(R.string.verySmallEnrollment));
                break;
            case 2:
                mEnrolledStudentsLabel.setText(getString(R.string.smallEnrollmentCC));
                break;
            case 9:case 10:case 11:
                mEnrolledStudentsLabel.setText(getString(R.string.smallEnrollment));
                break;
            case 3:
                mEnrolledStudentsLabel.setText(getString(R.string.mediumEnrollmentCC));
                break;
            case 4:case 12:case 13:case 14:
                mEnrolledStudentsLabel.setText(getString(R.string.mediumEnrollment));
                break;
            case 5:case 15:case 16:case 17:
                mEnrolledStudentsLabel.setText(getString(R.string.largeEnrolment));
                break;
            default:
                mEnrolledStudentsLabel.setText(getString(R.string.noEnrollmentData));
        }
    }

    private void setDegreeAwardText() {
        if (mCollege.getCarnegie_size_setting() >= 6) {
            mDegreeAwardedTextView.setText(getString(R.string.bachelorsDegreesAwardedIcon));
            mDegreeAwardedLabelTextView.setText(getString(R.string.bachelorsDegreesAwardedText));
        }
    }

    private void setResidentialText() {
        if (mCollege.getCarnegie_size_setting() == 8 ||
                mCollege.getCarnegie_size_setting() == 11 ||
                mCollege.getCarnegie_size_setting() == 14 ||
                mCollege.getCarnegie_size_setting() == 17) {
            mResidentialIconTextView.setText(getString(R.string.residentialIcon));
            mResidentialLabelTextView.setText(getString(R.string.residentialLabel));
        } else if (mCollege.getCarnegie_size_setting() < 6 || mCollege.getCarnegie_size_setting() == 18) {
            mResidentialIconTextView.setVisibility(View.GONE);
            mResidentialLabelTextView.setVisibility(View.GONE);
        }
    }

    private void setPartTimeText() {
        if (mCollege.getCarnegie_size_setting() == 6 ||
                mCollege.getCarnegie_size_setting() == 9 ||
                mCollege.getCarnegie_size_setting() == 12 ||
                mCollege.getCarnegie_size_setting() == 15) {
            mPartTimeIconLabelTextView.setText(getString(R.string.partTimeLabel));
        } else if (mCollege.getCarnegie_size_setting() < 6 || mCollege.getCarnegie_size_setting() == 18) {
            mPartTimeIconLabelTextView.setVisibility(View.GONE);
            mPartTimeIconTextView.setVisibility(View.GONE);
        }
    }
}
