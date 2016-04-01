package com.epicodus.ccnearme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.ccnearme.CollegeApplication;
import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.services.CollegeScorecardService;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;
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

public class MainActivity extends FirebaseLoginBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    @Bind(R.id.background_image) ImageView mBackgroundImage;
    @Bind(R.id.beginButton) Button mBeginButton;
    @Bind(R.id.fab) FloatingActionButton mFloatingActionButton;
    @Bind(R.id.collegesNearYouNumberTextView) TextView mCollegesNearYouNumberTextView;

    private ArrayList<College> mNearbyColleges = new ArrayList<>();
    private Firebase mFirebaseRef;

    private MenuItem mLoginOption;
    private MenuItem mLogoutOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        getColleges("97218", 20);

        mFloatingActionButton.setOnClickListener(this);
        mBeginButton.setOnClickListener(this);
        mBeginButton.setEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Picasso.with(this).load(R.drawable.background).fit().centerCrop().into(mBackgroundImage);

        mFirebaseRef = CollegeApplication.getAppInstance().getFirebaseRef();


    }

    @Override
    protected void onStart() {
        super.onStart();
        // All providers are optional! Remove any you don't want.
        setEnabledAuthProvider(AuthProviderType.FACEBOOK);
//        setEnabledAuthProvider(AuthProviderType.TWITTER);
        setEnabledAuthProvider(AuthProviderType.GOOGLE);
        setEnabledAuthProvider(AuthProviderType.PASSWORD);
        checkForUserAuthentication();

    }

    @Override
    public void onClick(View view) {
        if (view == mBeginButton) {
            Intent intent = new Intent(MainActivity.this, CollegeListActivity.class);
            intent.putExtra("colleges", Parcels.wrap(mNearbyColleges));
            startActivity(intent);
        } else if (view == mFloatingActionButton) {
            Snackbar.make(view, "Do something here", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_public || id == R.id.nav_private) {
            if (item.isChecked()) {
                item.setIcon(R.drawable.ic_thumb_down_black_24dp);
                item.setChecked(false);
            } else {
                item.setIcon(R.drawable.ic_thumb_up_black_24dp);
                item.setChecked(true);
            }
            //TODO do something to persist this across activities
            return true;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getColleges(String location, int searchRange) {
        final CollegeScorecardService collegeScorecardService = new CollegeScorecardService(this);

        collegeScorecardService.findColleges(location, searchRange, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
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
                    }
                });
            }
        });
    }


    //LOGIN / LOGOUT METHODS

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
        Log.e(TAG, "Login user error: " + firebaseError.toString());
        showErrorDialog("Failed to login. Please double-check your login information.");
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
        Toast toast = Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT);
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

}
