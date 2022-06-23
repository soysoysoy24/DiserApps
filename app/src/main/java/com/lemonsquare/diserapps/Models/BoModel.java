package com.lemonsquare.diserapps.Models;

public class BoModel extends MaterialModel{

    private String rtvnum;
    private String rtvdate;


    public String getRtvnum() {
        return rtvnum.trim();
    }

    public void setRtvnum(String rtvnum) {
        this.rtvnum = rtvnum.trim();
    }

    public String getRtvdate() {
        return rtvdate;
    }

    public void setRtvdate(String rtvdate) {
        this.rtvdate = rtvdate.trim();
    }
}
