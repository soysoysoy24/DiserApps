package com.lemonsquare.diserapps.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class MaterialModel extends CustomerModel implements Serializable {

    @JsonProperty("MATCODE")
    private String matcd;

    @JsonProperty("MATNAME")
    private String matnme;

    @JsonProperty("UNIT")
    private String uom;

    @JsonProperty("EXTMATGRP")
    private String extmat;

    private String  expdte;



    private int Qty;


    public String getMatcd() {
        return matcd.trim();
    }

    public void setMatcd(String matcd) {
        this.matcd = matcd.trim();
    }

    public String getMatnme() {
        return matnme.trim();
    }

    public void setMatnme(String matnme) {
        this.matnme = matnme.trim();
    }

    public String getUom() {
        return uom.trim();
    }

    public void setUom(String uom) {
        this.uom = uom.trim();
    }


    public String getExtmat() {
        return extmat.trim();
    }

    public void setExtmat(String extmat) {
        this.extmat = extmat.trim();
    }


    public String getExpdte() {
        return expdte.trim();
    }

    public void setExpdte(String expdte) {
        this.expdte = expdte.trim();
    }


    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }

}
