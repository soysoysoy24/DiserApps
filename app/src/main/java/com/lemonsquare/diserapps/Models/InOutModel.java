package com.lemonsquare.diserapps.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InOutModel extends CustomerModel{

    @JsonProperty("DATE")
    private String date;

    @JsonProperty("TMEIN")
    private String tmein;

    @JsonProperty("LOCIN")
    private String locin;

    @JsonProperty("TMEOUT")
    private String tmeout;

    @JsonProperty("LOCOUT")
    private String locout;


    public String getDate() {
        return date.trim();
    }

    public void setDate(String date) {
        this.date = date.trim();
    }

    public String getTmein() {
        return tmein.trim();
    }

    public void setTmein(String tmein) {
        this.tmein = tmein.trim();
    }

    public String getLocin() {
        return locin.trim();
    }

    public void setLocin(String locin) {
        this.locin = locin.trim();
    }

    public String getTmeout() {
        return tmeout.trim();
    }

    public void setTmeout(String tmeout) {
        this.tmeout = tmeout.trim();
    }

    public String getLocout() {
        return locout.trim();
    }

    public void setLocout(String locout) {
        this.locout = locout.trim();
    }
}
