package com.epicodus.ccnearme.models;

import android.content.res.Resources;

import com.epicodus.ccnearme.R;

import org.parceler.Parcel;

@Parcel
public class College {
    public int id;
    public String name;
    public String city;
    public String state;
    public int mLocaleCode;
    public int mCarnegieBasicCode;
    public String price_calculator_url;
    public String college_main_url;

    public College(){}

    public College(int id, String name, String city, String state, int locale, int carnegie, String price_calculator_url, String college_main_url) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        mLocaleCode = locale;
        mCarnegieBasicCode = carnegie;
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
        return city;
    }

    public String getState() {
        return state;
    }

    public int getLocaleCode() {
        return mLocaleCode;
    }

    public int getCarnegieBasicCode() {
        return mCarnegieBasicCode;
    }

    public String getPriceCalculatorUrl() {
        return price_calculator_url;
    }

    public String getMainUrl() {
        return college_main_url;
    }

    public String[] getPublicPrivateIcon(Resources resources) {
        if (mCarnegieBasicCode <= 8 | mCarnegieBasicCode == 11 | mCarnegieBasicCode == 12) {
            return new String[] {resources.getString(R.string.icon_public_school), resources.getString(R.string.icon_public_label)};
        } else {
            return new String[] {resources.getString(R.string.icon_private_school), resources.getString(R.string.icon_private_label)};
        }
    }

    public String[] getProfitNonprofitIcon(Resources resources) {
        if (mCarnegieBasicCode == 10 | mCarnegieBasicCode == 14) {
            return new String[] {resources.getString(R.string.icon_for_profit), resources.getString(R.string.icon_for_profit_label)};
        } else {
            return new String[] {resources.getString(R.string.icon_non_profit), resources.getString(R.string.icon_non_profit_label)};
        }
    }

    public String[] getUrbanRuralIcon(Resources resources) {
        if (mLocaleCode < 13 ) {
            return new String[] {resources.getString(R.string.icon_city), resources.getString(R.string.icon_city_label)};
        } else if (mLocaleCode > 40) {
            return new String[] {resources.getString(R.string.icon_rural), resources.getString(R.string.icon_rural_label)};
        } else {
            return new String[] {resources.getString(R.string.icon_town), resources.getString(R.string.icon_town_label)};
        }
    }

    public String[] getMultiCampusIcon(Resources resources) {
        if (mCarnegieBasicCode == 5 | mCarnegieBasicCode == 7) {
            return new String[] {resources.getString(R.string.icon_multi_campus), resources.getString(R.string.icon_multi_campus_label)};
        } else {
            return new String[]{};
        }
    }
}
