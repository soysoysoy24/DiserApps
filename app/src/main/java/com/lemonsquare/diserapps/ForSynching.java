package com.lemonsquare.diserapps;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadingview.LoadingView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lemonsquare.diserapps.Controls.DeliveryActivity;
import com.lemonsquare.diserapps.Controls.ViewAttendance.ViewAttendanceActivity;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.JsonInterface.JsonAPI;
import com.lemonsquare.diserapps.LogIn.LogInActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.DiserModel;
import com.lemonsquare.diserapps.Models.ExpenseModel;
import com.lemonsquare.diserapps.Models.MaterialModel;
import com.lemonsquare.diserapps.Models.ResultModel;
import com.lemonsquare.diserapps.Models.StatusReportModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ForSynching {
    private JsonAPI jsonPlaceHolderApi;
    private List<String> paramArray;
    private Context context;
    private Dialog loadingDialog;
    private LoadingView loading;
    private DataLogic databaseAccess;
    private int resource;

    public ForSynching(Context context ,JsonAPI jsonPlaceHolderApi, List<String> paramArray,int resource) {
        this.jsonPlaceHolderApi = jsonPlaceHolderApi;
        this.paramArray = paramArray;
        this.context = context;
        this.resource = resource;

        getCust();
    }

    private void getCust()
    {
        databaseAccess = DataLogic.getInstance(context);

        databaseAccess.DeleteMaintainTable();

        loadingDialog = new Dialog(context);

        loadingDialog.setContentView(resource);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);

        loading = (LoadingView) loadingDialog.findViewById(R.id.loadingView);


        loadingDialog.show();
        loading.start();



        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit  = new Retrofit.Builder()
                .baseUrl("https://lsbizportal.lemonsquare.com.ph/testportal/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi  = retrofit.create(JsonAPI.class);

/*        paramArray = new ArrayList<String>();
        paramArray.add(usr);*/

        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        for( int i=0;i<=paramArray.size()-1;i++) {
            JsonObject internalObject = new JsonObject();
            internalObject.addProperty("data"+i,paramArray.get(i));
            array.add(internalObject);
        }
        jsonObject.addProperty("sp_name","SP_REST_FTCH_CUST_DSER");
        jsonObject.add("param",array);

        System.out.println("Test");

        Call<ResultModel> userCall = jsonPlaceHolderApi.createPosts(jsonObject);
        userCall.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                try {
                    if (!response.isSuccessful())
                    {
                        loading.stop();
                        loadingDialog.dismiss();
                    }
                    else
                    {
                        String resultJSON = response.body().getReslt();
                        JSONArray jo = new JSONArray(resultJSON);
                        CustomerModel cstModel = new CustomerModel();
                        for (int res = 0; res<=jo.length()-1;res++)
                        {
                            JSONObject JsnRes = new JSONObject(String.valueOf(jo.getJSONObject(res)));
                            String ress = String.valueOf(JsnRes);
                            ObjectMapper objectMapper = new ObjectMapper();
                            cstModel  = objectMapper.readValue(ress,CustomerModel.class);

                            cstModel.setCustcode(cstModel.getCustcode());
                            cstModel.setCustnme(cstModel.getCustnme());
                            cstModel.setCustalias(cstModel.getCustalias());

                            databaseAccess.synchCust(cstModel);


                        }
                       getMaterials();

                    }

                } catch (JSONException e) {
                    // e.printStackTrace();
                    loading.stop();
                    loadingDialog.dismiss();

                } catch (JsonMappingException e) {
                    //  e.printStackTrace();
                    loading.stop();
                    loadingDialog.dismiss();


                } catch (JsonProcessingException e) {
                    // e.printStackTrace();
                    loading.stop();
                    loadingDialog.dismiss();

                }
            }
            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                if (t instanceof IOException) {
                    loading.stop();
                    loadingDialog.dismiss();

                    DialogControl diag = new DialogControl(context,5,R.drawable.noconnect,"No data connection!");
                    diag.create();
                }
                else {
                    // todo log to some central bug tracking service*/

                    loading.stop();
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void getMaterials()
    {

        //OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        //List<LinkModel> getActvLnk = dataRawOffline.getLinkActv();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit  = new Retrofit.Builder()
                .baseUrl("https://lsbizportal.lemonsquare.com.ph/portal/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi  = retrofit.create(JsonAPI.class);

        Call<ResultModel> userCall = jsonPlaceHolderApi.getListMaterials();
        userCall.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                try {
                    if (!response.isSuccessful())
                    {
                        loading.stop();
                        loadingDialog.dismiss();
                    }
                    else
                    {
                        String resultJSON = response.body().getReslt();
                        JSONArray jo = new JSONArray(resultJSON);
                        MaterialModel matmodel = new MaterialModel();
                        for (int res = 0; res<=jo.length()-1;res++)
                        {
                            JSONObject JsnRes = new JSONObject(String.valueOf(jo.getJSONObject(res)));
                            String ress = String.valueOf(JsnRes);
                            ObjectMapper objectMapper = new ObjectMapper();
                            matmodel  = objectMapper.readValue(ress,MaterialModel.class);

                            matmodel.setMatcd(matmodel.getMatcd());
                            matmodel.setMatnme(matmodel.getMatnme());
                            matmodel.setUom(matmodel.getUom());
                            matmodel.setExtmat(matmodel.getExtmat());

                            databaseAccess.synchMats(matmodel);
                        }

                        getStatsCat();

                    }

                } catch (JSONException e) {
                    // e.printStackTrace();
                    loading.stop();
                    loadingDialog.dismiss();

                } catch (JsonMappingException e) {
                    //  e.printStackTrace();
                    loading.stop();
                    loadingDialog.dismiss();


                } catch (JsonProcessingException e) {
                    // e.printStackTrace();
                    loading.stop();
                    loadingDialog.dismiss();

                }
            }
            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                if (t instanceof IOException) {
                    loading.stop();
                    loadingDialog.dismiss();

                    DialogControl diag = new DialogControl(context,5,R.drawable.noconnect,"No data connection!");
                    diag.create();
                }
                else {
                    // todo log to some central bug tracking service*/
                    loading.stop();
                    loadingDialog.dismiss();
                }
            }
        });
    }

    private void getStatsCat()
    {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .writeTimeout(40, TimeUnit.SECONDS)
                .build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit  = new Retrofit.Builder()
                .baseUrl("https://lsbizportal.lemonsquare.com.ph/portal/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi  = retrofit.create(JsonAPI.class);

        Call<ResultModel> userCall = jsonPlaceHolderApi.getStatsCategory();
        userCall.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                try {
                    if (!response.isSuccessful())
                    {
                        loading.stop();
                        loadingDialog.dismiss();
                    }
                    else
                    {
                        String resultJSON = response.body().getReslt();
                        JSONArray jo = new JSONArray(resultJSON);
                        StatusReportModel stats = new StatusReportModel();
                        ExpenseModel statsExpnse = new ExpenseModel();
                        for (int res = 0; res<=jo.length()-1;res++)
                        {
                            JSONObject JsnRes = new JSONObject(String.valueOf(jo.getJSONObject(res)));
                            String ress = String.valueOf(JsnRes);
                            ObjectMapper objectMapper = new ObjectMapper();
                            stats  = objectMapper.readValue(ress,StatusReportModel.class);
                            statsExpnse = objectMapper.readValue(ress, ExpenseModel.class);


                            stats.setStatscat(stats.getStatscat());
                            databaseAccess.synchStatCat(stats);


                            stats.setReportcat(stats.getReportcat());
                            databaseAccess.synchReportCat(stats);


                            stats.setFilingcat(stats.getFilingcat());
                            databaseAccess.synchFilingCat(stats);



                            stats.setDispcat(stats.getDispcat());
                            databaseAccess.synchDispCat(stats);


                            statsExpnse.setXpnseDesc(statsExpnse.getXpnseDesc());
                            databaseAccess.synchXpenseCat(statsExpnse);


                            statsExpnse.setXpnseTranDesc(statsExpnse.getXpnseTranDesc());
                            databaseAccess.synchXpenseTranspoCat(statsExpnse);

                        }

                        loading.stop();
                        loadingDialog.dismiss();

                        DialogControl diag = new DialogControl(context,2,R.drawable.ic_success,"Synching success");
                        diag.create();

                    }

                } catch (JSONException e) {
                    // e.printStackTrace();
                    loading.stop();
                    loadingDialog.dismiss();

                } catch (JsonMappingException e) {
                    //  e.printStackTrace();
                    loading.stop();
                    loadingDialog.dismiss();


                } catch (JsonProcessingException e) {
                    // e.printStackTrace();
                    loading.stop();
                    loadingDialog.dismiss();

                }
            }
            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                if (t instanceof IOException)
                {
                    loading.stop();
                    loadingDialog.dismiss();

                    DialogControl diag = new DialogControl(context,5,R.drawable.noconnect,"No data connection!");
                    diag.create();
                }
                else
                {
                    // todo log to some central bug tracking service*/
                    loading.stop();
                    loadingDialog.dismiss();
                }
            }
        });
    }
}
