package com.epicodus.ccnearme.services;

import android.content.Context;

import com.epicodus.ccnearme.models.College;

import okhttp3.HttpUrl;

/**
 * Created by Jeffrey on 3/25/2016.
 */
public class StaticMapsService {

    public static String buildImageURL(College college, String key) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/staticmap").newBuilder();
        urlBuilder.addQueryParameter("visible", college.getCity() + "," + college.getState());
        urlBuilder.addQueryParameter("markers", college.getName() +
                " " + college.getCity() + ", " + college.getState() + " " + college.getZip());
        urlBuilder.addQueryParameter("size", "360x220");
        urlBuilder.addQueryParameter("scale", "2");
        urlBuilder.addQueryParameter("key", key);

        return urlBuilder.build().toString();
    }
}
