package com.epicodus.ccnearme.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.ui.CollegeDetailActivity;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CollegeListAdapter extends RecyclerView.Adapter<CollegeListViewHolder> {
    private ArrayList<College> mColleges = new ArrayList<>();
    private Context mContext;

    public CollegeListAdapter(Context context, ArrayList<College> colleges) {
        mColleges = colleges;
        mContext = context;
    }

    @Override
    public CollegeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.college_list_item, parent, false);
        CollegeListViewHolder viewHolder = new CollegeListViewHolder(view, mColleges);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CollegeListViewHolder holder, int position) {
        holder.bindCollege(mColleges.get(position));
    }

    @Override
    public int getItemCount() {
        return mColleges.size();
    }

}
