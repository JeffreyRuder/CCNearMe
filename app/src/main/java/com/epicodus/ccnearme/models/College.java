package com.epicodus.ccnearme.models;

/**
 * Created by Jeffrey on 3/18/2016.
 */
public class College {
    public String name;
    public String city;
    public String state;
    public String price_calculator_url;

    public College(String name, String city, String state, String price_calculator_url) {
        this.name = name;
        this.city = city;
        this.state = state;
        this.price_calculator_url = price_calculator_url;
    }
}
