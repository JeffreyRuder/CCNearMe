package com.epicodus.ccnearme.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.epicodus.ccnearme.R;
import com.epicodus.ccnearme.models.College;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jeffrey on 3/18/2016.
 */
public class CollegeAdapterShort extends RecyclerView.Adapter<CollegeAdapterShort.CollegeViewHolderShort> {
    private ArrayList<College> mColleges = new ArrayList<>();
    private Context mContext;

    public CollegeAdapterShort(Context context, ArrayList<College> colleges) {
        mColleges = colleges;
        mContext = context;
    }

    @Override
    public CollegeAdapterShort.CollegeViewHolderShort onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_college_short, parent, false);
        CollegeViewHolderShort viewHolder = new CollegeViewHolderShort(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CollegeAdapterShort.CollegeViewHolderShort holder, int position) {
        holder.bindCollege(mColleges.get(position));
    }

    @Override
    public int getItemCount() {
        return mColleges.size();
    }

    public class CollegeViewHolderShort extends RecyclerView.ViewHolder {
        @Bind(R.id.collegeNameTextView) TextView mCollegeNameTextView;
        @Bind(R.id.collegeCityTextView) TextView mCollegeCityTextView;
        @Bind(R.id.publicPrivateIcon) TextView mPublicPrivate;
        @Bind(R.id.publicPrivateIconLabel) TextView mPublicPrivateLabel;
        @Bind(R.id.profitNonprofitIcon) TextView mProfitNonprofit;
        @Bind(R.id.profitNonprofitIconLabel) TextView mProfitNonProfitLabel;
        @Bind(R.id.urbanRuralIcon) TextView mUrbanRural;
        @Bind(R.id.urbanRuralIconLabel) TextView mUrbanRuralLabel;
        @Bind(R.id.numberCampusesIcon) TextView mNumberCampuses;
        @Bind(R.id.numberCampusesIconLabel) TextView mNumberCampusesLabel;
        private Context mContext;

        public CollegeViewHolderShort(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        public void bindCollege(College college) {
            Resources res = mContext.getResources();

            mCollegeNameTextView.setText(college.getName());
            mCollegeCityTextView.setText(String.format(Locale.US, res.getString(R.string.city_state), college.getCity(), college.getState()));
            mPublicPrivate.setText(college.getPublicPrivateIcon(res)[0]);
            mPublicPrivateLabel.setText(college.getPublicPrivateIcon(res)[1]);
            mProfitNonprofit.setText(college.getProfitNonprofitIcon(res)[0]);
            mProfitNonProfitLabel.setText(college.getProfitNonprofitIcon(res)[1]);
            mUrbanRural.setText(college.getUrbanRuralIcon(res)[0]);
            mUrbanRuralLabel.setText(college.getUrbanRuralIcon(res)[1]);

            if (college.getMultiCampusIcon(res).length == 0) {
                mNumberCampuses.setVisibility(View.INVISIBLE);
                mNumberCampusesLabel.setVisibility(View.INVISIBLE);
            }
        }
    }

}
