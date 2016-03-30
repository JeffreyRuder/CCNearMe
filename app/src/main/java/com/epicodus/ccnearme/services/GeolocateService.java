package com.epicodus.ccnearme.services;

import android.content.Context;
import android.util.Log;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jeffrey on 3/29/2016.
 */
public class GeolocateService {
    private Context mContext;
    private final String API_KEY;

    public GeolocateService (Context context) {
        mContext = context;
        API_KEY = mContext.getString(R.string.GOOGLE_MAPS_KEY);
    }

    public void getLocation(College college, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder().build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/geocode/json").newBuilder();
        urlBuilder.addQueryParameter("address", college.getName() + " " + college.getCity() + ", " + college.getState() + " " + college.getZip());
        urlBuilder.addQueryParameter("key", API_KEY);

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void setLocation(College college, Response response) {

        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject responseJSON = new JSONObject(jsonData);
                JSONArray locationResults = responseJSON.getJSONArray("results");

                Double lat = locationResults.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                Double lng = locationResults.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                LatLng latLng = new LatLng(lat, lng);
                college.setLatLng(latLng);
            }
        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
    }
}
