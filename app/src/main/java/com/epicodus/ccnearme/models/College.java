package com.epicodus.ccnearme.models;

public class College {
    public int id;
    public String name;
    public String city;
    public String state;
    public int carnegie_basic;
    public String price_calculator_url;

    public College(int id, String name, String city, String state, int carnegie_basic, String price_calculator_url) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.carnegie_basic = carnegie_basic;
        this.price_calculator_url = price_calculator_url;
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

    public int getCarnegieId() {
        return carnegie_basic;
    }

    public String getPriceCalculatorUrl() {
        return price_calculator_url;
    }
}
