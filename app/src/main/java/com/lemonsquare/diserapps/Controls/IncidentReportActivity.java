package com.lemonsquare.diserapps.Controls;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadingview.LoadingView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.DialogControl;
import com.lemonsquare.diserapps.JsonInterface.JsonAPI;
import com.lemonsquare.diserapps.LogIn.LogInActivity;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.IncidentReportModel;
import com.lemonsquare.diserapps.Models.ResultModel;
import com.lemonsquare.diserapps.Models.ServerResult;
import com.lemonsquare.diserapps.Models.StatusReportModel;
import com.lemonsquare.diserapps.R;
import com.mapbox.mapboxsdk.plugins.annotation.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
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

public class IncidentReportActivity extends AppCompatActivity {
    private JsonAPI jsonPlaceHolderApi;
    private List<String> paramArray;

    private ArrayList<CustomerModel> getCustList;
    private List<IncidentReportModel> getIncidentCat;
    public ArrayAdapter<String> adapter;
    private DataLogic databaseAccess;

    private Dialog listDialog,loadingDialog;
    private LoadingView loading;
    private RelativeLayout camBtn,sbmt;

    private EditText custTxt,rprtTxt,reptField;
    private String Custcd,imgString = "";
    private ImageView rptImg,cnclImg;
    private LinearLayout imgLay;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private int reportID,typ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidentreport);
        databaseAccess = DataLogic.getInstance(this);

        custTxt = (EditText) findViewById(R.id.customerField);
        rprtTxt = (EditText) findViewById(R.id.reportFieldTxt);
        camBtn = (RelativeLayout) findViewById(R.id.cameraBtn);
        rptImg = (ImageView) findViewById(R.id.reportImg);
        imgLay = (LinearLayout) findViewById(R.id.imgReportHolder);
        reptField = (EditText) findViewById(R.id.rprtEditTxt);
        cnclImg = (ImageView) findViewById(R.id.cnclImg);
        sbmt = (RelativeLayout) findViewById(R.id.sbmtRprtBtn);

        listDialog = new Dialog(this);
        loadingDialog = new Dialog(this);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));

        custTxt.setFocusable(false);
        rprtTxt.setFocusable(false);

        custTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCustList = new ArrayList<CustomerModel>();
                List<CustomerModel> CheckInCust = databaseAccess.showCustNme();
                for (int cst = 0; cst<=CheckInCust.size() -1; cst++)
                {
                    CustomerModel cstName = new CustomerModel();
                    cstName.setCustalias(CheckInCust.get(cst).getCustalias());
                    getCustList.add(cstName);
                }

                listDialog.setContentView(R.layout.listdialog);
                listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                listDialog.setCanceledOnTouchOutside(false);
                listDialog.setCancelable(true);
                listDialog.show();


                ListView custView = (ListView) listDialog.findViewById(R.id.reportTypView);
                TextView titleView = (TextView) listDialog.findViewById(R.id.typeRprtTxt);
                EditText searchField = (EditText) listDialog.findViewById(R.id.searchTxtField);



                searchField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                titleView.setText("Choose customer");
                List<String> type = new ArrayList<String>();


                for (int arr = 0; arr<=getCustList.size()-1; arr++)
                {
                    type.add(getCustList.get(arr).getCustalias());
                }

                adapter = new ArrayAdapter< String >
                        (IncidentReportActivity.this, android.R.layout.simple_list_item_1,
                                type);

                custView.setAdapter(adapter);

                custView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        custTxt.setText(String.valueOf(parent.getAdapter().getItem(position)));
                        CustomerModel cstName = new CustomerModel();
                        cstName.setCustalias(String.valueOf(parent.getAdapter().getItem(position)));
                        Custcd = databaseAccess.getCustCd(cstName);
                        listDialog.dismiss();
                    }
                });
            }
        });

        rprtTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIncidentCat = new ArrayList<IncidentReportModel>();
                List<IncidentReportModel> ReportNme = databaseAccess.showReportCat();
                for (int stat = 0; stat<=ReportNme.size() -1; stat++)
                {
                    IncidentReportModel repNme = new IncidentReportModel();
                    repNme.setReportcat(ReportNme.get(stat).getReportcat());
                    getIncidentCat.add(repNme);
                }

                listDialog.setContentView(R.layout.listdialog);
                listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                listDialog.setCanceledOnTouchOutside(false);
                listDialog.setCancelable(true);
                listDialog.show();


                ListView reportViewer = (ListView) listDialog.findViewById(R.id.reportTypView);
                TextView titleView = (TextView) listDialog.findViewById(R.id.typeRprtTxt);
                EditText searchField = (EditText) listDialog.findViewById(R.id.searchTxtField);



                searchField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                titleView.setText("Choose report category");
                List<String> type = new ArrayList<String>();


                for (int arr = 0; arr<=getIncidentCat.size()-1; arr++)
                {
                    type.add(getIncidentCat.get(arr).getReportcat());
                }

                adapter = new ArrayAdapter< String >
                        (IncidentReportActivity.this, android.R.layout.simple_list_item_1,
                                type);

                reportViewer.setAdapter(adapter);

                reportViewer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        rprtTxt.setText(String.valueOf(parent.getAdapter().getItem(position)));
                        IncidentReportModel statnme = new IncidentReportModel();
                        statnme.setReportcat(String.valueOf(parent.getAdapter().getItem(position)));
                        reportID = databaseAccess.getReportCatID(statnme);
                        listDialog.dismiss();
                    }
                });
            }
        });

        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });


        cnclImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rptImg.setImageResource(0);
                imgLay.setVisibility(View.GONE);
            }
        });


        sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.setContentView(R.layout.loadinglayout);
                loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.setCancelable(false);

                loading = (LoadingView) loadingDialog.findViewById(R.id.loadingView);
                loadingDialog.show();
                loading.start();

                if (imgString.equals(""))
                {
                    typ = 0;
                }
                else
                {
                    typ = 1;
                }

                SbmitReport();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            rptImg.setImageBitmap(photo);
            GetBase64Img(photo);
            imgLay.setVisibility(View.VISIBLE);
        }
    }

    private void GetBase64Img(Bitmap img)
    {
        imgString = Base64.encodeToString(getBytes(img),
                Base64.NO_WRAP);

    }
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    private void SbmitReport()
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

        List<CustomerModel> getRaw = databaseAccess.showRaw();
        paramArray = new ArrayList<String>();
        paramArray.add(String.valueOf(typ));
        paramArray.add(imgString);
        paramArray.add(Custcd);
        paramArray.add(getRaw.get(0).getBioId());
        paramArray.add(String.valueOf(reportID));
        paramArray.add(reptField.getText().toString());

        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        for( int i=0;i<=paramArray.size()-1;i++) {
            JsonObject internalObject = new JsonObject();
            internalObject.addProperty("data"+i,paramArray.get(i));
            array.add(internalObject);
        }
        jsonObject.addProperty("sp_name","SP_REST_INCNDNT_RPT");
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
                        ServerResult reslt = new ServerResult();
                        for (int res = 0; res<=jo.length()-1;res++)
                        {
                            JSONObject JsnRes = new JSONObject(String.valueOf(jo.getJSONObject(res)));
                            String ress = String.valueOf(JsnRes);
                            ObjectMapper objectMapper = new ObjectMapper();
                            reslt  = objectMapper.readValue(ress,ServerResult.class);

                            DialogControl diag = new DialogControl(IncidentReportActivity.this,2,R.drawable.ic_success,reslt.getRes());
                            diag.create();

                        }

                        loading.stop();
                        loadingDialog.dismiss();

                        custTxt.setText("");
                        rprtTxt.setText("");
                        reptField.setText("");
                        rptImg.setImageResource(0);
                        imgLay.setVisibility(View.GONE);
                        imgString = "";

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

                    DialogControl diag = new DialogControl(IncidentReportActivity.this,5,R.drawable.noconnect,"No data connection!");
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            Intent intent = new Intent(IncidentReportActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }

}
