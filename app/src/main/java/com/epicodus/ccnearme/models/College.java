package com.epicodus.ccnearme.models;

import android.content.res.Resources;

import com.epicodus.ccnearme.R;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.nearby.messages.PublishCallback;

import org.parceler.Parcel;

@Parcel
public class College {
    public int id;
    public String name;
    public String city;
    public String state;
    public int zip;
    public int ownershipCode;
    public int localeCode;
    public int carnegieBasicCode;
    public int numberOfBranches;
    public String priceCalculatorUrl;
    public String collegeMainUrl;
    public Double lat;
    public Double lng;
    public String admissionPercentage;

    public College(){}

    public College(int id, String name, String city, String state, int zip, int ownership,
                   int locale, int carnegie, int numberOfBranches, String price_calculator_url,
                   String college_main_url, String admissionPercentage) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.ownershipCode = ownership;
        this.localeCode = locale;
        this.carnegieBasicCode = carnegie;
        this.numberOfBranches = numberOfBranches;
        this.priceCalculatorUrl = price_calculator_url;
        this.collegeMainUrl = college_main_url;
        this.admissionPercentage = admissionPercentage;
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

    public int getZip() {
        return zip;
    }

    public int getOwnershipCode() {
        return ownershipCode;
    }

    public int getLocaleCode() {
        return localeCode;
    }

    public int getCarnegieBasicCode() {
        return carnegieBasicCode;
    }

    public int getNumberOfBranches() {
        return numberOfBranches;
    }

    public String getAdmissionPercentage() {
        return admissionPercentage;
    }

    public String getPriceCalculatorUrl() {
        return priceCalculatorUrl;
    }

    public String getCollegeMainUrl() {
        return collegeMainUrl;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    @JsonIgnore
    public boolean isMultiCampus() {
        return (carnegieBasicCode == 5 | carnegieBasicCode == 7 | numberOfBranches > 1);
    }

    @JsonIgnore
    public void setLatLng(LatLng latLng) {
        lat = latLng.latitude;
        lng = latLng.longitude;
    }

    @JsonIgnore
    public String getProfitStatusText() {
        if (ownershipCode == 3) {
            return "For-Profit";
        } else {
            return "Non-Profit";
        }
    }

    @JsonIgnore
    public String getLocaleText() {
        if (localeCode < 13 ) {
            return "City";
        } else if (localeCode > 40) {
            return "Rural";
        } else {
            return "Town";
        }
    }

    @JsonIgnore
    public String getOwnershipText() {
        if (ownershipCode == 1) {
            return "Public";
        } else {
            return "Private";
        }
    }

    @JsonIgnore
    public String getOwnershipIcon (Resources resources) {
        if (ownershipCode == 1) {
            return resources.getString(R.string.icon_public_school);
        } else {
            return resources.getString(R.string.icon_private_school);
        }
    }

    @JsonIgnore
    public String getProfitStatusIcon(Resources resources) {
        if (ownershipCode == 3) {
            return resources.getString(R.string.icon_for_profit);
        } else {
            return resources.getString(R.string.icon_non_profit);
        }
    }

    @JsonIgnore
    public String getLocaleIcon(Resources resources) {
        if (localeCode < 13 ) {
            return resources.getString(R.string.icon_city);
        } else if (localeCode > 40) {
            return resources.getString(R.string.icon_rural);
        } else {
            return resources.getString(R.string.icon_town);
        }
    }

    @JsonIgnore
    public String getMultiCampusIcon(Resources resources) {
        if (numberOfBranches > 1) {
            return resources.getString(R.string.icon_multi_campus);
        } else {
            return "";
        }
    }

}
