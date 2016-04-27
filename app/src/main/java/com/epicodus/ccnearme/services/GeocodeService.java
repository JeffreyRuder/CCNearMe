package com.epicodus.ccnearme.services;

import android.content.Context;
import android.location.Location;

import com.epicodus.ccnearme.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GeocodeService {
    private Context mContext;
    private final String API_KEY;

    public GeocodeService(Context context) {
        mContext = context;
        API_KEY = Constants.GOOGLE_MAPS_KEY;
    }

    public void getZipFromLocation(Location location, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/geocode/json").newBuilder();
        urlBuilder.addQueryParameter("latlng", location.getLatitude() + "," + location.getLongitude());
        urlBuilder.addQueryParameter("key", API_KEY);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public String processZipResults(Response response) {
        String zip  = "";
        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject responseJSON = new JSONObject(jsonData);
                JSONObject results = responseJSON.getJSONArray("results").getJSONObject(0);
                JSONArray addressComponents = results.getJSONArray("address_components");
                JSONObject lastObject = addressComponents.getJSONObject(addressComponents.length() - 1);
                JSONArray type = lastObject.getJSONArray("types");
                if (type.get(0).equals("postal_code_suffix")) {
                    JSONObject secondToLast = addressComponents.getJSONObject(addressComponents.length() - 2);
                    zip = secondToLast.getString("short_name");
                } else {
                    zip = lastObject.getString("short_name");
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return zip;
    }
}
