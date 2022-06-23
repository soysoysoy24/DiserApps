package com.lemonsquare.diserapps.Models;

public class PriceSurveyModel extends MaterialModel {

    private double prce;
    private String prdct;

    public double getPrce() {
        return prce;
    }

    public void setPrce(double prce) {
        this.prce = prce;
    }

    public String getPrdct() {
        return prdct.trim();
    }

    public void setPrdct(String prdct) {
        this.prdct = prdct.trim();
    }
}
