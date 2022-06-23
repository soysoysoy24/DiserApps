package com.lemonsquare.diserapps.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DisplayModel {

    @JsonProperty("DISP_DESC")
    private String dispcat;

    private String asstno;

    private String imgDisp;

    private int ID,ccake,lcake,inpt,whtps,totlmn,totcat;


    public String getDispcat() {
        return dispcat.trim();
    }

    public void setDispcat(String dispcat) {
        this.dispcat = dispcat.trim();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }


    public int getCcake() {
        return ccake;
    }

    public void setCcake(int ccake) {
        this.ccake = ccake;
    }

    public int getLcake() {
        return lcake;
    }

    public void setLcake(int lcake) {
        this.lcake = lcake;
    }

    public int getWhtps() {
        return whtps;
    }

    public void setWhtps(int whtps) {
        this.whtps = whtps;
    }

    public int getInpt() {
        return inpt;
    }

    public void setInpt(int inpt) {
        this.inpt = inpt;
    }

    public int getTotlmn() {
        return totlmn;
    }

    public void setTotlmn(int totlmn) {
        this.totlmn = totlmn;
    }

    public int getTotcat() {
        return totcat;
    }

    public void setTotcat(int totcat) {
        this.totcat = totcat;
    }


    public String getAsstno() {
        return asstno.trim();
    }

    public void setAsstno(String asstno) {
        this.asstno = asstno.trim();
    }

    public String getImgDisp() {
        return imgDisp.trim();
    }

    public void setImgDisp(String imgDisp) {
        this.imgDisp = imgDisp.trim();
    }
}
