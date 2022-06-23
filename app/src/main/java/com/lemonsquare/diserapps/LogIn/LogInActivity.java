package com.lemonsquare.diserapps.LogIn;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadingview.LoadingView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lemonsquare.diserapps.BuildConfig;
import com.lemonsquare.diserapps.Controls.InventoryActivity;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.DialogControl;
import com.lemonsquare.diserapps.JsonInterface.JsonAPI;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.DiserModel;
import com.lemonsquare.diserapps.Models.FilingModel;
import com.lemonsquare.diserapps.Models.IncidentReportModel;
import com.lemonsquare.diserapps.Models.MaterialModel;
import com.lemonsquare.diserapps.Models.ResultModel;
import com.lemonsquare.diserapps.Models.ServerResult;
import com.lemonsquare.diserapps.Models.StatusReportModel;
import com.lemonsquare.diserapps.R;
import com.lemonsquare.diserapps.Splashscreen.SplashscreenActivity;
import com.lemonsquare.diserapps.Validation.ValidationActivity;

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

public class LogInActivity  extends AppCompatActivity {
    private JsonAPI jsonPlaceHolderApi;
    private List<String> paramArray;

    private DataLogic dataLogic;

    private RelativeLayout loginBtn;
    private EditText bioTxt,PassTxt;
    private TextView frgtTxt;

    private int perm;
    private  String[] permitx;
    private final int MULTIPLE_PERMISSIONS=10;

    private Dialog loadingDialog,forgotDialog;
    private LoadingView loading;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            dataLogic = DataLogic.getInstance(this);

            loginBtn = (RelativeLayout) findViewById(R.id.LogInBtn);
            bioTxt = (EditText) findViewById(R.id.bioIdField);
            PassTxt = (EditText) findViewById(R.id.passField);
            frgtTxt = (TextView) findViewById(R.id.cnfrmPassTxt);


