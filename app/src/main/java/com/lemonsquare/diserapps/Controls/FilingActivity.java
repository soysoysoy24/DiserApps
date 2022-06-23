package com.lemonsquare.diserapps.Controls;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.DialogControl;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.BoModel;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.FilingModel;
import com.lemonsquare.diserapps.Models.StatusReportModel;
import com.lemonsquare.diserapps.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FilingActivity extends AppCompatActivity {

    public ArrayAdapter<String> adapter;
    private List<FilingModel> getFilingList = new ArrayList<FilingModel>();
    private DataLogic databaseAccess;

    private EditText frmTxt,toTxt,catergTxt,reasonTxt;
    private int mYear, mMonth, mDay,lvtp;
    private Dialog CalendarDiag,listDialog;
    private RelativeLayout sbmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filing);
        databaseAccess = DataLogic.getInstance(this);


        frmTxt = (EditText) findViewById(R.id.dteFrmFiling);
        toTxt = (EditText) findViewById(R.id.dteToFiling);
        catergTxt = (EditText) findViewById(R.id.categoryField);
        reasonTxt = (EditText) findViewById(R.id.reasonTxtField);
        sbmt = (RelativeLayout) findViewById(R.id.sbmtRprtBtn);

        CalendarDiag = new Dialog(this);
        listDialog = new Dialog(this);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));


        catergTxt.setFocusable(false);
        frmTxt.setFocusable(false);
        toTxt.setFocusable(false);


        sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AwesomeInfoDialog awesomeInfoDialog = new AwesomeInfoDialog(FilingActivity.this);
                awesomeInfoDialog.setTitle("OFF FILING");
                awesomeInfoDialog.setMessage("Are you sure, want to file leave/change day off ?");
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

                        FilingModel fil = new FilingModel();
                        fil.setRson(reasonTxt.getText().toString());
                        fil.setFilingcat(catergTxt.getText().toString());
                        databaseAccess.sbmitFiling(fil,getRaw.get(0).getBioId());

                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(getRaw.get(0).getMobile(),null,"FL " + getRaw.get(0).getBioId() +"*"+ lvtp +"*"+ reasonTxt.getText().toString(),null,null);

                        awesomeInfoDialog.setCancelable(true);


                        catergTxt.setText("");
                        reasonTxt.setText("");
                        frmTxt.setText("");
                        toTxt.setText("");

                        DialogControl diag = new DialogControl(FilingActivity.this,2,R.drawable.ic_success,"Successfully Submitted");
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


        catergTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFilingList = new ArrayList<FilingModel>();
                List<FilingModel> CatNme = databaseAccess.showFilingCat();
                for (int stat = 0; stat<=CatNme.size() -1; stat++)
                {
                    FilingModel CategName = new FilingModel();
                    CategName.setFilingcat(CatNme.get(stat).getFilingcat());
                    getFilingList.add(CategName);
                }

                listDialog.setContentView(R.layout.listdialog);
                listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                listDialog.setCanceledOnTouchOutside(false);
                listDialog.setCancelable(true);
                listDialog.show();


                ListView filingView = (ListView) listDialog.findViewById(R.id.reportTypView);
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
                titleView.setText("Select category");
                List<String> type = new ArrayList<String>();

                for (int arr = 0; arr<=getFilingList.size()-1; arr++)
                {
                    type.add(getFilingList.get(arr).getFilingcat());
                }




                adapter = new ArrayAdapter< String >
                        (FilingActivity.this, android.R.layout.simple_list_item_1,
                                type);

                filingView.setAdapter(adapter);

                filingView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        catergTxt.setText(String.valueOf(parent.getAdapter().getItem(position)));
                        FilingModel statname = new FilingModel();
                        statname.setFilingcat(String.valueOf(parent.getAdapter().getItem(position)));
                        lvtp = databaseAccess.getFilingCatID(statname);
                        listDialog.dismiss();
                    }
                });
            }
        });

        frmTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                if (frmTxt.length() == 0)
                {
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                }
                else
                {
                    String str =  frmTxt.getText().toString();//InvList.get(position).get(Constant.FIFTH_COLUMN).toString();
                    String [] checkRt = str.split("/");
                    // mYear = c.get(Calendar.YEAR);
                    for (int a =0; a<=checkRt.length-1; a++)
                    {
                        mMonth = Integer.parseInt(checkRt[0]) -1;
                        mDay = Integer.parseInt(checkRt[1]);
                        mYear = Integer.parseInt(checkRt[2]);
                    }
                }


                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarDiag.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yyyy");
                            String date = sdf.format(Calendar.getInstance().getTime());
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                frmTxt.setText((monthOfYear + 1) + "/" + dayOfMonth  + "/" +year);
                                try {
                                    Date date1 =  new SimpleDateFormat("MM/dd/yyyy").parse(frmTxt.getText().toString());
                                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                                    String strDate = formatter.format(date1);
                                    frmTxt.setText(strDate);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        toTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                if (toTxt.length() == 0)
                {
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                }
                else
                {
                    String str =  toTxt.getText().toString();//InvList.get(position).get(Constant.FIFTH_COLUMN).toString();
                    String [] checkRt = str.split("/");
                    // mYear = c.get(Calendar.YEAR);
                    for (int a =0; a<=checkRt.length-1; a++)
                    {
                        mMonth = Integer.parseInt(checkRt[0]) -1;
                        mDay = Integer.parseInt(checkRt[1]);
                        mYear = Integer.parseInt(checkRt[2]);
                    }
                }


                DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarDiag.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM/d/yyyy");
                            String date = sdf.format(Calendar.getInstance().getTime());
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                toTxt.setText((monthOfYear + 1) + "/" + dayOfMonth  + "/" +year);
                                try {
                                    Date date1 =  new SimpleDateFormat("MM/dd/yyyy").parse(toTxt.getText().toString());
                                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                                    String strDate = formatter.format(date1);
                                    toTxt.setText(strDate);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            Intent intent = new Intent(FilingActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
}
