package com.lemonsquare.diserapps.Controls;

import static com.lemonsquare.diserapps.Constant.locate;
import static com.lemonsquare.diserapps.Constant.toSend;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.android.material.snackbar.Snackbar;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.DialogControl;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.MaterialModel;
import com.lemonsquare.diserapps.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InventoryActivity  extends AppCompatActivity {

    private ArrayList<CustomerModel> getCustList;
    private ArrayList<MaterialModel> getMatList;
    private List<MaterialModel> getInvtItem;
    public ArrayAdapter<String> adapter;
    private DataLogic databaseAccess;

    private EditText custTxt;
    private Dialog listDialog,informationDialog;
    private LinearLayout parentInvView,invntView;
    private RelativeLayout sbmtBtn;


    private String Custcd = "",Matuom,Extmat,RawItm = "",toSndInvntor="";
    private FloatingActionButton addNewItm;

    private int mYear,mMonth,mDay,qtyCnt,cntr,cstmerin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        databaseAccess = DataLogic.getInstance(this);

        custTxt = (EditText) findViewById(R.id.customerField);
        addNewItm = (FloatingActionButton) findViewById(R.id.AddItemBtn);
        sbmtBtn = (RelativeLayout) findViewById(R.id.sbmtRprtBtn);


        parentInvView = (LinearLayout) findViewById(R.id.layout_inventorydets);
        invntView  = (LinearLayout) findViewById(R.id.layout_inventorydets);
        listDialog = new Dialog(this);
        informationDialog = new Dialog(this);




        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));

        custTxt.setFocusable(false);
        getInvtItem = new ArrayList<MaterialModel>();

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

            Controls();
        }
        else
        {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View noDataView = inflater.inflate(R.layout.layout_nodata, null);

            invntView.removeAllViews();
            invntView.addView(noDataView);
        }


        sbmtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CustomerModel> getRaw = databaseAccess.showRaw();
                MaterialModel matData = new MaterialModel();
                matData.setCustcode(Custcd);
                matData.setBioId(getRaw.get(0).getBioId());
                List<MaterialModel> getInvItm = databaseAccess.getInvItmToSend(matData);
                if (getInvItm.size()== 0)
                {
                    AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(InventoryActivity.this);
                    awesomeInfoDialog.setTitle("INVENTORY");
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
                    AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(InventoryActivity.this);
                    awesomeInfoDialog.setTitle("INVENTORY");
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

                            for (int tosnd = 0; tosnd<=getInvItm.size()-1;tosnd++)
                            {
                                cntr +=1;

                                MaterialModel matss  = new MaterialModel();
                                matss.setCustcode(Custcd);
                                matss.setExtmat(getInvItm.get(tosnd).getExtmat());
                                matss.setBioId(getRaw.get(0).getBioId());
                                databaseAccess.UpdateInvItmSend(matss);
                                RawItm += getInvItm.get(tosnd).getExtmat()+"-"+getInvItm.get(tosnd).getQty()+"-"+getInvItm.get(tosnd).getExpdte()+ "|";

                                if (cntr == 3)
                                {
                                    toSend = Custcd+"*"+ RawItm.substring(0,RawItm.length()-1)+"*"+getInvItm.get(tosnd).getBioId();
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(getRaw.get(0).getMobile(),null,"IVR " + toSend,null,null);

                                    cntr = 0;
                                    RawItm = "";
                                    toSend = "";
                                }
                            }

                            if (RawItm.equals("") || RawItm == "")
                            {
                                return;
                            }
                            else
                            {
                                toSend = Custcd+"*"+ RawItm.substring(0,RawItm.length()-1)+"*"+getInvItm.get(0).getBioId();

                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(getRaw.get(0).getMobile(),null,"IVR " + toSend,null,null);

                                cntr = 0;
                                RawItm = "";
                                toSend = "";
                            }

                            Controls();

                            awesomeInfoDialog.setCancelable(true);

                            DialogControl diag = new DialogControl(InventoryActivity.this,2,R.drawable.ic_success,"Inventory Submitted");
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

        addNewItm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cust = custTxt.getText().toString().trim();

                if (TextUtils.isEmpty(cust))
                {
                    DialogControl diag = new DialogControl(InventoryActivity.this,1,R.drawable.customer,"Please select customer");
                    diag.create();
                }
                else
                {
                    LayoutInflater inflater = InventoryActivity.this.getLayoutInflater();
                    final View mDialogView = inflater.inflate(R.layout.itemdialog, null);

                    informationDialog.setContentView(mDialogView);
                    informationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    informationDialog.setCanceledOnTouchOutside(false);
                    informationDialog.setCancelable(true);
                    informationDialog.show();

                    TextView titleTxt  = (TextView) informationDialog.findViewById(R.id.typeRprtTxt);
                    EditText expDte = (EditText) informationDialog.findViewById(R.id.ExpDateTxt);
                    EditText qtyTxt = (EditText) informationDialog.findViewById(R.id.QtyTxt);
                    EditText itmTxt = (EditText) informationDialog.findViewById(R.id.AddItmTxt);
                    RelativeLayout addBtn = (RelativeLayout) informationDialog.findViewById(R.id.AddItemBtn);
                    RelativeLayout addMreBtn = (RelativeLayout) informationDialog.findViewById(R.id.addMoreBtn);
                    RelativeLayout clseBtn = (RelativeLayout) informationDialog.findViewById(R.id.CloseBtn);
                    Button plsBtn = (Button) informationDialog.findViewById(R.id.plusBtn);
                    Button mnsBtn = (Button) informationDialog.findViewById(R.id.minusBtn);

                    titleTxt.setText("Add inventory item");


                    itmTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            getMatList = new ArrayList<MaterialModel>();

                            List<MaterialModel> materialsLists = databaseAccess.showMatNme();
                            for (int materls = 0; materls<=materialsLists.size() -1; materls++)
                            {
                                MaterialModel matLists = new MaterialModel();
                                matLists.setMatnme(materialsLists.get(materls).getMatnme());
                                getMatList.add(matLists);
                            }

                            listDialog.setContentView(R.layout.listdialog);
                            listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            listDialog.setCanceledOnTouchOutside(false);
                            listDialog.setCancelable(true);
                            listDialog.show();

                            ListView matView = (ListView) listDialog.findViewById(R.id.reportTypView);
                            TextView titleView = (TextView) listDialog.findViewById(R.id.typeRprtTxt);
                            EditText searchField = (EditText) listDialog.findViewById(R.id.searchTxtField);

                            titleView.setText("Select Material");

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

                            List<String> type = new ArrayList<String>();

                            for (int arr = 0; arr<=getMatList.size()-1; arr++)
                            {
                                type.add(getMatList.get(arr).getMatnme());
                            }

                            adapter = new ArrayAdapter< String >
                                    (InventoryActivity.this, android.R.layout.simple_list_item_1,
                                            type);

                            matView.setAdapter(adapter);

                            matView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    itmTxt.setText(String.valueOf(parent.getAdapter().getItem(position)));
                                    MaterialModel Mats = new MaterialModel();
                                    Mats.setMatnme(String.valueOf(parent.getAdapter().getItem(position)));
                                    Matuom = databaseAccess.getUom(Mats);
                                    Extmat = databaseAccess.getExtMat(Mats);
                                    listDialog.dismiss();
                                }
                            });
                        }
                    });


                    expDte.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Calendar c = Calendar.getInstance();

                            if (expDte.length() == 0)
                            {
                                mYear = c.get(Calendar.YEAR);
                                mMonth = c.get(Calendar.MONTH);
                                mDay = c.get(Calendar.DAY_OF_MONTH);
                            }
                            else
                            {
                                String str =  expDte.getText().toString();//InvList.get(position).get(Constant.FIFTH_COLUMN).toString();
                                String [] checkRt = str.split("/");
                                // mYear = c.get(Calendar.YEAR);
                                for (int a =0; a<=checkRt.length-1; a++)
                                {
                                    mMonth = Integer.parseInt(checkRt[0]) -1;
                                    mDay = Integer.parseInt(checkRt[1]);
                                    mYear = Integer.parseInt(checkRt[2]);
                                }
                            }


                            DatePickerDialog datePickerDialog = new DatePickerDialog(informationDialog.getContext(),
                                    new DatePickerDialog.OnDateSetListener() {
                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yyyy");
                                        String date = sdf.format(Calendar.getInstance().getTime());
                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            expDte.setText((monthOfYear + 1) + "/" + dayOfMonth+ "/" +year);
                                            try {
                                                Date date1 =  new SimpleDateFormat("MM/dd/yyyy").parse(expDte.getText().toString());
                                                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                                                String strDate = formatter.format(date1);
                                                expDte.setText(strDate);

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();
                        }
                    });


                    qtyTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (qtyTxt.length() > 1)
                            {
                                if (qtyTxt.getText().toString().substring(0,1).equals("0")){
                                    qtyTxt.setText(qtyTxt.getText().toString().substring(1));
                                    qtyTxt.setSelection(qtyTxt.getText().length());
                                }
                                else
                                {
                                    InputFilter[] filters = new InputFilter[1];
                                    filters[0] = new InputFilter.LengthFilter(4);
                                    qtyTxt .setFilters(filters);
                                }
                            }
                            else
                            {
                                if (qtyTxt.getText().toString().startsWith(String.valueOf(0)))
                                {
                                    qtyTxt.getText().toString().replace("0","");
                                    InputFilter[] filters = new InputFilter[1];
                                    filters[0] = new InputFilter.LengthFilter(1); //Filter to 10 characters
                                    qtyTxt .setFilters(filters);
                                }
                                else
                                {
                                    if (qtyTxt.getText().toString().startsWith(String.valueOf(0)))
                                    {
                                        qtyTxt.getText().toString().replace("0","");
                                    }
                                    else
                                    {
                                        InputFilter[] filters = new InputFilter[1];
                                        filters[0] = new InputFilter.LengthFilter(4);
                                        qtyTxt .setFilters(filters);
                                    }
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (qtyTxt.getText().length() == 0)
                            {
                                qtyTxt.getText().toString();
                            }
                            else
                            {
                                qtyCnt = Integer.parseInt(qtyTxt.getText().toString());
                            }
                        }
                    });


                    plsBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            qtyCnt = Integer.valueOf(String.valueOf(qtyTxt.getText()));
                            qtyCnt = qtyCnt +1;
                            qtyTxt.setText(""+qtyCnt);
                        }
                    });

                    mnsBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (qtyCnt==0)
                            {
                                return;
                            }
                            else
                            {
                                qtyCnt = Integer.valueOf(String.valueOf(qtyTxt.getText()));
                                qtyCnt = qtyCnt -1;
                                qtyTxt.setText(""+qtyCnt);
                            }
                        }
                    });


                    addBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String mats,expdte;

                            mats = itmTxt.getText().toString().trim();
                            expdte = expDte.getText().toString().trim();


                            if (TextUtils.isEmpty(mats))
                            {
                                Snackbar.make(mDialogView, "Please input material", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                            else if (TextUtils.isEmpty(expdte))
                            {
                                Snackbar.make(mDialogView, "Please input expiration date", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                            else if (qtyCnt == 0)
                            {
                                Snackbar.make(mDialogView, "Invalid quantity", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            else
                            {
                                if (getInvtItem.size() == 0)
                                {
                                    List<CustomerModel> getRaw = databaseAccess.showRaw();
                                    MaterialModel AddMats = new MaterialModel();
                                    CustomerModel actLogs = new CustomerModel();
                                    AddMats.setCustcode(Custcd);
                                    AddMats.setExtmat(Extmat);
                                    AddMats.setMatnme(mats);
                                    AddMats.setQty(Integer.parseInt(qtyTxt.getText().toString()));
                                    AddMats.setExpdte(expdte);
                                    AddMats.setBioId(getRaw.get(0).getBioId());
                                    databaseAccess.AddInvItm(AddMats);


                                    actLogs.setCustcode(Custcd);
                                    actLogs.setActvty("Inventory");
                                    actLogs.setBioId(getRaw.get(0).getBioId());
                                    databaseAccess.AddLogs(actLogs);

                                    Controls();

                                    itmTxt.setText("");
                                    expDte.setText("");
                                    qtyTxt.setText(String.valueOf(0));
                                    Matuom = "";
                                    qtyCnt = 0;

                                    informationDialog.dismiss();
                                }
                                else
                                {
                                    MaterialModel matNme = new MaterialModel();
                                    matNme.setCustcode(Custcd);
                                    matNme.setMatnme(mats);
                                    matNme.setQty(Integer.parseInt(qtyTxt.getText().toString()));
                                    matNme.setExpdte(expdte);
                                    int CntItm =  databaseAccess.CountItm(matNme,0);

                                    if (CntItm == 0)
                                    {
                                        List<CustomerModel> getRaw = databaseAccess.showRaw();
                                        MaterialModel AddMats = new MaterialModel();
                                        AddMats.setCustcode(Custcd);
                                        AddMats.setExtmat(Extmat);
                                        AddMats.setMatnme(mats);
                                        AddMats.setExpdte(expdte);
                                        AddMats.setQty(Integer.parseInt(qtyTxt.getText().toString()));
                                        AddMats.setBioId(getRaw.get(0).getBioId());
                                        databaseAccess.AddInvItm(AddMats);
                                        Controls();

                                        itmTxt.setText("");
                                        expDte.setText("");
                                        qtyTxt.setText(String.valueOf(0));
                                        Matuom = "";
                                        qtyCnt = 0;

                                        informationDialog.dismiss();
                                    }
                                    else
                                    {
                                        DialogControl diag = new DialogControl(InventoryActivity.this,1,R.drawable.invoicenum,"Item already exist");
                                        diag.create();
                                    }
                                }

                            }

                        }
                    });


                    addMreBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String mats,expdte;

                            mats = itmTxt.getText().toString().trim();
                            expdte = expDte.getText().toString().trim();


                            if (TextUtils.isEmpty(mats))
                            {
                                Snackbar.make(mDialogView, "Please input material", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                            else if (TextUtils.isEmpty(expdte))
                            {
                                Snackbar.make(mDialogView, "Please input expiration date", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                            else if (qtyCnt == 0)
                            {
                                Snackbar.make(mDialogView, "Invalid quantity", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            else
                            {
                                if (getInvtItem.size() == 0)
                                {
                                    List<CustomerModel> getRaw = databaseAccess.showRaw();
                                    MaterialModel AddMats = new MaterialModel();
                                    AddMats.setCustcode(Custcd);
                                    AddMats.setExtmat(Extmat);
                                    AddMats.setMatnme(mats);
                                    AddMats.setQty(Integer.parseInt(qtyTxt.getText().toString()));
                                    AddMats.setExpdte(expdte);
                                    AddMats.setBioId(getRaw.get(0).getBioId());
                                    databaseAccess.AddInvItm(AddMats);
                                    Controls();

                                    itmTxt.setText("");
                                    expDte.setText("");
                                    qtyTxt.setText(String.valueOf(0));
                                    Matuom = "";
                                    qtyCnt = 0;
                                }
                                else
                                {
                                    MaterialModel matNme = new MaterialModel();
                                    matNme.setCustcode(Custcd);
                                    matNme.setMatnme(mats);
                                    matNme.setQty(Integer.parseInt(qtyTxt.getText().toString()));
                                    matNme.setExpdte(expdte);
                                    int CntItm =  databaseAccess.CountItm(matNme,0);

                                    if (CntItm == 0)
                                    {
                                        List<CustomerModel> getRaw = databaseAccess.showRaw();
                                        MaterialModel AddMats = new MaterialModel();
                                        AddMats.setCustcode(Custcd);
                                        AddMats.setExtmat(Extmat);
                                        AddMats.setMatnme(mats);
                                        AddMats.setQty(Integer.parseInt(qtyTxt.getText().toString()));
                                        AddMats.setExpdte(expdte);
                                        AddMats.setBioId(getRaw.get(0).getBioId());
                                        databaseAccess.AddInvItm(AddMats);
                                        Controls();

                                        itmTxt.setText("");
                                        expDte.setText("");
                                        qtyTxt.setText(String.valueOf(0));
                                        Matuom = "";
                                        qtyCnt = 0;
                                    }
                                    else
                                    {
                                        DialogControl diag = new DialogControl(InventoryActivity.this,1,R.drawable.invoicenum,"Item already exist");
                                        diag.create();
                                    }
                                }
                            }
                        }
                    });


                    clseBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            informationDialog.dismiss();
                        }
                    });
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
                        (InventoryActivity.this, android.R.layout.simple_list_item_1,
                                type);

                custView.setAdapter(adapter);

                custView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        custTxt.setText(String.valueOf(parent.getAdapter().getItem(position)));
                        CustomerModel cstName = new CustomerModel();
                        cstName.setCustalias(String.valueOf(parent.getAdapter().getItem(position)));
                        Custcd = databaseAccess.getCustCd(cstName);
                        Controls();

                        listDialog.dismiss();
                    }
                });
            }
        });


    }



    private void Controls()
    {
        MaterialModel cst = new MaterialModel();
        cst.setCustcode(Custcd);
        getInvtItem = databaseAccess.getMatInv(cst);
        if (getInvtItem.size() == 0)
        {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View noDataView = inflater.inflate(R.layout.layout_nodata, null);

            invntView.removeAllViews();
            invntView.addView(noDataView);


        }
        else
        {
            invntView.removeAllViews();
            parentInvView.removeAllViews();

            for (int itmInv = 0; itmInv<=getInvtItem.size()-1;itmInv++)
            {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View itemDetails = inflater.inflate(R.layout.layout_inventorydata_v2,null);
                itemDetails.setId(itmInv);

                TextView matNameTxt = (TextView) itemDetails.findViewById(R.id.itemMatTxt);
                parentInvView = (LinearLayout) itemDetails.findViewById(R.id.layout_itemDets);

                matNameTxt.setText(getInvtItem.get(itmInv).getMatnme()+" ("+getInvtItem.get(itmInv).getUom() +")");


                MaterialModel matName = new MaterialModel();
                matName.setMatnme(getInvtItem.get(itmInv).getMatnme());
                List<MaterialModel> getMatDets = databaseAccess.getMatInvDets(matName);


                for (int matdets = 0; matdets<= getMatDets.size()-1; matdets++)
                {
                    LayoutInflater Invinflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View InvItm = Invinflater.inflate(R.layout.layout_itemdata_v2,null);
                    InvItm.setId(matdets);

                    TextView QtyTxt = (TextView) InvItm.findViewById(R.id.qtyTxt);
                    TextView expDteTxt = (TextView) InvItm.findViewById(R.id.ExpDteTxt);

                    QtyTxt.setText(""+getMatDets.get(matdets).getQty());
                    expDteTxt.setText(getMatDets.get(matdets).getExpdte());


                    int finalMatdets = matdets;
                    InvItm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater inflater = InventoryActivity.this.getLayoutInflater();
                            final View mDialogView = inflater.inflate(R.layout.itemdialog, null);

                            informationDialog.setContentView(mDialogView);
                            informationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            informationDialog.setCanceledOnTouchOutside(false);
                            informationDialog.setCancelable(true);
                            informationDialog.show();

                            TextView titleTxt  = (TextView) informationDialog.findViewById(R.id.typeRprtTxt);
                            TextView addBtnTxt = (TextView) informationDialog.findViewById(R.id.addItemTxt);
                            TextView addMreBtnTxt = (TextView) informationDialog.findViewById(R.id.addMoreItemTxt);
                            EditText expDte = (EditText) informationDialog.findViewById(R.id.ExpDateTxt);
                            EditText qtyTxt = (EditText) informationDialog.findViewById(R.id.QtyTxt);
                            EditText itmTxt = (EditText) informationDialog.findViewById(R.id.AddItmTxt);
                            RelativeLayout addBtn = (RelativeLayout) informationDialog.findViewById(R.id.AddItemBtn);
                            RelativeLayout addMreBtn = (RelativeLayout) informationDialog.findViewById(R.id.addMoreBtn);
                            RelativeLayout clseBtn = (RelativeLayout) informationDialog.findViewById(R.id.CloseBtn);
                            Button plsBtn = (Button) informationDialog.findViewById(R.id.plusBtn);
                            Button mnsBtn = (Button) informationDialog.findViewById(R.id.minusBtn);

                            titleTxt.setText("Update inventory item");


                            itmTxt.setText(getMatDets.get(finalMatdets).getMatnme());
                            expDte.setText(expDteTxt.getText().toString());
                            qtyTxt.setText(QtyTxt.getText().toString());

                            expDte.setFocusable(false);


                            addBtnTxt.setText("Update Item");
                            addMreBtnTxt.setText("Delete Item");


                            qtyCnt = Integer.parseInt(QtyTxt.getText().toString());



                            qtyTxt.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (qtyTxt.length() > 1)
                                    {
                                        if (qtyTxt.getText().toString().substring(0,1).equals("0")){
                                            qtyTxt.setText(qtyTxt.getText().toString().substring(1));
                                            qtyTxt.setSelection(qtyTxt.getText().length());
                                        }
                                        else
                                        {
                                            InputFilter[] filters = new InputFilter[1];
                                            filters[0] = new InputFilter.LengthFilter(4);
                                            qtyTxt .setFilters(filters);
                                        }
                                    }
                                    else
                                    {
                                        if (qtyTxt.getText().toString().startsWith(String.valueOf(0)))
                                        {
                                            qtyTxt.getText().toString().replace("0","");
                                            InputFilter[] filters = new InputFilter[1];
                                            filters[0] = new InputFilter.LengthFilter(1); //Filter to 10 characters
                                            qtyTxt .setFilters(filters);
                                        }
                                        else
                                        {
                                            if (qtyTxt.getText().toString().startsWith(String.valueOf(0)))
                                            {
                                                qtyTxt.getText().toString().replace("0","");
                                            }
                                            else
                                            {
                                                InputFilter[] filters = new InputFilter[1];
                                                filters[0] = new InputFilter.LengthFilter(4);
                                                qtyTxt .setFilters(filters);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (qtyTxt.getText().length() == 0)
                                    {
                                        qtyTxt.getText().toString();
                                    }
                                    else
                                    {
                                        qtyCnt = Integer.parseInt(qtyTxt.getText().toString());
                                    }
                                }
                            });


                            plsBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    qtyCnt = Integer.valueOf(String.valueOf(qtyTxt.getText()));
                                    qtyCnt = qtyCnt +1;
                                    qtyTxt.setText(""+qtyCnt);
                                }
                            });

                            mnsBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (qtyCnt==0)
                                    {
                                        return;
                                    }
                                    else
                                    {
                                        qtyCnt = Integer.valueOf(String.valueOf(qtyTxt.getText()));
                                        qtyCnt = qtyCnt -1;
                                        qtyTxt.setText(""+qtyCnt);
                                    }
                                }
                            });

                            addBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MaterialModel mats = new MaterialModel();
                                    mats.setMatnme(getMatDets.get(finalMatdets).getMatnme());
                                    mats.setQty(qtyCnt);
                                    mats.setCustcode(Custcd);
                                    mats.setExpdte(expDteTxt.getText().toString());
                                    databaseAccess.UpdateInvItem(mats);
                                    informationDialog.dismiss();

                                    DialogControl diag = new DialogControl(InventoryActivity.this,2,R.drawable.ic_success,"Item updated");
                                    diag.create();

                                    Controls();

                                }
                            });


                            addMreBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MaterialModel mats = new MaterialModel();
                                    mats.setMatnme(getMatDets.get(finalMatdets).getMatnme());
                                    mats.setCustcode(Custcd);
                                    mats.setExpdte(expDteTxt.getText().toString());
                                    databaseAccess.DeleteInvItem(mats);
                                    informationDialog.dismiss();

                                    DialogControl diag = new DialogControl(InventoryActivity.this,3,R.drawable.ic_dialog_info,"Item deleted");
                                    diag.create();


                                    Controls();
                                }
                            });


                            clseBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    informationDialog.dismiss();
                                }
                            });


                        }
                    });
                    parentInvView.addView(InvItm);
                }
                invntView.addView(itemDetails);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            Intent intent = new Intent(InventoryActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
}
