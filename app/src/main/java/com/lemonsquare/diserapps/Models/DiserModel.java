package com.lemonsquare.diserapps.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiserModel extends IncidentReportModel {

    @JsonProperty("BIO_ID")
    private String BioId;

    @JsonProperty("FIRST_NAME")
    private String Fname;

    @JsonProperty("MDDLE_NAME")
    private String Mname;

    @JsonProperty("LST_NAME")
    private String Lname;

    @JsonProperty("COMP")
    private String compny;

    @JsonProperty("ZRX_MOB")
    private String mobile;

    @JsonProperty("Res")
    private String ResultSet;


    private String actvty;
    private String Loc;


    public String getBioId() {
        return BioId.trim();
    }

    public void setBioId(String bioId) {
        BioId = bioId.trim();
    }

    public String getFname() {
        return Fname.trim();
    }

    public void setFname(String fname) {
        Fname = fname.trim();
    }


    public String getMname() {
        return Mname.trim();
    }

    public void setMname(String mname) {
        Mname = mname.trim();
    }

    public String getLname() {
        return Lname.trim();
    }

    public void setLname(String lname) {
        Lname = lname.trim();
    }

    public String getLoc() {
        return Loc.trim();
    }

    public void setLoc(String loc) {
        Loc = loc.trim();
    }


    public String getCompny() {
        return compny.trim();
    }

    public void setCompny(String compny) {
        this.compny = compny.trim();
    }


    public String getMobile() {
        return mobile.trim();
    }

    public void setMobile(String mobile) {
        this.mobile = mobile.trim();
    }


    public String getResultSet() {
        return ResultSet.trim();
    }

    public void setResultSet(String resultSet) {
        ResultSet = resultSet.trim();
    }


    public String getActvty() {
        return actvty.trim();
    }

    public void setActvty(String actvty) {
        this.actvty = actvty.trim();
    }
}
