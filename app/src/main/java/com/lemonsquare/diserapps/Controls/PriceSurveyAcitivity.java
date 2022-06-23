package com.lemonsquare.diserapps.Controls;

import static com.lemonsquare.diserapps.Constant.toSend;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.DialogControl;
import com.lemonsquare.diserapps.ListAdapter.PriceSurveyAdapter;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.MaterialModel;
import com.lemonsquare.diserapps.Models.PriceSurveyModel;
import com.lemonsquare.diserapps.R;

import java.util.ArrayList;
import java.util.List;

public class PriceSurveyAcitivity extends AppCompatActivity {


    private ArrayList<CustomerModel> getCustList;
    public ArrayAdapter<String> adapter;
    private List<PriceSurveyModel> getPrceSrvey;
    private PriceSurveyAdapter priceSurveyAdapter;
    private DataLogic databaseAccess;

    private Dialog listDialog,informationDialog;


    private FloatingActionButton addItmBtn;
    private RelativeLayout addBtn;
    private RelativeLayout sbmtBtn;
    private ListView prcsrvyView;
    private LinearLayout header;
    private EditText custTxt;
    private String Custcd = "",RawItm="";
    private  int cntr,cstmerin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prcesrvy_2);
        databaseAccess = DataLogic.getInstance(this);

        custTxt = (EditText) findViewById(R.id.customerField);
        prcsrvyView = (ListView) findViewById(R.id.PrceSrvyView);
        header = (LinearLayout) findViewById(R.id.header_title);
        addItmBtn = (FloatingActionButton) findViewById(R.id.AddItemBtn);
        sbmtBtn = (RelativeLayout) findViewById(R.id.sbmtRprtBtn);
      //  addBtn = (RelativeLayout) findViewById(R.id.addItemBtn);

        listDialog = new Dialog(this);
        informationDialog = new Dialog(this);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));


        getPrceSrvey = new ArrayList<PriceSurveyModel>();


        prcsrvyView.setEmptyView(findViewById(R.id.imageViewNoData));
        custTxt.setFocusable(false);


        List<CustomerModel> getRaw = databaseAccess.showRaw();
        cstmerin = databaseAccess.getCheckInCnt(getRaw.get(0).getBioId());

        if (cstmerin !=0)
        {
            custTxt.setText(getRaw.get(0).getCustalias());
            custTxt.setEnabled(false);
        }

        if (custTxt.length() != 0)
        {
            CustomerModel cstName = new CustomerModel();
            cstName.setCustalias(getRaw.get(0).getCustalias());
            Custcd = databaseAccess.getCustCd(cstName);

            getPrceData();

        }

   //     getPrceData();

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
                        (PriceSurveyAcitivity.this, android.R.layout.simple_list_item_1,
                                type);

                custView.setAdapter(adapter);

                custView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        custTxt.setText(String.valueOf(parent.getAdapter().getItem(position)));
                        CustomerModel cstName = new CustomerModel();
                        cstName.setCustalias(String.valueOf(parent.getAdapter().getItem(position)));
                        Custcd = databaseAccess.getCustCd(cstName);

                        getPrceData();

                        listDialog.dismiss();
                    }
                });
            }
        });


        sbmtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CustomerModel> getRaw = databaseAccess.showRaw();
                if (getPrceSrvey.size() ==0)
                {
                    AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(PriceSurveyAcitivity.this);
                    awesomeInfoDialog.setTitle("DELIVERY");
                    awesomeInfoDialog.setMessage("No data to submit");
                    awesomeInfoDialog.setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white);
                    awesomeInfoDialog.setColoredCircle(R.color.dialogInfoBackgroundColor);
                    awesomeInfoDialog.setCancelable(true);
                    awesomeInfoDialog.setNeutralButtonText("Ok");
                    awesomeInfoDialog.setNeutralButtonbackgroundColor(R.color.dialogInfoBackgroundColor);
                    awesomeInfoDialog.setNeutralButtonTextColor(R.color.white);
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
                    AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(PriceSurveyAcitivity.this);
                    awesomeInfoDialog.setTitle("TIME IN ?");
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

                            for (int prcsrvy = 0; prcsrvy<=getPrceSrvey.size()-1;prcsrvy++)
                            {

                                cntr +=1;

                                PriceSurveyModel prc = new PriceSurveyModel();
                                prc.setCustcode(Custcd);
                                prc.setPrce(getPrceSrvey.get(prcsrvy).getPrce());
                                prc.setPrdct(getPrceSrvey.get(prcsrvy).getPrdct());
                                prc.setBioId(getRaw.get(0).getBioId());
                                databaseAccess.UpdatePrceSrvrySend(prc);


                                RawItm += getPrceSrvey.get(prcsrvy).getPrdct()+"-"+getPrceSrvey.get(prcsrvy).getPrce()+ "|";
                                if (cntr == 3)
                                {
                                    toSend = Custcd+"*"+ RawItm.substring(0,RawItm.length()-1)+"*"+getPrceSrvey.get(prcsrvy).getBioId();
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(getRaw.get(0).getMobile(),null,"PVR " + toSend,null,null);

                                    cntr = 0;
                                    RawItm = "";
                                    toSend = "";
                                }
                            }

                            if (RawItm.equals("")||RawItm == "")
                            {
                                return;
                            }
                            else
                            {
                                toSend = Custcd+"*"+ RawItm.substring(0,RawItm.length()-1)+"*"+getPrceSrvey.get(0).getBioId();

                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(getRaw.get(0).getMobile(),null,"PVR " + toSend,null,null);

                                cntr = 0;
                                RawItm = "";
                                toSend = "";
                            }
                            awesomeInfoDialog.setCancelable(true);

                            DialogControl diag = new DialogControl(PriceSurveyAcitivity.this,2,R.drawable.ic_success,"Price Survey submitted");
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
        });



        addItmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cust = custTxt.getText().toString().trim();

                if (TextUtils.isEmpty(cust))
                {
                    DialogControl diag = new DialogControl(PriceSurveyAcitivity.this,1,R.drawable.customer,"Please select customer");
                    diag.create();
                }
                else
                {
                    LayoutInflater inflater = PriceSurveyAcitivity.this.getLayoutInflater();
                    final View mDialogView = inflater.inflate(R.layout.prcesrvy_dialog, null);

                    informationDialog.setContentView(mDialogView);
                    informationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    informationDialog.setCanceledOnTouchOutside(false);
                    informationDialog.setCancelable(true);
                    informationDialog.show();

                    TextView titleTxt  = (TextView) informationDialog.findViewById(R.id.typeRprtTxt);
                    EditText priceTxt = (EditText) informationDialog.findViewById(R.id.PrceTxt);
                    EditText itmTxt = (EditText) informationDialog.findViewById(R.id.AddPrdctTxt);
                    RelativeLayout addBtn = (RelativeLayout) informationDialog.findViewById(R.id.AddItemBtn);
                    RelativeLayout addMreBtn = (RelativeLayout) informationDialog.findViewById(R.id.addMoreBtn);
                    RelativeLayout clseBtn = (RelativeLayout) informationDialog.findViewById(R.id.CloseBtn);

                    titleTxt.setText("Add price survey product");



                    addBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<CustomerModel> getRaw = databaseAccess.showRaw();
                            PriceSurveyModel prce = new PriceSurveyModel();
                            prce.setCustcode(Custcd);
                            prce.setPrdct(itmTxt.getText().toString());
                            prce.setPrce(Double.parseDouble(priceTxt.getText().toString()));
                            prce.setBioId(getRaw.get(0).getBioId());
                            getPrceSrvey.add(prce);

                            if (getPrceSrvey.size() !=0)
                            {
                                int Cnt = databaseAccess.CountPrceItm(prce);

                                if (Cnt == 0)
                                {
                                    databaseAccess.AddPrceSrvey(prce);

                                    priceSurveyAdapter = new PriceSurveyAdapter(PriceSurveyAcitivity.this,R.layout.layout_itemdata_v2,getPrceSrvey);
                                    prcsrvyView.setAdapter(priceSurveyAdapter);

                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                                    header.setVisibility(View.VISIBLE);

                                    itmTxt.setText("");
                                    priceTxt.setText("");

                                    informationDialog.dismiss();
                                }
                                else
                                {
                                    DialogControl diag = new DialogControl(PriceSurveyAcitivity.this,1,R.drawable.invoicenum,"Item already exist");
                                    diag.create();
                                }
                            }
                            else
                            {
                                databaseAccess.AddPrceSrvey(prce);

                                priceSurveyAdapter = new PriceSurveyAdapter(PriceSurveyAcitivity.this,R.layout.layout_itemdata_v2,getPrceSrvey);
                                prcsrvyView.setAdapter(priceSurveyAdapter);

                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                                header.setVisibility(View.VISIBLE);
                                informationDialog.dismiss();
                            }


                        }
                    });


                    addMreBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<CustomerModel> getRaw = databaseAccess.showRaw();
                            PriceSurveyModel prce = new PriceSurveyModel();
                            prce.setCustcode(Custcd);
                            prce.setPrdct(itmTxt.getText().toString());
                            prce.setPrce(Double.parseDouble(priceTxt.getText().toString()));
                            prce.setBioId(getRaw.get(0).getBioId());
                            getPrceSrvey.add(prce);

                            if (getPrceSrvey.size() !=0)
                            {
                                int Cnt = databaseAccess.CountPrceItm(prce);

                                if (Cnt == 0)
                                {
                                    databaseAccess.AddPrceSrvey(prce);

                                    priceSurveyAdapter = new PriceSurveyAdapter(PriceSurveyAcitivity.this,R.layout.layout_itemdata_v2,getPrceSrvey);
                                    prcsrvyView.setAdapter(priceSurveyAdapter);

                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                                    itmTxt.setText("");
                                    priceTxt.setText("");

                                    header.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    DialogControl diag = new DialogControl(PriceSurveyAcitivity.this,1,R.drawable.invoicenum,"Item already exist");
                                    diag.create();
                                }

                            }
                            else
                            {
                                databaseAccess.AddPrceSrvey(prce);

                                priceSurveyAdapter = new PriceSurveyAdapter(PriceSurveyAcitivity.this,R.layout.layout_itemdata_v2,getPrceSrvey);
                                prcsrvyView.setAdapter(priceSurveyAdapter);

                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                                itmTxt.setText("");
                                priceTxt.setText("");

                                header.setVisibility(View.VISIBLE);
                            }

                        }
                    });

                    clseBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                            informationDialog.dismiss();
                        }
                    });

                }
            }
        });

        prcsrvyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater inflater = PriceSurveyAcitivity.this.getLayoutInflater();
                final View mDialogView = inflater.inflate(R.layout.prcesrvy_dialog, null);

                informationDialog.setContentView(mDialogView);
                informationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                informationDialog.setCanceledOnTouchOutside(false);
                informationDialog.setCancelable(true);
                informationDialog.show();

                TextView titleTxt  = (TextView) informationDialog.findViewById(R.id.typeRprtTxt);
                EditText priceTxt = (EditText) informationDialog.findViewById(R.id.PrceTxt);
                EditText itmTxt = (EditText) informationDialog.findViewById(R.id.AddPrdctTxt);
                TextView addItmTxt = (TextView) informationDialog.findViewById(R.id.addItemTxt);
                TextView addMreTxt = (TextView) informationDialog.findViewById(R.id.addMoreItemTxt);
                RelativeLayout addBtn = (RelativeLayout) informationDialog.findViewById(R.id.AddItemBtn);
                RelativeLayout addMreBtn = (RelativeLayout) informationDialog.findViewById(R.id.addMoreBtn);
                RelativeLayout clseBtn = (RelativeLayout) informationDialog.findViewById(R.id.CloseBtn);

                titleTxt.setText("Update price survey product");

                addItmTxt.setText("Update product");
                addMreTxt.setText("Delete product");

                itmTxt.setFocusable(false);

                itmTxt.setText(getPrceSrvey.get(position).getPrdct());
                priceTxt.setText(""+getPrceSrvey.get(position).getPrce());


                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<CustomerModel> getRaw = databaseAccess.showRaw();
                        PriceSurveyModel prce = new PriceSurveyModel();
                        prce.setCustcode(Custcd);
                        prce.setPrdct(itmTxt.getText().toString());
                        prce.setPrce(Double.parseDouble(priceTxt.getText().toString()));
                        prce.setBioId(getRaw.get(0).getBioId());
                        getPrceSrvey.set(position,prce);
                        databaseAccess.UpdatePrceSrvry(prce);

                        priceSurveyAdapter.notifyDataSetChanged();

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        informationDialog.dismiss();
                    }
                });

                addMreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<CustomerModel> getRaw = databaseAccess.showRaw();
                        PriceSurveyModel prce = new PriceSurveyModel();
                        prce.setCustcode(Custcd);
                        prce.setPrdct(itmTxt.getText().toString());
                        prce.setBioId(getRaw.get(0).getBioId());
                        getPrceSrvey.remove(position);

                        databaseAccess.DeletePrceSrvy(prce);

                        priceSurveyAdapter.notifyDataSetChanged();

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        if (getPrceSrvey.size() ==0)
                        {
                            header.setVisibility(View.GONE);
                        }

                        informationDialog.dismiss();

                    }
                });


                clseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        informationDialog.dismiss();
                    }
                });
            }
        });

    }


    private void getPrceData()
    {
        List<CustomerModel> getRaw = databaseAccess.showRaw();
        PriceSurveyModel prcData = new PriceSurveyModel();
        prcData.setCustcode(Custcd);
        prcData.setBioId(getRaw.get(0).getBioId());
        List<PriceSurveyModel> getDataPrice = databaseAccess.getPrceModel(prcData);

        for (int prcedata = 0; prcedata<=getDataPrice.size()-1;prcedata++)
        {
            PriceSurveyModel dataprce = new PriceSurveyModel();
            dataprce.setPrdct(getDataPrice.get(prcedata).getPrdct());
            dataprce.setPrce(getDataPrice.get(prcedata).getPrce());
            dataprce.setBioId(getDataPrice.get(prcedata).getBioId());
            getPrceSrvey.add(dataprce);
        }


        if (getPrceSrvey.size() == 0)
        {
            header.setVisibility(View.GONE);
        }
        else
        {
            header.setVisibility(View.VISIBLE);
            priceSurveyAdapter = new PriceSurveyAdapter(PriceSurveyAcitivity.this,R.layout.layout_itemdata_v2,getPrceSrvey);
            prcsrvyView.setAdapter(priceSurveyAdapter);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            Intent intent = new Intent(PriceSurveyAcitivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
}
