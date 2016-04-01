package com.epicodus.ccnearme;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Jeffrey on 4/1/2016.
 */
public class CollegeApplication extends Application {
    private static CollegeApplication app;
    private Firebase mFirebaseRef;

    public static CollegeApplication getAppInstance() {
        return app;
    }

    public Firebase getFirebaseRef() {
        return mFirebaseRef;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(this.getString(R.string.firebase_url));
    }
}
