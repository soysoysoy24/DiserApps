package com.lemonsquare.diserapps.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExpenseModel {

    @JsonProperty("EXPNSE_DESC")
    private String xpnseDesc;

    @JsonProperty("TRANSPO_XPNSE_DESC")
    private String xpnseTranDesc;

    private int id;


    public String getXpnseDesc() {
        return xpnseDesc.trim();
    }

    public void setXpnseDesc(String xpnseDesc) {
        this.xpnseDesc = xpnseDesc.trim();
    }

    public String getXpnseTranDesc() {
        return xpnseTranDesc.trim();
    }

    public void setXpnseTranDesc(String xpnseTranDesc) {
        this.xpnseTranDesc = xpnseTranDesc.trim();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
