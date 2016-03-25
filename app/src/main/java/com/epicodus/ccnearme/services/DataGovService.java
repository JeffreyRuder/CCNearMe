package com.epicodus.ccnearme.services;

import android.content.Context;

import com.epicodus.ccnearme.R;

import okhttp3.Callback;

/**
 * Created by Jeffrey on 3/25/2016.
 */
public class DataGovService {
    private Context mContext;

    public DataGovService(Context context) {
        this.mContext = context;
    }

    public void findColleges(String zip, Callback callback) {
        String API_KEY = mContext.getString(R.string.DATA_GOV_KEY);
    }
}
