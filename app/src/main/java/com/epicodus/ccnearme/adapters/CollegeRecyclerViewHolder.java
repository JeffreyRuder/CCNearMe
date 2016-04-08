package com.epicodus.ccnearme.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;
import com.epicodus.ccnearme.ui.CollegeDetailActivity;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CollegeRecyclerViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.collegeNameTextView)
    TextView mCollegeNameTextView;
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
    ArrayList<College> mColleges = new ArrayList<>();

    public CollegeRecyclerViewHolder(View itemView, ArrayList<College> colleges) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mColleges = colleges;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = getLayoutPosition();
                Intent intent = new Intent(mContext, CollegeDetailActivity.class);
                intent.putExtra("position", itemPosition + "");
                intent.putExtra("colleges", Parcels.wrap(mColleges));
                mContext.startActivity(intent);
            }
        });
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
        } else {
            mNumberCampuses.setVisibility(View.VISIBLE);
            mNumberCampusesLabel.setVisibility(View.VISIBLE);
        }
    }
}
