package com.lemonsquare.diserapps.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultModel {

    @SerializedName("result")
    @Expose
    private String reslt;


    public String getReslt() {
        return reslt.trim();
    }

    public void setReslt(String reslt) {
        this.reslt = reslt.trim();
    }
}
