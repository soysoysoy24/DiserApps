package com.lemonsquare.diserapps.Validation;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadingview.LoadingView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lemonsquare.diserapps.BuildConfig;
import com.lemonsquare.diserapps.Controls.Expense.ExpenseActivity;
import com.lemonsquare.diserapps.Controls.InventoryActivity;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.DialogControl;
import com.lemonsquare.diserapps.JsonInterface.JsonAPI;
import com.lemonsquare.diserapps.LogIn.LogInActivity;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.DiserModel;
import com.lemonsquare.diserapps.Models.MaterialModel;
import com.lemonsquare.diserapps.Models.ResultModel;
import com.lemonsquare.diserapps.Models.ServerResult;
import com.lemonsquare.diserapps.Models.StatusReportModel;
import com.lemonsquare.diserapps.R;
import com.lemonsquare.diserapps.Splashscreen.SplashscreenActivity;

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

public class ValidationActivity extends AppCompatActivity {
    private JsonAPI jsonPlaceHolderApi;
    private List<String> paramArray;
    private DataLogic dataLogic;

    private RelativeLayout validBtn,skpBtn;
    private EditText bioIdTxt,PassTxt;
    private CheckBox chck;
    private Dialog loadingDialog;
    private LoadingView loading;
    private View parentLayout;
    private String Mypassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        dataLogic = DataLogic.getInstance(this);

        validBtn = (RelativeLayout) findViewById(R.id.validateBtn);
        bioIdTxt = (EditText) findViewById(R.id.bioIdField);
        PassTxt = (EditText) findViewById(R.id.passField);
        chck = (CheckBox) findViewById(R.id.chckPass);
        skpBtn = (RelativeLayout) findViewById(R.id.skipBtn);
        parentLayout = findViewById(android.R.id.content);

        loadingDialog = new Dialog(this);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));

        skpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ValidationActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });


        chck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    PassTxt.setVisibility(View.GONE);
                }
                else
                {
                    PassTxt.setVisibility(View.VISIBLE);
                }
            }
        });

        validBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (chck.isChecked())
                {
                    String usr = bioIdTxt.getText().toString().trim();

                    if (TextUtils.isEmpty(usr))
                    {
                        Snackbar.make(parentLayout, "Please input your BIO ID", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else
                    {
                        byte[] encodeValue = android.util.Base64.encode(new StringBuffer(bioIdTxt.getText().toString()).reverse().toString().getBytes(), Base64.DEFAULT);
                        Mypassword = new String(encodeValue).trim();

                        loadingDialog.setContentView(R.layout.loadinglayout);
                        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        loadingDialog.setCanceledOnTouchOutside(false);
                        loadingDialog.setCancelable(false);

                        loading = (LoadingView) loadingDialog.findViewById(R.id.loadingView);
                        loadingDialog.show();
                        loading.start();

                        validate();

                    }
                }
                else
                {
                    String usr = bioIdTxt.getText().toString().trim();
                    String pass = PassTxt.getText().toString().trim();

                    if (TextUtils.isEmpty(usr))
                    {
                        Snackbar.make(parentLayout, "Please input your BIO ID", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else if (TextUtils.isEmpty(pass))
                    {
                        Snackbar.make(parentLayout, "Please input your Password", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else
                    {
                        byte[] encodeValue = android.util.Base64.encode(new StringBuffer(PassTxt.getText().toString()).reverse().toString().getBytes(), Base64.DEFAULT);
                        Mypassword = new String(encodeValue).trim();


                        loadingDialog.setContentView(R.layout.loadinglayout);
                        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        loadingDialog.setCanceledOnTouchOutside(false);
                        loadingDialog.setCancelable(false);

                        loading = (LoadingView) loadingDialog.findViewById(R.id.loadingView);
                        loadingDialog.show();
                        loading.start();

                        validate();
                    }

                }
            }
        });
    }


    private void validate()
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

        paramArray= new ArrayList<String>();
        paramArray.add(bioIdTxt.getText().toString().trim());
        paramArray.add(Mypassword);


        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        for( int i=0;i<=paramArray.size()-1;i++) {
            JsonObject internalObject = new JsonObject();
            internalObject.addProperty("data"+i,paramArray.get(i));
            array.add(internalObject);
        }
        jsonObject.addProperty("sp_name","SP_REST_FOR_VALIDATION");
        jsonObject.add("param",array);


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
                        ServerResult result = new ServerResult();
                        for (int res = 0; res<=jo.length()-1;res++)
                        {
                            JSONObject JsnRes = new JSONObject(String.valueOf(jo.getJSONObject(res)));
                            String ress = String.valueOf(JsnRes);
                            ObjectMapper objectMapper = new ObjectMapper();
                            result  = objectMapper.readValue(ress,ServerResult.class);


                            if (result.getRes().equals("Bio ID not exist"))
                            {
                                loading.stop();
                                loadingDialog.dismiss();

                                DialogControl diag = new DialogControl(ValidationActivity.this,4,R.drawable.ic_dialog_info,result.getRes());
                                diag.create();
                            }
                            else if (result.getRes().equals("Account already validated, skip for this activity"))
                            {
                                loading.stop();
                                loadingDialog.dismiss();

                                skpBtn.setVisibility(View.VISIBLE);

                                DialogControl diag = new DialogControl(ValidationActivity.this,4,R.drawable.ic_dialog_info,result.getRes());
                                diag.create();
                            }
                            else if (result.getRes().equals("Succesfully Validated"))
                            {
                                ForUser();
                            }

                        }

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

                    DialogControl diag = new DialogControl(ValidationActivity.this,5,R.drawable.noconnect,"No data connection!");
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


    private void ForUser()
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

        paramArray= new ArrayList<String>();
        paramArray.add(bioIdTxt.getText().toString().trim());


        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        for( int i=0;i<=paramArray.size()-1;i++) {
            JsonObject internalObject = new JsonObject();
            internalObject.addProperty("data"+i,paramArray.get(i));
            array.add(internalObject);
        }
        jsonObject.addProperty("sp_name","SP_REST_FTCH_DSR_INFO");
        jsonObject.add("param",array);


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
                        DiserModel dser = new DiserModel();
                        for (int res = 0; res<=jo.length()-1;res++)
                        {
                            JSONObject JsnRes = new JSONObject(String.valueOf(jo.getJSONObject(res)));
                            String ress = String.valueOf(JsnRes);
                            ObjectMapper objectMapper = new ObjectMapper();
                            dser  = objectMapper.readValue(ress,DiserModel.class);

                            dser.setBioId(dser.getBioId());
                            dser.setFname(dser.getFname());
                            dser.setMname(dser.getMname());
                            dser.setLname(dser.getLname());
                            dser.setCompny(dser.getCompny());
                            dser.setMobile(dser.getMobile());

                            dataLogic.synchDser(dser);

                        }
                        loading.stop();
                        loadingDialog.dismiss();

                        DialogControl diag = new DialogControl(ValidationActivity.this,2,R.drawable.ic_success,"Successfully Validated");
                        diag.create();

                        Intent intent = new Intent(ValidationActivity.this, LogInActivity.class);
                        startActivity(intent);
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

                    DialogControl diag = new DialogControl(ValidationActivity.this,5,R.drawable.noconnect,"No data connection!");
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
}
