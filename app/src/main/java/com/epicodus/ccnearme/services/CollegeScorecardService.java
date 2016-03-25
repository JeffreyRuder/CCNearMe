package com.epicodus.ccnearme.services;

import android.content.Context;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;

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
 * Created by Jeffrey on 3/25/2016.
 */
public class CollegeScorecardService {
    private Context mContext;
    private final String API_KEY;
    private static final String RESULTS_PER_PAGE = "50";
    private static final String CARNEGIE_EXCLUDE_PRIVATE = "1,2,3,4,5,6,7,8,9,12";

    public CollegeScorecardService(Context context) {
        mContext = context;
        API_KEY = mContext.getString(R.string.DATA_GOV_KEY);
    }

    public void findColleges(String zip, int searchRange, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder().build();

        String fieldsToInclude = "id," +
                "school.name," +
                "school.city," +
                "school.state," +
                "school.carnegie_basic," +
                "school.price_calculator_url";

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.data.gov/ed/collegescorecard/v1/schools.json").newBuilder();
        urlBuilder.addQueryParameter("_fields", fieldsToInclude);
        urlBuilder.addQueryParameter("school.carnegie_basic", CARNEGIE_EXCLUDE_PRIVATE);
        urlBuilder.addQueryParameter("_zip", zip);
        urlBuilder.addQueryParameter("_distance", searchRange + "mi");
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
                    int carnegie = collegeResult.getInt("school.carnegie_basic");
                    String priceCalculatorUrl = collegeResult.getString("school.price_calculator_url");

                    College college = new College(id, name, city, state, carnegie, priceCalculatorUrl);
                    colleges.add(college);
                }
            }
        } catch (IOException | JSONException exception) {
            exception.printStackTrace();
        }
        return colleges;
    }
}