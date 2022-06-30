package com.lemonsquare.diserapps.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerResult {

    @JsonProperty("Res")
    private String res;

    public String getRes() {
        return res.trim();
    }

    public void setRes(String res) {
        this.res = res.trim();
    }
}
