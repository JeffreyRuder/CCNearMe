package com.epicodus.ccnearme.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CollegeListAdapter extends RecyclerView.Adapter<CollegeListAdapter.CollegeListViewHolder> {
    private ArrayList<College> mColleges = new ArrayList<>();
    private Context mContext;

    public CollegeListAdapter(Context context, ArrayList<College> colleges) {
        mColleges = colleges;
        mContext = context;
    }

    @Override
    public CollegeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.college_list_item, parent, false);
        CollegeListViewHolder viewHolder = new CollegeListViewHolder(view);
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

    public class CollegeListViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.collegeNameTextView) TextView mCollegeNameTextView;
        @Bind(R.id.collegeCityTextView) TextView mCollegeCityTextView;
        @Bind(R.id.publicPrivateIcon) TextView mOwnershipIcon;
        @Bind(R.id.publicPrivateIconLabel) TextView mOwnershipLabel;
        @Bind(R.id.profitNonprofitIcon) TextView mProfitNonprofit;
        @Bind(R.id.profitNonprofitIconLabel) TextView mProfitNonProfitLabel;
        @Bind(R.id.urbanRuralIcon) TextView mUrbanRural;
        @Bind(R.id.urbanRuralIconLabel) TextView mUrbanRuralLabel;
        @Bind(R.id.numberCampusesIcon) TextView mNumberCampuses;
        @Bind(R.id.numberCampusesIconLabel) TextView mNumberCampusesLabel;
        private Context mContext;

        public CollegeListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindCollege(College college) {
            Resources res = mContext.getResources();

            mCollegeNameTextView.setText(college.getName());
            mCollegeCityTextView.setText(String.format(Locale.US, res.getString(R.string.city_state), college.getCity(), college.getState()));
            mOwnershipIcon.setText(college.getOwnershipIcon(res));
            mOwnershipLabel.setText(college.getOwnershipText());
            mProfitNonprofit.setText(college.getProfitStatusIcon(res));
            mProfitNonProfitLabel.setText(college.getProfitStatusText());
            mUrbanRural.setText(college.getLocaleIcon(res));
            mUrbanRuralLabel.setText(college.getLocaleText());

            if (!college.isMultiCampus()) {
                mNumberCampuses.setVisibility(View.INVISIBLE);
                mNumberCampusesLabel.setVisibility(View.INVISIBLE);
            }
        }
    }

}
