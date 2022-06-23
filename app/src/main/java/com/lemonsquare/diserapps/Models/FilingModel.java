package com.lemonsquare.diserapps.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

public class FilingModel extends DisplayModel {

    @JsonProperty("FILING_DESC")
    private String filingcat;


    private String rson;


    private int ID;


    public String getFilingcat() {
        return filingcat.trim();
    }

    public void setFilingcat(String filingcat) {
        this.filingcat = filingcat.trim();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getRson() {
        return rson.trim();
    }

    public void setRson(String rson) {
        this.rson = rson.trim();
    }
}
