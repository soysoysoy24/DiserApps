package com.lemonsquare.diserapps.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IncidentReportModel extends FilingModel {

    @JsonProperty("RPRT_DESC")
    private String reportcat;


    public String getReportcat() {
        return reportcat.trim();
    }


    public void setReportcat(String reportcat) {
        this.reportcat = reportcat.trim();
    }
}
