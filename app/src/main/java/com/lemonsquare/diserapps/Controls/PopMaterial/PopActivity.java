package com.lemonsquare.diserapps.Controls.PopMaterial;

import static com.lemonsquare.diserapps.Constant.asstTxt;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lemonsquare.diserapps.Controls.DeliveryActivity;
import com.lemonsquare.diserapps.Controls.Expense.LoadExpense;
import com.lemonsquare.diserapps.Controls.IncidentReportActivity;
import com.lemonsquare.diserapps.Controls.InventoryActivity;
import com.lemonsquare.diserapps.Controls.ViewLogs.FragSent;
import com.lemonsquare.diserapps.Controls.ViewLogs.ViewLogsActivity;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.ListAdapter.DisplayPopAdapter;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.ArrayModel;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.DisplayModel;
import com.lemonsquare.diserapps.Models.IncidentReportModel;
import com.lemonsquare.diserapps.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PopActivity extends AppCompatActivity {



    private DataLogic databaseAccess;
    private List<DisplayModel> getDisplayCat = new ArrayList<DisplayModel>();
    public ArrayAdapter<String> adapter;
    private List<CustomerModel> getCustList = new ArrayList<CustomerModel>();
    private ArrayList<DisplayModel> getDataDisp = new ArrayList<DisplayModel>();
    private ArrayList<DisplayModel> getDataIntentDisp = new ArrayList<DisplayModel>();
    private DisplayPopAdapter displayPopAdapter;


    private RelativeLayout sbnmt,nxt,prev;
    private FloatingActionButton addDisplayBtn;
    public Dialog popDialog,listDialog;
    private EditText custTxt;
    private ImageView imgCapt;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    private String imgString = "",Custcd;
    private int dispID,cstmerin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popupmaterial);
        databaseAccess = DataLogic.getInstance(this);

        sbnmt = (RelativeLayout) findViewById(R.id.sbmtRprtBtn);
        nxt = (RelativeLayout) findViewById(R.id.nxtBtn);
        prev = (RelativeLayout) findViewById(R.id.prevBtn);
        custTxt = (EditText) findViewById(R.id.customerField);
        addDisplayBtn = (FloatingActionButton) findViewById(R.id.AddItemBtn);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));


        popDialog = new Dialog(this);
        listDialog = new Dialog(this);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sbnmt.getLayoutParams();
        params.height = 0;

        /*RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sbnmt.getLayoutParams();
        params.height = 130;*/

        Fragment selectedFrag = null;

        selectedFrag = new ShareShelvesActivity();
        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,selectedFrag).commit();

        custTxt.setFocusable(false);

        List<CustomerModel> getRaw = databaseAccess.showRaw();
        cstmerin = databaseAccess.getCheckInCnt(getRaw.get(0).getBioId());

        if (cstmerin !=0)
        {
            custTxt.setText(getRaw.get(0).getCustalias());
            custTxt.setEnabled(false);
        }


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
                        (PopActivity.this, android.R.layout.simple_list_item_1,
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

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFrag = null;

                selectedFrag = new DisplayActivity();
                Bundle bundleSent = new Bundle();
                bundleSent.putInt("trg",0);
                selectedFrag = new DisplayActivity();
                selectedFrag.setArguments(bundleSent);

                getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,selectedFrag).commit();

                addDisplayBtn.setVisibility(View.VISIBLE);
                prev.setVisibility(View.VISIBLE);
                nxt.setVisibility(View.GONE);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFrag = null;

                selectedFrag = new ShareShelvesActivity();
                getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,selectedFrag).commit();

                addDisplayBtn.setVisibility(View.GONE);
                nxt.setVisibility(View.VISIBLE);
                prev.setVisibility(View.GONE);
            }
        });


        addDisplayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = PopActivity.this.getLayoutInflater();
                final View mDialogView = inflater.inflate(R.layout.display_dialog, null);

                popDialog.setContentView(mDialogView);
                popDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popDialog.setCanceledOnTouchOutside(false);
                popDialog.setCancelable(true);
                popDialog.show();


                ImageView captBtnImg = (ImageView) popDialog.findViewById(R.id.editImgBtn);
                imgCapt = (ImageView) popDialog.findViewById(R.id.iv_detail);
                EditText dsply = (EditText) popDialog.findViewById(R.id.displayTxt);
                RelativeLayout scanQr  = (RelativeLayout) popDialog.findViewById(R.id.qrBtn);
                asstTxt = (EditText) popDialog.findViewById(R.id.assetTxt);
                RelativeLayout addBtn = (RelativeLayout) popDialog.findViewById(R.id.addDispBtn);


                dsply.setFocusable(false);
                asstTxt.setFocusable(false);


                dsply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDisplayCat = new ArrayList<DisplayModel>();
                        List<DisplayModel> DisplayNme = databaseAccess.showDispCat();
                        for (int stat = 0; stat<=DisplayNme.size() -1; stat++)
                        {
                            DisplayModel dispNme = new DisplayModel();
                            dispNme.setDispcat(DisplayNme.get(stat).getDispcat());
                            getDisplayCat.add(dispNme);
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
                        titleView.setText("Choose display category");
                        List<String> type = new ArrayList<String>();


                        for (int arr = 0; arr<=getDisplayCat.size()-1; arr++)
                        {
                            type.add(getDisplayCat.get(arr).getDispcat());
                        }

                        adapter = new ArrayAdapter< String >
                                (PopActivity.this, android.R.layout.simple_list_item_1,
                                        type);

                        reportViewer.setAdapter(adapter);

                        reportViewer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                dsply.setText(String.valueOf(parent.getAdapter().getItem(position)));
                                DisplayModel statnme = new DisplayModel();
                                statnme.setDispcat(String.valueOf(parent.getAdapter().getItem(position)));
                                dispID = databaseAccess.getDisplayCatID(statnme);
                                listDialog.dismiss();
                            }
                        });
                    }
                });

                captBtnImg.setOnClickListener(new View.OnClickListener() {
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


                scanQr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PopActivity.this, QRActivity.class);
                        startActivity(intent);
                    }
                });


                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DisplayModel dispdata = new DisplayModel();
                        dispdata.setID(dispID);
                        dispdata.setDispcat(dsply.getText().toString());
                        dispdata.setAsstno(asstTxt.getText().toString());
                        dispdata.setImgDisp(imgString);
                        getDataDisp.add(dispdata);


              /*        ArrayModel arrayModel = (ArrayModel) getIntent().getSerializableExtra("data");
                        getDataIntentDisp = arrayModel.getMyList();*/


                        Fragment selectedFrag = null;

                        Bundle bundleSent = new Bundle();
                        bundleSent.putSerializable("data",new ArrayModel(getDataDisp));
                        bundleSent.putInt("trg",1);
                        selectedFrag = new DisplayActivity();
                        selectedFrag.setArguments(bundleSent);

                        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,selectedFrag).commit();

                        popDialog.dismiss();

                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgCapt.setImageBitmap(photo);
            GetBase64Img(photo);
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
}
