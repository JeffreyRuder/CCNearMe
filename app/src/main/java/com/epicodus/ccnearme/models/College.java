package com.epicodus.ccnearme.models;

import android.content.res.Resources;

import com.epicodus.ccnearme.R;

import org.parceler.Parcel;

@Parcel
public class College {
    public int id;
    public String name;
    public String mCity;
    public String mState;
    public int mZip;
    public int mOwnershipCode;
    public int mLocaleCode;
    public int mCarnegieBasicCode;
    public int mNumberOfBranches;
    public String price_calculator_url;
    public String college_main_url;

    public College(){}

    public College(int id, String name, String city, String state, int zip, int ownership, int locale, int carnegie, int numberOfBranches, String price_calculator_url, String college_main_url) {
        this.id = id;
        this.name = name;
        mCity = city;
        mState = state;
        mZip = zip;
        mOwnershipCode = ownership;
        mLocaleCode = locale;
        mCarnegieBasicCode = carnegie;
        mNumberOfBranches = numberOfBranches;
        this.price_calculator_url = price_calculator_url;
        this.college_main_url = college_main_url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return mCity;
    }

    public String getState() {
        return mState;
    }

    public int getZip() {
        return mZip;
    }

    public int getOwnershipCode() {
        return mOwnershipCode;
    }

    public String getOwnershipText() {
        if (mOwnershipCode == 1) {
            return "Public";
        } else {
            return "Private";
        }
    }

    public String getProfitStatusText() {
        if (mOwnershipCode == 3) {
            return "For-Profit";
        } else {
            return "Non-Profit";
        }
    }

    public String getLocaleText() {
        if (mLocaleCode < 13 ) {
            return "City";
        } else if (mLocaleCode > 40) {
            return "Rural";
        } else {
            return "Town";
        }
    }

    public int getLocaleCode() {
        return mLocaleCode;
    }

    public int getCarnegieBasicCode() {
        return mCarnegieBasicCode;
    }

    public int getNumberOfBranches() {
        return mNumberOfBranches;
    }

    public String getPriceCalculatorUrl() {
        return price_calculator_url;
    }

    public String getMainUrl() {
        return college_main_url;
    }

    public String getOwnershipIcon (Resources resources) {
        if (mOwnershipCode == 1) {
            return resources.getString(R.string.icon_public_school);
        } else {
            return resources.getString(R.string.icon_private_school);
        }
    }

    public String getProfitStatusIcon(Resources resources) {
        if (mOwnershipCode == 3) {
            return resources.getString(R.string.icon_for_profit);
        } else {
            return resources.getString(R.string.icon_non_profit);
        }
    }

    public String getLocaleIcon(Resources resources) {
        if (mLocaleCode < 13 ) {
            return resources.getString(R.string.icon_city);
        } else if (mLocaleCode > 40) {
            return resources.getString(R.string.icon_rural);
        } else {
            return resources.getString(R.string.icon_town);
        }
    }

    public String getMultiCampusIcon(Resources resources) {
        if (mNumberOfBranches > 1) {
            return resources.getString(R.string.icon_multi_campus);
        } else {
            return "";
        }
    }

    public boolean isMultiCampus() {
        return (mCarnegieBasicCode == 5 | mCarnegieBasicCode == 7 | mNumberOfBranches > 1);
    }
}
