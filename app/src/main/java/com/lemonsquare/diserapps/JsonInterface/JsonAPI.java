package com.lemonsquare.diserapps.JsonInterface;

import com.google.gson.JsonObject;
import com.lemonsquare.diserapps.Models.ResultModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface JsonAPI {

    @GET("diser/SP_REST_FTCH_MATS")
    Call<ResultModel> getListMaterials();


    @GET("diser/SP_REST_FTCH_CATCLSS")
    Call<ResultModel> getStatsCategory();


    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("diser")
    Call<ResultModel> createPosts(@Body JsonObject jsonObject);
}
