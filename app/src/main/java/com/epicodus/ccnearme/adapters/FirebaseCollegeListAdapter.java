package com.epicodus.ccnearme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.util.FirebaseRecyclerAdapter;
import com.firebase.client.Query;

/**
 * Created by Jeffrey on 4/1/2016.
 */
public class FirebaseCollegeListAdapter extends FirebaseRecyclerAdapter<CollegeListViewHolder, College> {

    public FirebaseCollegeListAdapter(Query query, Class<College> itemClass) {
        super(query, itemClass);
    }

    @Override
    public CollegeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.college_list_item, parent, false);
        return new CollegeListViewHolder(view, getItems());
    }

    @Override
    public void onBindViewHolder(CollegeListViewHolder holder, int position) {
        holder.bindCollege(getItem(position));
    }

    @Override
    protected void itemAdded(College item, String key, int position) {

    }

    @Override
    protected void itemChanged(College oldItem, College newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(College item, String key, int position) {

    }

    @Override
    protected void itemMoved(College item, String key, int oldPosition, int newPosition) {

    }
}
