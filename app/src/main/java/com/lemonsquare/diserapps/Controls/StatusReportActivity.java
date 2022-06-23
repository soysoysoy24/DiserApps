package com.lemonsquare.diserapps.Controls;

import static com.lemonsquare.diserapps.Constant.toSend;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.material.snackbar.Snackbar;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.DialogControl;
import com.lemonsquare.diserapps.ListAdapter.StatusReportAdapter;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.StatusReportModel;
import com.lemonsquare.diserapps.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatusReportActivity extends AppCompatActivity {

    private ArrayList<CustomerModel> getCustList;
    private ArrayList<StatusReportModel> getStatusList;
    private List<StatusReportModel> getStatsReport = new ArrayList<StatusReportModel>();;
    public ArrayAdapter<String> adapter;
    private DataLogic databaseAccess;
    private StatusReportAdapter statusReportAdapter;

    private Dialog listDialog;
    private View parentLayout;

    private EditText custTxt,rprtCat,dteTxt,tmeTxt;
    private RelativeLayout sbmtBtn;
    private ListView rptView;
    private String Custcd = "";
    private  int mYear, mMonth, mDay,statsID,cstmerin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statusreport);
        databaseAccess = DataLogic.getInstance(this);

        custTxt = (EditText) findViewById(R.id.customerField);
        rprtCat = (EditText) findViewById(R.id.rprtCatField);
        dteTxt = (EditText) findViewById(R.id.dateStatsTxt);
        tmeTxt = (EditText) findViewById(R.id.tmeStatsTxt);
        rptView = (ListView) findViewById(R.id.statsRptView);
        sbmtBtn = (RelativeLayout) findViewById(R.id.sbmtRprtBtn);
        parentLayout = findViewById(android.R.id.content);

        listDialog = new Dialog(this);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));

        rptView.setEmptyView(findViewById(R.id.imageViewNoData));

        custTxt.setFocusable(false);
        rprtCat.setFocusable(false);
        dteTxt.setFocusable(false);
        tmeTxt.setFocusable(false);

        List<CustomerModel> getRaw = databaseAccess.showRaw();
        cstmerin = databaseAccess.getCheckInCnt(getRaw.get(0).getBioId());

        if (cstmerin !=0)
        {
            custTxt.setText(getRaw.get(0).getCustalias());
            custTxt.setEnabled(false);
        }


        if (custTxt.length() != 0)
        {
            ViewData();
        }


        sbmtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cust = custTxt.getText().toString().trim();
                String stats_ = rprtCat.getText().toString().trim();
                String date_ = dteTxt.getText().toString().trim();
                String tme_ = tmeTxt.getText().toString().trim();

                if (TextUtils.isEmpty(cust))
                {
                    DialogControl diag = new DialogControl(StatusReportActivity.this,1,R.drawable.customer,"Please select customer");
                    diag.create();
                }
                else if (TextUtils.isEmpty(stats_))
                {
                    DialogControl diag = new DialogControl(StatusReportActivity.this,1,R.drawable.invoicenum,"Please input invoice");
                    diag.create();
                }
                else if (TextUtils.isEmpty(date_))
                {
                    DialogControl diag = new DialogControl(StatusReportActivity.this,1,R.drawable.calendar,"Please input invoice date");
                    diag.create();
                }
                else if (TextUtils.isEmpty(tme_))
                {
                    DialogControl diag = new DialogControl(StatusReportActivity.this,1,R.drawable.time,"Please input invoice date");
                    diag.create();
                }
                else
                {

                    AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(StatusReportActivity.this);
                    awesomeInfoDialog.setTitle("STATUS REPORT");
                    awesomeInfoDialog.setMessage("Are you sure, want to submit?");
                    awesomeInfoDialog.setDialogIconAndColor(R.drawable.time, R.color.white);
                    awesomeInfoDialog.setColoredCircle(R.color.dialogInfoBackgroundColor);
                    awesomeInfoDialog.setCancelable(true);
                    awesomeInfoDialog.setPositiveButtonText("Yes");
                    awesomeInfoDialog.setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor);
                    awesomeInfoDialog.setPositiveButtonTextColor(R.color.white);
                    awesomeInfoDialog.setNegativeButtonText("No");
                    awesomeInfoDialog.setNegativeButtonbackgroundColor(R.color.dialogErrorBackgroundColor);
                    awesomeInfoDialog.setNegativeButtonTextColor(R.color.white);
                    awesomeInfoDialog.setPositiveButtonClick(new Closure() {
                        @Override
                        public void exec() {
                            List<CustomerModel> getRaw = databaseAccess.showRaw();

                            StatusReportModel stats = new StatusReportModel();
                            stats.setCustcode(Custcd);
                            stats.setStatsID(statsID);
                            stats.setStatsDate(dteTxt.getText().toString());
                            stats.setStatsTme(tmeTxt.getText().toString());
                            stats.setBioId(getRaw.get(0).getBioId());
                            databaseAccess.AddStatsRpt(stats);

                            ViewData();

                            toSend = Custcd+"*"+statsID+"*"+dteTxt.getText().toString()+"*"+tmeTxt.getText().toString()+"*"+getRaw.get(0).getBioId();

                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(getRaw.get(0).getMobile(),null,"SVR " + toSend,null,null);

                            toSend = "";


                            dteTxt.setText("");
                            tmeTxt.setText("");
                            rprtCat.setText("");

                            awesomeInfoDialog.setCancelable(true);

                            DialogControl diag = new DialogControl(StatusReportActivity.this,2,R.drawable.ic_success,"Status report submitted");
                            diag.create();
                        }
                    });

                    awesomeInfoDialog.setNeutralButtonClick(new Closure() {
                        @Override
                        public void exec() {
                            awesomeInfoDialog.setCancelable(true);
                        }
                    });
                    awesomeInfoDialog.show();
                }
            }
        });

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
                        (StatusReportActivity.this, android.R.layout.simple_list_item_1,
                                type);

                custView.setAdapter(adapter);

                custView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        custTxt.setText(String.valueOf(parent.getAdapter().getItem(position)));
                        CustomerModel cstName = new CustomerModel();
                        cstName.setCustalias(String.valueOf(parent.getAdapter().getItem(position)));
                        Custcd = databaseAccess.getCustCd(cstName);
                        ViewData();
                        listDialog.dismiss();
                    }
                });
            }
        });


        rprtCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStatusList = new ArrayList<StatusReportModel>();
                List<StatusReportModel> StatusNme = databaseAccess.showStatsCat();
                for (int stat = 0; stat<=StatusNme.size() -1; stat++)
                {
                    StatusReportModel statsNme = new StatusReportModel();
                    statsNme.setStatscat(StatusNme.get(stat).getStatscat());
                    getStatusList.add(statsNme);
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
                titleView.setText("Choose status category");
                List<String> type = new ArrayList<String>();


                for (int arr = 0; arr<=getStatusList.size()-1; arr++)
                {
                    type.add(getStatusList.get(arr).getStatscat());
                }

                adapter = new ArrayAdapter< String >
                        (StatusReportActivity.this, android.R.layout.simple_list_item_1,
                                type);

                reportViewer.setAdapter(adapter);

                reportViewer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        rprtCat.setText(String.valueOf(parent.getAdapter().getItem(position)));
                        StatusReportModel statname = new StatusReportModel();
                        statname.setStatscat(String.valueOf(parent.getAdapter().getItem(position)));
                        statsID = databaseAccess.getStatusCatID(statname);
                        listDialog.dismiss();
                    }
                });
            }
        });


        dteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                if (dteTxt.length() == 0)
                {
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                }
                else
                {
                    String str =  dteTxt.getText().toString();//InvList.get(position).get(Constant.FIFTH_COLUMN).toString();
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
                                dteTxt.setText((monthOfYear + 1) + "/" + dayOfMonth  + "/" +year);
                                try {
                                    Date date1 =  new SimpleDateFormat("MM/dd/yyyy").parse(dteTxt.getText().toString());
                                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                                    String strDate = formatter.format(date1);
                                    dteTxt.setText(strDate);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });



        tmeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(StatusReportActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String AM_PM = " AM";
                        String mm_precede = "";
                        if (selectedHour >= 12) {
                            AM_PM = " PM";
                            if (selectedHour >=13 && selectedHour < 24) {
                                selectedHour -= 12;
                            }
                            else {
                                selectedHour = 12;
                            }
                        } else if (selectedHour == 0) {
                            selectedHour = 12;
                        }
                        if (minute < 10) {
                            mm_precede = "0";
                        }
                        // Toast.makeText(getApplicationContext(), "" + selectedHour + ":" + mm_precede + minute + AM_PM, Toast.LENGTH_SHORT).show();
                        if (String.valueOf(selectedHour).length() == 1 && String.valueOf(selectedMinute).length() == 1 )
                        {
                            tmeTxt.setText( "0"+selectedHour + ":" + "0"+selectedMinute+" " +AM_PM);
                        }
                        else if (String.valueOf(selectedHour).length() == 2 && String.valueOf(selectedMinute).length() == 1)
                        {
                            tmeTxt.setText( selectedHour + ":" + "0"+selectedMinute+" " +AM_PM);
                        }
                        else if (String.valueOf(selectedHour).length() == 1 && String.valueOf(selectedMinute).length() == 2)
                        {
                            tmeTxt.setText(  "0"+selectedHour + ":" + selectedMinute+" " +AM_PM);
                        }
                        else
                        {
                            tmeTxt.setText( selectedHour + ":" + selectedMinute+" " +AM_PM);
                        }
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

    }

    private void ViewData()
    {
        List<CustomerModel> getRaw = databaseAccess.showRaw();
        StatusReportModel stas = new StatusReportModel();
        stas.setCustcode(Custcd);
        stas.setBioId(getRaw.get(0).getBioId());

        List<StatusReportModel> getStats = databaseAccess.getStatsReport(stas);

        for (int stats = 0; stats <= getStats.size() -1; stats++)
        {
            stas.setCustalias(getStats.get(stats).getCustalias());
            stas.setStatscat(getStats.get(stats).getStatscat());
            stas.setStatsDate(getStats.get(stats).getStatsDate());
            stas.setStatsTme(getStats.get(stats).getStatsTme());
            getStatsReport.add(stas);
        }

        if (getStatsReport.size() != 0)
        {
            statusReportAdapter = new StatusReportAdapter(StatusReportActivity.this,R.layout.layout_statusreportdata,getStatsReport);
            rptView.setAdapter(statusReportAdapter);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            Intent intent = new Intent(StatusReportActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }

}
