package com.lemonsquare.diserapps.Controls.ViewAttendance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.lemonsquare.diserapps.Controls.IncidentReportActivity;
import com.lemonsquare.diserapps.Controls.InventoryActivity;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.DialogControl;
import com.lemonsquare.diserapps.JsonInterface.JsonAPI;
import com.lemonsquare.diserapps.ListAdapter.AttendanceAdapter;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.InOutModel;
import com.lemonsquare.diserapps.Models.ResultModel;
import com.lemonsquare.diserapps.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class ViewAttendanceActivity  extends AppCompatActivity {
    private JsonAPI jsonPlaceHolderApi;
    private List<String> paramArray;
    private DataLogic databaseAccess;


    private AttendanceAdapter attendanceAdapter;
    private List<InOutModel> getDataInOut = new ArrayList<InOutModel>();
    public ArrayAdapter<String> adapter;
    private ListView attndnceView;

    private Dialog listDialog,loadingDialog;
    private RelativeLayout filter;
    private LoadingView loading;
    private EditText dteFrm,dteTo,flterTxt;
    private int mYear, mMonth, mDay,typ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewattendance);
        databaseAccess = DataLogic.getInstance(this);

        dteFrm = (EditText) findViewById(R.id.frmDteTxt);
        dteTo = (EditText) findViewById(R.id.toDteTxt);
        flterTxt = (EditText) findViewById(R.id.filterType);
        attndnceView = (ListView) findViewById(R.id.attndnceView);
        filter = (RelativeLayout) findViewById(R.id.flterBtn);


        listDialog = new Dialog(this);
        loadingDialog = new Dialog(this);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));


        dteFrm.setFocusable(false);
        dteTo.setFocusable(false);
        flterTxt.setFocusable(false);


        attndnceView.setEmptyView(findViewById(R.id.imageViewNoData));


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.setContentView(R.layout.loadinglayout);
                loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.setCancelable(false);

                loading = (LoadingView) loadingDialog.findViewById(R.id.loadingView);
                loadingDialog.show();
                loading.start();

                getInOut();

            }
        });

        flterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listDialog.setContentView(R.layout.listdialog);
                listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                listDialog.setCanceledOnTouchOutside(false);
                listDialog.setCancelable(true);
                listDialog.show();


                ListView filterView = (ListView) listDialog.findViewById(R.id.reportTypView);
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
                titleView.setText("Filter by");
                List<String> type = new ArrayList<String>();

                type.add("TIME IN/OUT");
                type.add("CHECK IN/OUT");


                adapter = new ArrayAdapter< String >
                        (ViewAttendanceActivity.this, android.R.layout.simple_list_item_1,
                                type);

                filterView.setAdapter(adapter);

                filterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        flterTxt.setText(String.valueOf(parent.getAdapter().getItem(position)));

                        if (String.valueOf(parent.getAdapter().getItem(position)).equals("TIME IN/OUT"))
                        {
                            typ = 1;
                        }
                        else
                        {
                            typ = 2;
                        }

                        listDialog.dismiss();
                    }
                });
            }
        });

        dteFrm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                if (dteFrm.length() == 0)
                {
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                }
                else
                {
                    String str =  dteFrm.getText().toString();//InvList.get(position).get(Constant.FIFTH_COLUMN).toString();
                    String [] checkRt = str.split("/");
                    // mYear = c.get(Calendar.YEAR);
                    for (int a =0; a<=checkRt.length-1; a++)
                    {
                        mMonth = Integer.parseInt(checkRt[0]) -1;
                        mDay = Integer.parseInt(checkRt[1]);
                        mYear = Integer.parseInt(checkRt[2]);
                    }
                }


                DatePickerDialog datePickerDialog = new DatePickerDialog(listDialog.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yyyy");
                            String date = sdf.format(Calendar.getInstance().getTime());
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dteFrm.setText((monthOfYear + 1) + "/" + dayOfMonth  + "/" +year);
                                try {
                                    Date date1 =  new SimpleDateFormat("MM/dd/yyyy").parse(dteFrm.getText().toString());
                                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                                    String strDate = formatter.format(date1);
                                    dteFrm.setText(strDate);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


        dteTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                if (dteTo.length() == 0)
                {
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                }
                else
                {
                    String str =  dteTo.getText().toString();//InvList.get(position).get(Constant.FIFTH_COLUMN).toString();
                    String [] checkRt = str.split("/");
                    // mYear = c.get(Calendar.YEAR);
                    for (int a =0; a<=checkRt.length-1; a++)
                    {
                        mMonth = Integer.parseInt(checkRt[0]) -1;
                        mDay = Integer.parseInt(checkRt[1]);
                        mYear = Integer.parseInt(checkRt[2]);
                    }
                }


                DatePickerDialog datePickerDialog = new DatePickerDialog(listDialog.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yyyy");
                            String date = sdf.format(Calendar.getInstance().getTime());
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dteTo.setText((monthOfYear + 1) + "/" + dayOfMonth  + "/" +year);
                                try {
                                    Date date1 =  new SimpleDateFormat("MM/dd/yyyy").parse(dteTo.getText().toString());
                                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                                    String strDate = formatter.format(date1);
                                    dteTo.setText(strDate);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });

    }


    private void getInOut()
    {
        getDataInOut = new ArrayList<InOutModel>();
        List<CustomerModel> getRaw = databaseAccess.showRaw();
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

        paramArray = new ArrayList<String>();
        paramArray.add(String.valueOf(typ));
        paramArray.add(getRaw.get(0).getBioId());
        paramArray.add(dteFrm.getText().toString());
        paramArray.add(dteTo.getText().toString());



        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        for( int i=0;i<=paramArray.size()-1;i++) {
            JsonObject internalObject = new JsonObject();
            internalObject.addProperty("data"+i,paramArray.get(i));
            array.add(internalObject);
        }
        jsonObject.addProperty("sp_name","REST_FTCH_INOUT");
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
                        InOutModel inout = new InOutModel();

                        for (int res = 0; res<=jo.length()-1;res++)
                        {
                            JSONObject JsnRes = new JSONObject(String.valueOf(jo.getJSONObject(res)));
                            String ress = String.valueOf(JsnRes);
                            ObjectMapper objectMapper = new ObjectMapper();
                            inout  = objectMapper.readValue(ress,InOutModel.class);


                            if (typ == 1)
                            {
                                inout.setDate(inout.getDate());
                                inout.setTmein(inout.getTmein());
                                inout.setLocin(inout.getLocin());
                                inout.setTmeout(inout.getTmeout());
                                inout.setLocout(inout.getLocout());
                                getDataInOut.add(inout);
                            }
                            else
                            {
                                inout.setDate(inout.getDate());
                                inout.setCustalias(inout.getCustalias());
                                inout.setTmein(inout.getTmein());
                                inout.setLocin(inout.getLocin());
                                inout.setTmeout(inout.getTmeout());
                                inout.setLocout(inout.getLocout());
                                getDataInOut.add(inout);
                            }

                        }


                        attendanceAdapter = new AttendanceAdapter(ViewAttendanceActivity.this,R.layout.layout_checinoutdata,getDataInOut,typ);
                        attndnceView.setAdapter(attendanceAdapter);

                        loading.stop();
                        loadingDialog.dismiss();


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

                    DialogControl diag = new DialogControl(ViewAttendanceActivity.this,5,R.drawable.noconnect,"No data connection!");
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
            Intent intent = new Intent(ViewAttendanceActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
}
