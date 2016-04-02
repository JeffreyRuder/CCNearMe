package com.epicodus.ccnearme.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jeffrey on 3/25/2016.
 */
public class CollegeScorecardService {
    private Context mContext;
    private final String API_KEY;
    private static final String API_ENDPOINT = "https://api.data.gov/ed/collegescorecard/v1/schools.json";
    private static final String RESULTS_PER_PAGE = "100";
    private static final String INCLUDED_CARNEGIE_CLASSIFICATIONS = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24";
    private final String INCLUDED_OWNERSHIPS;
    private boolean mIncludePrivate;
    private boolean mIncludeForProfit;
    private SharedPreferences mSharedPreferences;

    public CollegeScorecardService(Context context) {
        mContext = context;
        API_KEY = mContext.getString(R.string.DATA_GOV_KEY);
        mSharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.shared_preferences_file), Context.MODE_PRIVATE);
        mIncludePrivate = mSharedPreferences.getBoolean("include_private", true);
        mIncludeForProfit = mSharedPreferences.getBoolean("include_for_profit", true);

        if (mIncludeForProfit && mIncludePrivate) {
            INCLUDED_OWNERSHIPS = "1,2,3";
        } else if (mIncludeForProfit) {
            INCLUDED_OWNERSHIPS = "1,3";
        } else if (mIncludePrivate) {
            INCLUDED_OWNERSHIPS = "1,2";
        } else {
            INCLUDED_OWNERSHIPS = "1";
        }
    }

    public void findColleges(String zip, int searchRange, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder().build();

        String fieldsToInclude = "id," +
                "school.name," +
                "school.city," +
                "school.state," +
                "school.zip," +
                "school.ownership," +
                "school.locale," +
                "school.carnegie_basic," +
                "school.branches," +
                "school.price_calculator_url," +
                "school.school_url";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_ENDPOINT).newBuilder();

        //filter results by type of college
        urlBuilder.addQueryParameter("school.carnegie_basic", INCLUDED_CARNEGIE_CLASSIFICATIONS);
        urlBuilder.addQueryParameter("school.ownership", INCLUDED_OWNERSHIPS);

        //include only schools enrolling students recently
        urlBuilder.addQueryParameter(threeYearsAgo() + ".student.size__range", "1..");

        //indicate what information we want
        urlBuilder.addQueryParameter("_fields", fieldsToInclude);

        //filter results by distance
        urlBuilder.addQueryParameter("_zip", zip);
        urlBuilder.addQueryParameter("_distance", searchRange + "mi");

        //specify remaining parameters
        urlBuilder.addQueryParameter("_per_page", RESULTS_PER_PAGE);
        urlBuilder.addQueryParameter("api_key", API_KEY);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public ArrayList<College> processResults(Response response) {
        ArrayList<College> colleges = new ArrayList<>();

        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject responseJSON = new JSONObject(jsonData);
                JSONArray collegeResults = responseJSON.getJSONArray("results");

                for (int i = 0; i < collegeResults.length(); i++) {
                    JSONObject collegeResult = collegeResults.getJSONObject(i);
                    int id = collegeResult.getInt("id");
                    String name = collegeResult.getString("school.name");
                    String city = collegeResult.getString("school.city");
                    String state = collegeResult.getString("school.state");
                    int zip = collegeResult.getInt("school.zip");
                    int ownership = collegeResult.getInt("school.ownership");
                    int locale = collegeResult.getInt("school.locale");
                    int carnegie = collegeResult.getInt("school.carnegie_basic");
                    int numberOfBranches = collegeResult.getInt("school.branches");
                    String priceCalculatorUrl = collegeResult.getString("school.price_calculator_url");
                    String collegeUrl = collegeResult.getString("school.school_url");

                    College college = new College(id, name, city, state, zip, ownership, locale,
                            carnegie, numberOfBranches, priceCalculatorUrl, collegeUrl);
                    colleges.add(college);
                }
            }
        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
        return colleges;
    }

    private String threeYearsAgo() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -3);
        return (dateFormat.format(calendar.getTime()));
    }
}
