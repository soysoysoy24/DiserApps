package com.lemonsquare.diserapps.Controls.Expense;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lemonsquare.diserapps.Controls.FilingActivity;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.Models.ExpenseModel;
import com.lemonsquare.diserapps.Models.FilingModel;
import com.lemonsquare.diserapps.R;

import java.util.ArrayList;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {
   // private BottomNavigationView bottomView;\

    private DataLogic databaseAccess;
    private ArrayList<ExpenseModel> getExpnseCat = new ArrayList<ExpenseModel>();
    public ArrayAdapter<String> adapter;


    private EditText xpnseTpTxt;
    private Dialog listDialog;

    private int lvtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        databaseAccess = DataLogic.getInstance(this);

        xpnseTpTxt = (EditText) findViewById(R.id.xpnseTypField);
      //  bottomView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        listDialog = new Dialog(this);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));


        xpnseTpTxt.setFocusable(false);

        xpnseTpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ExpenseModel> CatNme = databaseAccess.showExpnseCat();
                for (int stat = 0; stat<=CatNme.size() -1; stat++)
                {
                    ExpenseModel CategName = new ExpenseModel();
                    CategName.setXpnseDesc(CatNme.get(stat).getXpnseDesc());
                    getExpnseCat.add(CategName);
                }

                listDialog.setContentView(R.layout.listdialog);
                listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                listDialog.setCanceledOnTouchOutside(false);
                listDialog.setCancelable(true);
                listDialog.show();


                ListView xpnseView = (ListView) listDialog.findViewById(R.id.reportTypView);
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

                for (int arr = 0; arr<=getExpnseCat.size()-1; arr++)
                {
                    type.add(getExpnseCat.get(arr).getXpnseDesc());
                }




                adapter = new ArrayAdapter< String >
                        (ExpenseActivity.this, android.R.layout.simple_list_item_1,
                                type);

                xpnseView.setAdapter(adapter);

                xpnseView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        xpnseTpTxt.setText(String.valueOf(parent.getAdapter().getItem(position)));
                        ExpenseModel statname = new ExpenseModel();
                        statname.setXpnseDesc(String.valueOf(parent.getAdapter().getItem(position)));
                        lvtp = databaseAccess.getExpenseCatID(statname);
                        listDialog.dismiss();
                    }
                });
            }
        });

    /*    bottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFrag = null;

                switch (menuItem.getItemId())
                {
                    case R.id.nav_load:
                      *//*  Bundle bundle = new Bundle();
                        bundle.putString("Shipno", shipno);
                        bundle.putString("ToCollect",toColl);*//*
                        selectedFrag = new LoadExpense();
                      //  selectedFrag.setArguments(bundle);
                        // selectedFrag = new BoPerSacks();
                        break;

                    case  R.id.nav_transpo:
                      *//*  Bundle MatBundle = new Bundle();
                        MatBundle.putString("Shipno", shipno);
                        MatBundle.putString("ToCollect",toColl);*//*
                        selectedFrag = new TransportationExpense();
             *//*           selectedFrag.setArguments(MatBundle);
                        Toolbar toolbars = (Toolbar) findViewById(R.id.toolbar);
                        setSupportActionBar(toolbars);*//*
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,selectedFrag).commit();
                return true;
            }
        });*/
    }
}
