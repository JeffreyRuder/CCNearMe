package com.epicodus.ccnearme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;

import java.util.ArrayList;

public class CollegeRecyclerAdapter extends RecyclerView.Adapter<CollegeRecyclerViewHolder> {
    private ArrayList<College> mColleges = new ArrayList<>();
    private Context mContext;

    public CollegeRecyclerAdapter(Context context, ArrayList<College> colleges) {
        mColleges = colleges;
        mContext = context;
    }

    @Override
    public CollegeRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.college_list_item, parent, false);
        CollegeRecyclerViewHolder viewHolder = new CollegeRecyclerViewHolder(view, mColleges);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CollegeRecyclerViewHolder holder, int position) {
        holder.bindCollege(mColleges.get(position));
    }

    @Override
    public int getItemCount() {
        return mColleges.size();
    }

}
