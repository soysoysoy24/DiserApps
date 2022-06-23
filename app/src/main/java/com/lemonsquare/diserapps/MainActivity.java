package com.lemonsquare.diserapps;

import static android.content.ContentValues.TAG;

import static com.lemonsquare.diserapps.Constant.locate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeWarningDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lemonsquare.diserapps.Controls.AbisActivity;
import com.lemonsquare.diserapps.Controls.DeliveryActivity;
import com.lemonsquare.diserapps.Controls.Expense.ExpenseActivity;
import com.lemonsquare.diserapps.Controls.FilingActivity;
import com.lemonsquare.diserapps.Controls.IncidentReportActivity;
import com.lemonsquare.diserapps.Controls.InventoryActivity;
import com.lemonsquare.diserapps.Controls.PopMaterial.PopActivity;
import com.lemonsquare.diserapps.Controls.PriceSurveyAcitivity;
import com.lemonsquare.diserapps.Controls.StatusReportActivity;
import com.lemonsquare.diserapps.Controls.ViewAttendance.ViewAttendanceActivity;
import com.lemonsquare.diserapps.Controls.ViewLogs.ViewLogsActivity;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.JsonInterface.JsonAPI;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.DiserModel;
import com.lemonsquare.diserapps.Models.FilingModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private JsonAPI jsonPlaceHolderApi;
    private List<String> paramArray;


    private RelativeLayout checkIn,timeIn,timeOut,invBtn,delBtn,absBtn,popMatsBtn,prcSrvyBtn,IncdntBtn,StatsRptBtn,XpnsBtn,LogsBtn,attndnceBtn;
    private LinearLayout settingsBtn;
    private TextView fnametxt,initialtxt;
    private DataLogic databaseAccess;
    private String location = "0.0,0.0",Custcd = " ";
    public ArrayAdapter<String> adapter;
    private ArrayList<CustomerModel> getCustList;

    private Location mCurrentLocation;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Boolean mRequestingLocationUpdates;

    private Dialog listDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseAccess = DataLogic.getInstance(this);


        checkIn = (RelativeLayout) findViewById(R.id.CheckInBtn);
        timeIn = (RelativeLayout) findViewById(R.id.tmeInBtn);
        timeOut = (RelativeLayout) findViewById(R.id.tmeOutBtn);
        invBtn = (RelativeLayout) findViewById(R.id.invBtn);
        delBtn = (RelativeLayout) findViewById(R.id.delBtn);
        absBtn = (RelativeLayout) findViewById(R.id.absBtn);
        prcSrvyBtn = (RelativeLayout) findViewById(R.id.prceSrveyBtn);
        popMatsBtn = (RelativeLayout) findViewById(R.id.popMatsBtn);
        IncdntBtn = (RelativeLayout) findViewById(R.id.incdntRprt);
        StatsRptBtn = (RelativeLayout) findViewById(R.id.statsRprtBtn);
        XpnsBtn = (RelativeLayout) findViewById(R.id.expnseBtn);
        LogsBtn = (RelativeLayout) findViewById(R.id.viewLogsBtn);
        attndnceBtn = (RelativeLayout) findViewById(R.id.viewAttndnceBtn);
        settingsBtn = (LinearLayout) findViewById(R.id.settingsBtn);
        initialtxt = (TextView) findViewById(R.id.initialTxt);
        fnametxt = (TextView) findViewById(R.id.fnmeTxt);

        listDialog = new Dialog(this);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));

        Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
        startService(serviceIntent);

        init();


        List<DiserModel> getInfo = databaseAccess.showDiserInfo();

        fnametxt.setText(getInfo.get(0).getFname());
        initialtxt.setText(getInfo.get(0).getFname().substring(0,1));

        timeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGpsEnabled())
                {
                    AwesomeWarningDialog awesomeWarningDialog = new AwesomeWarningDialog(MainActivity.this);
                    awesomeWarningDialog.setTitle("LOCATION");
                    awesomeWarningDialog.setMessage("Location must be enable");
                    awesomeWarningDialog.setColoredCircle(R.color.dialogWarningBackgroundColor);
                    awesomeWarningDialog.setDialogIconOnly(R.drawable.location);
                    awesomeWarningDialog.setCancelable(true);
                    awesomeWarningDialog.setButtonText("Ok");
                    awesomeWarningDialog.setButtonBackgroundColor(R.color.dialogWarningBackgroundColor);
                    awesomeWarningDialog.setButtonTextColor(R.color.white);
                    awesomeWarningDialog.setWarningButtonClick(new Closure() {
                        @Override
                        public void exec() {
                            awesomeWarningDialog.setCancelable(true);
                        }
                    });
                    awesomeWarningDialog.show();
                }
                else
                {
                    int Cnter,CnterAttndnce;
                    List<CustomerModel> getRaw = databaseAccess.showRaw();
                    DiserModel dserAttndnc = new DiserModel();
                    dserAttndnc.setLoc(locate);
                    dserAttndnc.setBioId(getRaw.get(0).getBioId());

                    Cnter  =   databaseAccess.checkAttndnce(dserAttndnc);
                    CnterAttndnce = databaseAccess.checkInOutAttndnce(dserAttndnc);

                    if (CnterAttndnce == 1)
                    {
                        AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(MainActivity.this);
                        awesomeInfoDialog.setTitle("ATTENDANCE");
                        awesomeInfoDialog.setMessage("You are already complete your attendance");
                        awesomeInfoDialog.setDialogIconAndColor(R.drawable.time, R.color.white);
                        awesomeInfoDialog.setColoredCircle(R.color.dialogInfoBackgroundColor);
                        awesomeInfoDialog.setCancelable(true);
                        awesomeInfoDialog.setNeutralButtonText("Ok");
                        awesomeInfoDialog.setNeutralButtonbackgroundColor(R.color.dialogInfoBackgroundColor);
                        awesomeInfoDialog.setNegativeButtonTextColor(R.color.white);
                        awesomeInfoDialog.setNeutralButtonClick(new Closure() {
                            @Override
                            public void exec() {
                                awesomeInfoDialog.setCancelable(true);
                            }
                        });
                        awesomeInfoDialog.show();
                    }
                    else if (CnterAttndnce >= 2)
                    {
                        AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(MainActivity.this);
                        awesomeInfoDialog.setTitle("ATTENDANCE");
                        awesomeInfoDialog.setMessage("You have a pending time out");
                        awesomeInfoDialog.setDialogIconAndColor(R.drawable.time, R.color.white);
                        awesomeInfoDialog.setColoredCircle(R.color.dialogInfoBackgroundColor);
                        awesomeInfoDialog.setCancelable(true);
                        awesomeInfoDialog.setNeutralButtonText("Ok");
                        awesomeInfoDialog.setNeutralButtonbackgroundColor(R.color.dialogInfoBackgroundColor);
                        awesomeInfoDialog.setNegativeButtonTextColor(R.color.white);
                        awesomeInfoDialog.setNeutralButtonClick(new Closure() {
                            @Override
                            public void exec() {
                                awesomeInfoDialog.setCancelable(true);
                            }
                        });
                        awesomeInfoDialog.show();
                    }
                    else
                    {
                        if (Cnter == 1)
                        {
                            AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(MainActivity.this);
                            awesomeInfoDialog.setTitle("ATTENDANCE");
                            awesomeInfoDialog.setMessage("You are already time in");
                            awesomeInfoDialog.setDialogIconAndColor(R.drawable.time, R.color.white);
                            awesomeInfoDialog.setColoredCircle(R.color.dialogInfoBackgroundColor);
                            awesomeInfoDialog.setCancelable(true);
                            awesomeInfoDialog.setNeutralButtonText("Ok");
                            awesomeInfoDialog.setNeutralButtonbackgroundColor(R.color.dialogInfoBackgroundColor);
                            awesomeInfoDialog.setNegativeButtonTextColor(R.color.white);
                            awesomeInfoDialog.setNeutralButtonClick(new Closure() {
                                @Override
                                public void exec() {
                                    awesomeInfoDialog.setCancelable(true);
                                }
                            });
                            awesomeInfoDialog.show();
                        }
                        else
                        {
                            AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(MainActivity.this);
                            awesomeInfoDialog.setTitle("ATTENDANCE");
                            awesomeInfoDialog.setMessage("Are you sure, want to time in ?");
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
                                    databaseAccess.InsertAttndnce(dserAttndnc);
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(getRaw.get(0).getMobile(),null,"ATTNDNCE " + dserAttndnc.getBioId() +"*"+ dserAttndnc.getLoc(),null,null);

                                    awesomeInfoDialog.setCancelable(true);

                                    DialogControl diag = new DialogControl(MainActivity.this,2,R.drawable.time,"Successfully Time in");
                                    diag.create();
                                }
                            });

                            awesomeInfoDialog.setNegativeButtonClick(new Closure() {
                                @Override
                                public void exec() {
                                    awesomeInfoDialog.setCancelable(true);
                                }
                            });
                            awesomeInfoDialog.show();
                        }
                    }
                }
            }
        });


        timeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isGpsEnabled())
                {
                    AwesomeWarningDialog awesomeWarningDialog = new AwesomeWarningDialog(MainActivity.this);
                    awesomeWarningDialog.setTitle("WARNING");
                    awesomeWarningDialog.setMessage("Location must be enable");
                    awesomeWarningDialog.setColoredCircle(R.color.dialogWarningBackgroundColor);
                    awesomeWarningDialog.setDialogIconOnly(R.drawable.location);
                    awesomeWarningDialog.setCancelable(true);
                    awesomeWarningDialog.setButtonText("Ok");
                    awesomeWarningDialog.setButtonBackgroundColor(R.color.dialogWarningBackgroundColor);
                    awesomeWarningDialog.setButtonTextColor(R.color.white);
                    awesomeWarningDialog.setWarningButtonClick(new Closure() {
                        @Override
                        public void exec() {
                            awesomeWarningDialog.setCancelable(true);
                        }
                    });
                    awesomeWarningDialog.show();
                }
                else
                {
                    int Cnter,CnterAttndnce;
                    List<CustomerModel> getRaw = databaseAccess.showRaw();
                    DiserModel dserAttndnc = new DiserModel();
                    dserAttndnc.setLoc(locate);
                    dserAttndnc.setBioId(getRaw.get(0).getBioId());

                    Cnter  =   databaseAccess.checkAttndnce(dserAttndnc);
                    CnterAttndnce = databaseAccess.checkInOutAttndnce(dserAttndnc);


                    if (CnterAttndnce == 1)
                    {
                        AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(MainActivity.this);
                        awesomeInfoDialog.setTitle("ATTENDANCE");
                        awesomeInfoDialog.setMessage("You are already complete your attendance");
                        awesomeInfoDialog.setDialogIconAndColor(R.drawable.time, R.color.white);
                        awesomeInfoDialog.setColoredCircle(R.color.dialogInfoBackgroundColor);
                        awesomeInfoDialog.setCancelable(true);
                        awesomeInfoDialog.setNeutralButtonText("Ok");
                        awesomeInfoDialog.setNeutralButtonbackgroundColor(R.color.dialogInfoBackgroundColor);
                        awesomeInfoDialog.setNegativeButtonTextColor(R.color.white);
                        awesomeInfoDialog.setNeutralButtonClick(new Closure() {
                            @Override
                            public void exec() {
                                awesomeInfoDialog.setCancelable(true);
                            }
                        });
                        awesomeInfoDialog.show();
                    }
                    else
                    {
                        if (Cnter == 0)
                        {
                            AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(MainActivity.this);
                            awesomeInfoDialog.setTitle("ATTENDANCE");
                            awesomeInfoDialog.setMessage("Please time in first");
                            awesomeInfoDialog.setDialogIconAndColor(R.drawable.time, R.color.white);
                            awesomeInfoDialog.setColoredCircle(R.color.dialogInfoBackgroundColor);
                            awesomeInfoDialog.setCancelable(true);
                            awesomeInfoDialog.setNeutralButtonText("Ok");
                            awesomeInfoDialog.setNeutralButtonbackgroundColor(R.color.dialogInfoBackgroundColor);
                            awesomeInfoDialog.setNegativeButtonTextColor(R.color.white);
                            awesomeInfoDialog.setNeutralButtonClick(new Closure() {
                                @Override
                                public void exec() {
                                    awesomeInfoDialog.setCancelable(true);
                                }
                            });
                            awesomeInfoDialog.show();
                        }
                        else
                        {
                            AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(MainActivity.this);
                            awesomeInfoDialog.setTitle("ATTENDANCE");
                            awesomeInfoDialog.setMessage("Are you sure, want to time out ?");
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
                                    databaseAccess.InsertAttndnce(dserAttndnc);
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(getRaw.get(0).getMobile(),null,"ATTNDNCE " + dserAttndnc.getBioId() +"*"+ dserAttndnc.getLoc(),null,null);
                                    awesomeInfoDialog.setCancelable(true);

                                    DialogControl diag = new DialogControl(MainActivity.this,2,R.drawable.time,"Successfully Time out");
                                    diag.create();
                                }
                            });

                            awesomeInfoDialog.setNegativeButtonClick(new Closure() {
                                @Override
                                public void exec() {
                                    awesomeInfoDialog.setCancelable(true);
                                }
                            });
                            awesomeInfoDialog.show();
                        }
                    }

                }
            }
        });

        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Cnter;
                List<CustomerModel> getRaw = databaseAccess.showRaw();
                DiserModel dserAttndnc = new DiserModel();
                dserAttndnc.setBioId(getRaw.get(0).getBioId());
                Cnter  =   databaseAccess.checkAttndnce(dserAttndnc);

     /*           if (Cnter == 0)
                {
                    AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(MainActivity.this);
                    awesomeInfoDialog.setTitle("CHECK IN");
                    awesomeInfoDialog.setMessage("Please check attendance first");
                    awesomeInfoDialog.setDialogIconAndColor(R.drawable.time, R.color.white);
                    awesomeInfoDialog.setColoredCircle(R.color.dialogInfoBackgroundColor);
                    awesomeInfoDialog.setCancelable(true);
                    awesomeInfoDialog.setNeutralButtonText("Ok");
                    awesomeInfoDialog.setNeutralButtonbackgroundColor(R.color.dialogInfoBackgroundColor);
                    awesomeInfoDialog.setNegativeButtonTextColor(R.color.white);
                    awesomeInfoDialog.setNeutralButtonClick(new Closure() {
                        @Override
                        public void exec() {
                            awesomeInfoDialog.setCancelable(true);
                        }
                    });
                    awesomeInfoDialog.show();
                }
                else
                {*/
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
                    titleView.setText("Check in at");
                    List<String> type = new ArrayList<String>();


                    for (int arr = 0; arr<=getCustList.size()-1; arr++)
                    {
                        type.add(getCustList.get(arr).getCustalias());
                    }

                    adapter = new ArrayAdapter< String >
                            (MainActivity.this, android.R.layout.simple_list_item_1,
                                    type);



                    custView.setAdapter(adapter);


                    custView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(MainActivity.this);
                            awesomeInfoDialog.setTitle("CHECK IN ?");
                            awesomeInfoDialog.setMessage("Are you sure, want to check in ?");
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
                                    CustomerModel cstName = new CustomerModel();

                                    cstName.setCustalias(String.valueOf(parent.getAdapter().getItem(position)));
                                    Custcd = databaseAccess.getCustCd(cstName);

                                    cstName.setBioId(getRaw.get(0).getBioId());
                                    cstName.setCustcode(Custcd);
                                    cstName.setLoc(locate);

                                    databaseAccess.CheckInOut(cstName);

                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(getRaw.get(0).getMobile(),null,"CHKINOUT " +getRaw.get(0).getBioId()+"*"+Custcd+"*"+ location,null,null);

                                    awesomeInfoDialog.setCancelable(true);
                                    listDialog.dismiss();

                                    DialogControl diag = new DialogControl(MainActivity.this,2,R.drawable.time,"Successfully Checked in");
                                    diag.create();
                                }
                            });

                            awesomeInfoDialog.setNegativeButtonClick(new Closure() {
                                @Override
                                public void exec() {
                                    awesomeInfoDialog.setCancelable(true);
                                }
                            });
                            awesomeInfoDialog.show();


                        }
                    });
               // }
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });


        invBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeliveryActivity.class);
                startActivity(intent);
            }
        });

        absBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AbisActivity.class);
                startActivity(intent);
            }
        });


        prcSrvyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PriceSurveyAcitivity.class);
                startActivity(intent);
            }
        });


        popMatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PopActivity.class);
                startActivity(intent);

        /*        AwesomeWarningDialog awesomeWarningDialog = new AwesomeWarningDialog(MainActivity.this);
                awesomeWarningDialog.setTitle("MAINTENANCE");
                awesomeWarningDialog.setMessage("This activity is on development");
                awesomeWarningDialog.setColoredCircle(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setDialogIconOnly(R.drawable.working);
                awesomeWarningDialog.setCancelable(true);
                awesomeWarningDialog.setButtonText("Ok");
                awesomeWarningDialog.setButtonBackgroundColor(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setButtonTextColor(R.color.white);
                awesomeWarningDialog.setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        awesomeWarningDialog.setCancelable(true);
                    }
                });
                awesomeWarningDialog.show();*/
            }
        });


        IncdntBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IncidentReportActivity.class);
                startActivity(intent);

              /*  AwesomeWarningDialog awesomeWarningDialog = new AwesomeWarningDialog(MainActivity.this);
                awesomeWarningDialog.setTitle("MAINTENANCE");
                awesomeWarningDialog.setMessage("This activity is on development");
                awesomeWarningDialog.setColoredCircle(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setDialogIconOnly(R.drawable.working);
                awesomeWarningDialog.setCancelable(true);
                awesomeWarningDialog.setButtonText("Ok");
                awesomeWarningDialog.setButtonBackgroundColor(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setButtonTextColor(R.color.white);
                awesomeWarningDialog.setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        awesomeWarningDialog.setCancelable(true);
                    }
                });
                awesomeWarningDialog.show();*/
            }
        });

        StatsRptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatusReportActivity.class);
                startActivity(intent);
            }
        });

        LogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewLogsActivity.class);
                startActivity(intent);


                /*AwesomeWarningDialog awesomeWarningDialog = new AwesomeWarningDialog(MainActivity.this);
                awesomeWarningDialog.setTitle("MAINTENANCE");
                awesomeWarningDialog.setMessage("This activity is on development");
                awesomeWarningDialog.setColoredCircle(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setDialogIconOnly(R.drawable.working);
                awesomeWarningDialog.setCancelable(true);
                awesomeWarningDialog.setButtonText("Ok");
                awesomeWarningDialog.setButtonBackgroundColor(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setButtonTextColor(R.color.white);
                awesomeWarningDialog.setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        awesomeWarningDialog.setCancelable(true);
                    }
                });
                awesomeWarningDialog.show();*/
            }
        });

        attndnceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewAttendanceActivity.class);
                startActivity(intent);

            /*    AwesomeWarningDialog awesomeWarningDialog = new AwesomeWarningDialog(MainActivity.this);
                awesomeWarningDialog.setTitle("MAINTENANCE");
                awesomeWarningDialog.setMessage("This activity is on development");
                awesomeWarningDialog.setColoredCircle(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setDialogIconOnly(R.drawable.working);
                awesomeWarningDialog.setCancelable(true);
                awesomeWarningDialog.setButtonText("Ok");
                awesomeWarningDialog.setButtonBackgroundColor(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setButtonTextColor(R.color.white);
                awesomeWarningDialog.setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        awesomeWarningDialog.setCancelable(true);
                    }
                });
                awesomeWarningDialog.show();*/
            }
        });

        XpnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
                startActivity(intent);


              /*  AwesomeWarningDialog awesomeWarningDialog = new AwesomeWarningDialog(MainActivity.this);
                awesomeWarningDialog.setTitle("MAINTENANCE");
                awesomeWarningDialog.setMessage("This activity is on development");
                awesomeWarningDialog.setColoredCircle(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setDialogIconOnly(R.drawable.working);
                awesomeWarningDialog.setCancelable(true);
                awesomeWarningDialog.setButtonText("Ok");
                awesomeWarningDialog.setButtonBackgroundColor(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setButtonTextColor(R.color.white);
                awesomeWarningDialog.setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        awesomeWarningDialog.setCancelable(true);
                    }
                });
                awesomeWarningDialog.show();*/
            }
        });

    }

    private void showBottomSheetDialog() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.layout_bottomdialog);

        LinearLayout dataSynch = bottomSheetDialog.findViewById(R.id.dataSynch);
        LinearLayout leaveFile = bottomSheetDialog.findViewById(R.id.leaveFiling);
        LinearLayout logOut = bottomSheetDialog.findViewById(R.id.logOut);



        dataSynch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(MainActivity.this);
                awesomeInfoDialog.setTitle("RE-SYNCHING");
                awesomeInfoDialog.setMessage("Are you sure, want to resynch data?");
                awesomeInfoDialog.setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white);
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

                        paramArray = new ArrayList<String>();
                        List<CustomerModel> getRaw = databaseAccess.showRaw();
                        paramArray.add(getRaw.get(0).getBioId());
                        ForSynching forSynching = new ForSynching(MainActivity.this,jsonPlaceHolderApi,paramArray,R.layout.loadinglayout);
                        forSynching.equals("");
                    }
                });

                awesomeInfoDialog.setNegativeButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        awesomeInfoDialog.setCancelable(true);
                    }
                });
                awesomeInfoDialog.show();
                bottomSheetDialog.dismiss();
            }
        });

        leaveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FilingActivity.class);
                startActivity(intent);

                bottomSheetDialog.dismiss();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Test",Toast.LENGTH_SHORT).show();


                AwesomeWarningDialog awesomeWarningDialog = new AwesomeWarningDialog(MainActivity.this);
                awesomeWarningDialog.setTitle("MAINTENANCE");
                awesomeWarningDialog.setMessage("This activity is on development");
                awesomeWarningDialog.setColoredCircle(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setDialogIconOnly(R.drawable.working);
                awesomeWarningDialog.setCancelable(true);
                awesomeWarningDialog.setButtonText("Ok");
                awesomeWarningDialog.setButtonBackgroundColor(R.color.dialogWarningBackgroundColor);
                awesomeWarningDialog.setButtonTextColor(R.color.white);
                awesomeWarningDialog.setWarningButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        awesomeWarningDialog.setCancelable(true);
                    }
                });
                awesomeWarningDialog.show();
            }
        });

        bottomSheetDialog.show();
    }

    boolean isGpsEnabled()
    {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }



    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        //Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                //Toast.makeText(AttendanceActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        mSettingsClient = LocationServices.getSettingsClient(MainActivity.this);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                //  mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };


        mRequestingLocationUpdates = true;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        startLocationUpdates();

    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            location = mCurrentLocation.getLongitude() + "," + mCurrentLocation.getLatitude();

            try {
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude(),1);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}