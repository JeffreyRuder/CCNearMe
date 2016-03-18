package com.epicodus.ccnearme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jeffrey on 3/18/2016.
 */
public class CollegeAdapterShort extends ArrayAdapter<College> {
    public CollegeAdapterShort(Context context, ArrayList<College> colleges) {
        super(context, 0, colleges);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        College college = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_college_short, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvCity = (TextView) convertView.findViewById(R.id.tvCity);
        TextView tvPriceCalculator = (TextView) convertView.findViewById(R.id.tvPriceCalculator);
        // Populate the data into the template view using the data object
        tvName.setText(college.name);
        tvCity.setText(college.city + ", " + college.state);
        tvPriceCalculator.setText(college.price_calculator_url);
        // Return the completed view to render on screen
        return convertView;
    }

}
