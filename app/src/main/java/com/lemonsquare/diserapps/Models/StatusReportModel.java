package com.lemonsquare.diserapps.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatusReportModel extends CustomerModel implements Serializable {


    @JsonProperty("STATUS_DESC")
    private String statscat;

    @JsonProperty("ID")
    private int StatsID;

    private String statsDate;

    private String statsTme;


    public String getStatscat() {
        return statscat.trim();
    }

    public void setStatscat(String statscat) {
        this.statscat = statscat.trim();
    }

    public int getStatsID() {
        return StatsID;
    }

    public void setStatsID(int statsID) {
        StatsID = statsID;
    }

    public String getStatsDate() {
        return statsDate.trim();
    }

    public void setStatsDate(String statsDate) {
        this.statsDate = statsDate.trim();
    }

    public String getStatsTme() {
        return statsTme.trim();
    }

    public void setStatsTme(String statsTme) {
        this.statsTme = statsTme.trim();
    }
}