            loadingDialog = new Dialog(this);
            forgotDialog = new Dialog(this);

            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));

         /* window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(ContextCompat.getColor(this,R.color.navBar));*/


            permitx = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_WIFI_STATE,
            };

            checkPermissions();

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (perm == 1)
                    {
                        checkPermissions();
                    }
                    else
                    {
                        loadingDialog.setContentView(R.layout.loadinglayout);
                        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        loadingDialog.setCanceledOnTouchOutside(false);
                        loadingDialog.setCancelable(false);

                        loading = (LoadingView) loadingDialog.findViewById(R.id.loadingView);
                        loadingDialog.show();
                        loading.start();


                        String Mypassword;

                        byte[] encodeValue = Base64.encode(new StringBuffer(PassTxt.getText().toString()).reverse().toString().getBytes(), Base64.DEFAULT);
                        Mypassword = new String(encodeValue).trim();
                        getCustomer(bioTxt.getText().toString(),Mypassword);
                    }

                }
            });


            frgtTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LogInActivity.this.getLayoutInflater();
                    final View mDialogView = inflater.inflate(R.layout.requestpass_dialog, null);

                    forgotDialog.setContentView(mDialogView);
                    forgotDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    forgotDialog.setCanceledOnTouchOutside(false);
                    forgotDialog.setCancelable(true);
                    forgotDialog.show();


                    EditText bioid = (EditText) forgotDialog.findViewById(R.id.bioId);
                    EditText passTxt = (EditText) forgotDialog.findViewById(R.id.passTxtField);
                    EditText CnfrmPassTxt = (EditText) forgotDialog.findViewById(R.id.CnfrmPassTxt);
                    RelativeLayout sbmtBtn = (RelativeLayout) forgotDialog.findViewById(R.id.sbmtBtn);


                    passTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if(passTxt.length() == 0)
                            {
                                passTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                CnfrmPassTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                sbmtBtn.setBackgroundResource(R.drawable.effect_btn_otp_off);
                                sbmtBtn.setEnabled(true);
                                sbmtBtn.setClickable(true);
                            }
                            else
                            {
                                if (passTxt.getText().toString().equals(CnfrmPassTxt.getText().toString()))
                                {

                                    passTxt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.check, 0);
                                    CnfrmPassTxt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.check, 0);
                                    sbmtBtn.setBackgroundResource(R.drawable.effect_accept_btn);
                                    sbmtBtn.setEnabled(true);
                                    sbmtBtn.setClickable(true);
                                }
                                else if (!passTxt.getText().equals(CnfrmPassTxt.getText().toString()))
                                {
                                    if (CnfrmPassTxt.length()== 0)
                                    {
                                        passTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                        sbmtBtn.setBackgroundResource(R.drawable.effect_btn_otp_off);
                                        sbmtBtn.setEnabled(true);
                                        sbmtBtn.setClickable(true);
                                    }
                                    else
                                    {
                                        passTxt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.cancel, 0);
                                        CnfrmPassTxt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.cancel, 0);
                                        sbmtBtn.setBackgroundResource(R.drawable.effect_btn_otp_off);
                                        sbmtBtn.setEnabled(true);
                                        sbmtBtn.setClickable(true);
                                    }


                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });


                    CnfrmPassTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            if (CnfrmPassTxt.length() == 0 )
                            {
                                CnfrmPassTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                passTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                sbmtBtn.setBackgroundResource(R.drawable.effect_btn_otp_off);
                                sbmtBtn.setEnabled(true);
                                sbmtBtn.setClickable(true);
                            }

                            else
                            {
                                if (CnfrmPassTxt.getText().toString().equals(passTxt.getText().toString()))
                                {

                                    passTxt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.check, 0);
                                    CnfrmPassTxt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.check, 0);
                                    sbmtBtn.setBackgroundResource(R.drawable.effect_accept_btn);
                                    sbmtBtn.setEnabled(true);
                                    sbmtBtn.setClickable(true);

                                }
                                else if (!CnfrmPassTxt.getText().equals(passTxt.getText().toString()))
                                {

                                    if (passTxt.length()== 0)
                                    {
                                        CnfrmPassTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                        sbmtBtn.setBackgroundResource(R.drawable.effect_btn_otp_off);
                                        sbmtBtn.setEnabled(true);
                                        sbmtBtn.setClickable(true);
                                    }
                                    else
                                    {
                                        CnfrmPassTxt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.cancel, 0);
                                        passTxt.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.cancel, 0);
                                        sbmtBtn.setBackgroundResource(R.drawable.effect_btn_otp_off);
                                        sbmtBtn.setEnabled(true);
                                        sbmtBtn.setClickable(true);
                                    }
                                }
                            }


                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });





                    sbmtBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newpass = "";
                            byte[] encodeValue = android.util.Base64.encode(new StringBuffer(CnfrmPassTxt.getText().toString()).reverse().toString().getBytes(), Base64.DEFAULT);
                            newpass = new String(encodeValue).trim();

                            loadingDialog.setContentView(R.layout.loadinglayout);
                            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            loadingDialog.setCanceledOnTouchOutside(false);
                            loadingDialog.setCancelable(false);

                            loading = (LoadingView) loadingDialog.findViewById(R.id.loadingView);
                            loadingDialog.show();
                            loading.start();


                            saveNewPass(bioid.getText().toString(),newpass);

                        }
                    });

                }
            });

       }


    private void saveNewPass(String bio_id,String pass)
    {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
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


        paramArray = new ArrayList<String>();
        paramArray.add(bio_id);
        paramArray.add(pass);

        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        for( int i=0;i<=paramArray.size()-1;i++) {
            JsonObject internalObject = new JsonObject();
            internalObject.addProperty("data"+i,paramArray.get(i));
            array.add(internalObject);
        }
        jsonObject.addProperty("sp_name","SP_REST_FRGT_PASS");
        jsonObject.add("param",array);

        System.out.println(jsonObject);


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
                        ServerResult resMod = new ServerResult();
                        for (int res = 0; res<=jo.length()-1;res++)
                        {
                            JSONObject JsnRes = new JSONObject(String.valueOf(jo.getJSONObject(res)));
                            String ress = String.valueOf(JsnRes);
                            ObjectMapper objectMapper = new ObjectMapper();
                            resMod  = objectMapper.readValue(ress,ServerResult.class);

                            if (resMod.getRes().equals("Password has successfully change"))
                            {
                                loading.stop();
                                loadingDialog.dismiss();


                                DialogControl diag = new DialogControl(LogInActivity.this,2,R.drawable.ic_success,resMod.getRes());
                                diag.create();
                            }

                        }

                    }
                } catch (JSONException e) {
                    //  e.printStackTrace();
                    loadingDialog.dismiss();
                    loading.stop();

                } catch (JsonMappingException e) {
                    // e.printStackTrace();
                    loadingDialog.dismiss();
                    loading.stop();

                } catch (JsonProcessingException e) {
                    //  e.printStackTrace();
                    loadingDialog.dismiss();
                    loading.stop();

                }
            }
            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                if (t instanceof IOException) {
                /*    Toast.makeText(LogInActivity.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary*/

                    DialogControl diag = new DialogControl(LogInActivity.this,5,R.drawable.noconnect,"No data connection!");
                    diag.create();

                    loadingDialog.dismiss();
                    loading.stop();


                }
                else {
                   /* Toast.makeText(LogInActivity.this, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service*/

                    loadingDialog.dismiss();
                    loading.stop();


                }
            }
        });
    }

    private void getCustomer(String usr, String pass)
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
                .baseUrl("https://lsbizportal.lemonsquare.com.ph/testportal/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi  = retrofit.create(JsonAPI.class);

        paramArray = new ArrayList<String>();
        paramArray.add(usr);
        paramArray.add(pass);

        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        for( int i=0;i<=paramArray.size()-1;i++) {
            JsonObject internalObject = new JsonObject();
            internalObject.addProperty("data"+i,paramArray.get(i));
            array.add(internalObject);
        }
        jsonObject.addProperty("sp_name","SP_REST_LOGIN");
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
                        CustomerModel cstModel = new CustomerModel();
                        for (int res = 0; res<=jo.length()-1;res++)
                        {
                            JSONObject JsnRes = new JSONObject(String.valueOf(jo.getJSONObject(res)));
                            String ress = String.valueOf(JsnRes);
                            ObjectMapper objectMapper = new ObjectMapper();
                            cstModel  = objectMapper.readValue(ress,CustomerModel.class);

                            if (cstModel.getResultSet().equals("Invalid Credentials"))
                            {
                                loading.stop();
                                loadingDialog.dismiss();

                                DialogControl diag = new DialogControl(LogInActivity.this,1,R.drawable.ic_dialog_warning,"Invalid Credentials");
                                diag.create();

                                return;
                            }
                            else if (cstModel.getResultSet().equals("Please reset the account"))
                            {
                                loading.stop();
                                loadingDialog.dismiss();

                                DialogControl diag = new DialogControl(LogInActivity.this,1,R.drawable.ic_dialog_info,"Please reset the account");
                                diag.create();

                                return;
                            }
                            else if (cstModel.getResultSet().equals("Successfully Login"))
                            {
                                cstModel.setCustcode(cstModel.getCustcode());
                                cstModel.setCustnme(cstModel.getCustnme());
                                cstModel.setCustalias(cstModel.getCustalias());

                                dataLogic.synchCust(cstModel);

                                cstModel.setBioId(cstModel.getBioId());
                                cstModel.setFname(cstModel.getFname());
                                cstModel.setMname(cstModel.getMname());
                                cstModel.setLname(cstModel.getLname());
                                cstModel.setCompny(cstModel.getCompny());
                                cstModel.setMobile(cstModel.getMobile());

                                dataLogic.synchDser(cstModel);
                            }

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

                    DialogControl diag = new DialogControl(LogInActivity.this,5,R.drawable.noconnect,"No data connection!");
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

                            dataLogic.synchMats(matmodel);
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

                    DialogControl diag = new DialogControl(LogInActivity.this,5,R.drawable.noconnect,"No data connection!");
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
                        for (int res = 0; res<=jo.length()-1;res++)
                        {
                            JSONObject JsnRes = new JSONObject(String.valueOf(jo.getJSONObject(res)));
                            String ress = String.valueOf(JsnRes);
                            ObjectMapper objectMapper = new ObjectMapper();
                            stats  = objectMapper.readValue(ress,StatusReportModel.class);

                            stats.setStatscat(stats.getStatscat());
                            dataLogic.synchStatCat(stats);


                            stats.setReportcat(stats.getReportcat());
                            dataLogic.synchReportCat(stats);


                            stats.setFilingcat(stats.getFilingcat());
                            dataLogic.synchFilingCat(stats);


                            stats.setDispcat(stats.getDispcat());
                            dataLogic.synchDispCat(stats);

                        }

                        loading.stop();
                        loadingDialog.dismiss();


                        DiserModel dsr = new DiserModel();
                        dsr.setBioId(bioTxt.getText().toString());
                        dataLogic.UpdateRaw(dsr);

                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
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
                if (t instanceof IOException)
                {
                    loading.stop();
                    loadingDialog.dismiss();

                    DialogControl diag = new DialogControl(LogInActivity.this,5,R.drawable.noconnect,"No data connection!");
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

    private  boolean checkPermissions() {

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permitx) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
                perm = 1;
            }
            else
            {
                perm = 0;
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
