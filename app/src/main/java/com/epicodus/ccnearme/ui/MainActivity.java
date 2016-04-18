package com.epicodus.ccnearme.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.ccnearme.CollegeApplication;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.services.CollegeScorecardService;
import com.epicodus.ccnearme.services.GeocodeService;
import com.epicodus.ccnearme.util.ModifiedFirebaseLoginBaseActivity;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends ModifiedFirebaseLoginBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = this.getClass().getSimpleName();

    @Bind(R.id.background_image) ImageView mBackgroundImage;
    @Bind(R.id.beginButton) Button mBeginButton;
    @Bind(R.id.collegesNearYouNumberTextView) TextView mCollegesNearYouNumberTextView;
    @Bind(R.id.zipInput) EditText mZipInput;
    @Bind(R.id.zipSearchButton) Button mZipSearchButton;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private boolean mIncludePrivate;
    private boolean mIncludeForProfit;

    private ArrayList<College> mNearbyColleges = new ArrayList<>();
    private Firebase mFirebaseRef;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLastZip;

    private MenuItem mLoginOption;
    private MenuItem mLogoutOption;

    private ProgressDialog mLoadingProgressDialog;

    private static final int SEARCH_RADIUS_IN_MILES = 18;

    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 333555;

    /////LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        mBeginButton.setOnClickListener(this);
        mBeginButton.setEnabled(false);
        mZipSearchButton.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Picasso.with(this).load(R.drawable.background).fit().centerCrop().into(mBackgroundImage);

        mFirebaseRef = CollegeApplication.getAppInstance().getFirebaseRef();
        initializeSharedPreferences();
        initializeNavigationDrawer();
        initializeApiClient();
        initializeProgressDialog();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // All providers are optional! Remove any you don't want.
        setEnabledAuthProvider(AuthProviderType.FACEBOOK);
        setEnabledAuthProvider(AuthProviderType.GOOGLE);
        checkForUserAuthentication();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /////HANDLE CLICKS

    @Override
    public void onClick(View view) {
        if (view == mBeginButton) {
            Intent intent = new Intent(MainActivity.this, CollegeListActivity.class);
            intent.putExtra("colleges", Parcels.wrap(mNearbyColleges));
            startActivity(intent);
        } else if (view == mZipSearchButton) {
            String input = mZipInput.getText().toString().trim();
            if (!input.isEmpty()) {
                mZipInput.setText("");
                getCollegesByZip(input);
            }
        }
    }

    /////ACTION BAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        //Show only appropriate login/logout options for authentication state.
        mLoginOption = menu.findItem(R.id.action_login);
        mLogoutOption = menu.findItem(R.id.action_logout);
        if (mFirebaseRef.getAuth() != null) {
            mLogoutOption.setVisible(true);
            mLoginOption.setVisible(false);
        } else {
            mLogoutOption.setVisible(false);
            mLoginOption.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_login:
                showFirebaseLoginPrompt();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /////NAVIGATION DRAWER

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_saved) {
            if (mFirebaseRef.getAuth() != null) {
                Intent intent = new Intent(MainActivity.this, SavedCollegeListActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "You must login to view saved colleges.", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_private) {
            if (item.isChecked()) {
                item.setIcon(R.drawable.ic_thumb_down_black_24dp);
                item.setChecked(false);
                mEditor.putBoolean("include_private", false).commit();
                getNearbyColleges(mLastZip, SEARCH_RADIUS_IN_MILES);
            } else {
                item.setIcon(R.drawable.ic_thumb_up_black_24dp);
                item.setChecked(true);
                mEditor.putBoolean("include_private", true).commit();
                getNearbyColleges(mLastZip, SEARCH_RADIUS_IN_MILES);
            }
            return true;
        } else if (id == R.id.nav_for_profit) {
            if (item.isChecked()) {
                item.setIcon(R.drawable.ic_thumb_down_black_24dp);
                item.setChecked(false);
                mEditor.putBoolean("include_for_profit", false).commit();
                getNearbyColleges(mLastZip, SEARCH_RADIUS_IN_MILES);
            } else {
                item.setIcon(R.drawable.ic_thumb_up_black_24dp);
                item.setChecked(true);
                mEditor.putBoolean("include_for_profit", true).commit();
                getNearbyColleges(mLastZip, SEARCH_RADIUS_IN_MILES);
            }
            return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (mIncludeForProfit) {
            MenuItem item = navigationView.getMenu().findItem(R.id.nav_for_profit);
            item.setChecked(true);
            item.setIcon(R.drawable.ic_thumb_up_black_24dp);
        }
        if (mIncludePrivate) {
            MenuItem item = navigationView.getMenu().findItem(R.id.nav_private);
            item.setChecked(true);
            item.setIcon(R.drawable.ic_thumb_up_black_24dp);
        }
    }

    /////GETTING COLLEGES

    private void initializeApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void getNearbyColleges(String location, int searchRange) {
        final CollegeScorecardService collegeScorecardService = new CollegeScorecardService(this);
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingProgressDialog.show();
            }
        });
        collegeScorecardService.findColleges(location, searchRange, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingProgressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mNearbyColleges = collegeScorecardService.processResults(response);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mNearbyColleges.size() > 0) {
                            mCollegesNearYouNumberTextView.setText(String.format(getString(R.string.main_activity_near_you), mNearbyColleges.size()));
                        } else {
                            mCollegesNearYouNumberTextView.setText(R.string.main_activity_none_nearby);
                        }
                        mBeginButton.setEnabled(true);
                        mLoadingProgressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void getCollegesByZip(String zip) {
        final CollegeScorecardService collegeScorecardService = new CollegeScorecardService(this);
        collegeScorecardService.findColleges(zip, SEARCH_RADIUS_IN_MILES, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mNearbyColleges = collegeScorecardService.processResults(response);
                Intent intent = new Intent(MainActivity.this, CollegeListActivity.class);
                intent.putExtra("colleges", Parcels.wrap(mNearbyColleges));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            useLastLocationToSearchForColleges();
        } else {
            showMessageOKCancel("Allow access to your location to see colleges near you.",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_COARSE_LOCATION);
                        }
                    });
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void useLastLocationToSearchForColleges() {
        if (mLastLocation != null) {
            final GeocodeService geocodeService = new GeocodeService(this);
            geocodeService.getZipFromLocation(mLastLocation, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    mLastZip = geocodeService.processZipResults(response);
                    getNearbyColleges(mLastZip, SEARCH_RADIUS_IN_MILES);
                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        showErrorDialog("Failed to determine your location. Please search by zip code instead.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showErrorDialog("Failed to determine your location. Please search by zip code instead.");
    }

    /////SHARED PREFERENCES FOR CONTROLLING SEARCH PARAMETERS

    private void initializeSharedPreferences() {
        mSharedPreferences = this.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mIncludePrivate = mSharedPreferences.getBoolean("include_private", true);
        mIncludeForProfit = mSharedPreferences.getBoolean("include_for_profit", true);
    }

    //LOGIN / LOGOUT

    @Override
    public void showFirebaseLoginPrompt() {
        super.showFirebaseLoginPrompt();
    }

    @Override
    public Firebase getFirebaseRef() {
        return CollegeApplication.getAppInstance().getFirebaseRef();
    }

    @Override
    public void onFirebaseLoginProviderError(FirebaseLoginError firebaseError) {
        Log.e(TAG, "Login provider error: " + firebaseError.toString());
        showErrorDialog("Unable to connect to the login provider.");
        dismissFirebaseLoginPrompt();
    }

    @Override
    public void onFirebaseLoginUserError(FirebaseLoginError firebaseError) {
        if (firebaseError.message.equals("FirebaseError: The specified user does not exist.")) {
            showErrorDialog("Failed to login. Please create an account.");
        } else {
            showErrorDialog("Failed to login. Please double-check your login information.");
        }
        dismissFirebaseLoginPrompt();
    }

    @Override
    public void onFirebaseLoggedIn(AuthData authData) {
        saveUserToFireBase(authData);
        if (mLoginOption != null && mLogoutOption != null) {
            mLogoutOption.setVisible(true);
            mLoginOption.setVisible(false);
        }
    }

    @Override
    public void onFirebaseLoggedOut() {
        Toast toast = Toast.makeText(this, "You are logged out. Login to save colleges.", Toast.LENGTH_SHORT);
        toast.show();
        if (mLoginOption != null && mLogoutOption != null) {
            mLogoutOption.setVisible(false);
            mLoginOption.setVisible(true);
        }
    }

    private void checkForUserAuthentication() {
        AuthData authData = mFirebaseRef.getAuth();
        if (authData == null) {
            showFirebaseLoginPrompt();
        }
    }

    private void saveUserToFireBase(AuthData authData) {
        Map<String, String> map = new HashMap<>();
        map.put("provider", authData.getProvider());
        if (authData.getProviderData().containsKey("displayName")) {
            map.put("displayName", authData.getProviderData().get("displayName").toString());
        }
        if (authData.getProviderData().containsKey("email")) {
            map.put("email", authData.getProviderData().get("email").toString());
        }
        if (authData.getProviderData().containsKey("profileImageURL")) {
            map.put("profileImageURL", authData.getProviderData().get("profileImageURL").toString());
        }
        mFirebaseRef.child("users").child(authData.getUid()).setValue(map);
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void initializeProgressDialog() {
        mLoadingProgressDialog = new ProgressDialog(this);
        mLoadingProgressDialog.setTitle(getString(R.string.searching));
        mLoadingProgressDialog.setMessage(getString(R.string.searching_nearby));
        mLoadingProgressDialog.setCancelable(false);
    }

}
