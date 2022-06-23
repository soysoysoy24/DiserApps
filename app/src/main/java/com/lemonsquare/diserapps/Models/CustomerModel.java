package com.lemonsquare.diserapps.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CustomerModel extends DiserModel implements Serializable {

    @JsonProperty("CUSTCODE")
    private String custcode;

    @JsonProperty("CUSTNAME")
    private String custnme;

    @JsonProperty("CUSTALIAS")
    private String custalias;

    private String sbmtddte;


    public String getCustcode() {
        return custcode.trim();
    }

    public void setCustcode(String custcode) {
        this.custcode = custcode.trim();
    }


    public String getCustnme() {
        return custnme.trim();
    }

    public void setCustnme(String custnme) {
        this.custnme = custnme.trim();
    }



    public String getCustalias() {
        return custalias.trim();
    }


    public void setCustalias(String custalias) {
        this.custalias = custalias.trim();
    }


    public String getSbmtddte() {
        return sbmtddte.trim();
    }

    public void setSbmtddte(String sbmtddte) {
        this.sbmtddte = sbmtddte.trim();
    }
}
