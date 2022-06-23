package com.lemonsquare.diserapps.Controls.ViewLogs;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lemonsquare.diserapps.Controls.Expense.LoadExpense;
import com.lemonsquare.diserapps.Controls.Expense.TransportationExpense;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.R;

public class ViewLogsActivityDetails  extends AppCompatActivity {

    private BottomNavigationView bottomView;
    private String CustAlias,Act,Custcd ="";
    private DataLogic databaseAccess;
    private TextView actTxt,custnmeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlogs_dets);
        databaseAccess = DataLogic.getInstance(this);

        actTxt = (TextView) findViewById(R.id.actTypeTxt);
        custnmeTxt = (TextView) findViewById(R.id.custTxt);
        bottomView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));


        CustAlias = getIntent().getStringExtra("Custalias");
        Act = getIntent().getStringExtra("Actvty");


        actTxt.setText(Act);
        custnmeTxt.setText(CustAlias);


        CustomerModel cstName = new CustomerModel();
        cstName.setCustalias(CustAlias);
        Custcd = databaseAccess.getCustCd(cstName);


        Bundle bundle = new Bundle();
        bundle.putString("Custcd", Custcd);
        bundle.putString("Actvty", Act);
        FragNotSent fragNotSent = new FragNotSent();
        fragNotSent.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,fragNotSent).commit();


        bottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFrag = null;

                switch (menuItem.getItemId())
                {
                    case R.id.nav_sent:
                        Bundle bundleSent = new Bundle();
                        bundleSent.putString("Custcd", Custcd);
                        bundleSent.putString("Actvty", Act);
                        selectedFrag = new FragSent();
                        selectedFrag.setArguments(bundleSent);
                        break;

                    case  R.id.nav_notsent:
                        Bundle bundleNotSent = new Bundle();
                        bundleNotSent.putString("Custcd", Custcd);
                        bundleNotSent.putString("Actvty", Act);
                        selectedFrag = new FragNotSent();
                        selectedFrag.setArguments(bundleNotSent);
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,selectedFrag).commit();
                return true;
            }
        });

    }
}
