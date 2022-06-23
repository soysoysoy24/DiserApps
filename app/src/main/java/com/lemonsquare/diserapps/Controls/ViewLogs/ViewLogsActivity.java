package com.lemonsquare.diserapps.Controls.ViewLogs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.lemonsquare.diserapps.Controls.InventoryActivity;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.ListAdapter.ActivityLogsAdapter;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewLogsActivity extends AppCompatActivity {

    private ListView logsView;
    private ActivityLogsAdapter activityLogsAdapter;
    private DataLogic databaseAccess;
    private List<CustomerModel> getLogsData;
    private EditText searchTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlogs);
        databaseAccess = DataLogic.getInstance(this);

        logsView = (ListView) findViewById(R.id.logsView);
        searchTxt = (EditText) findViewById(R.id.searchTxtField);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));

       logsView.setEmptyView(findViewById(R.id.imageViewNoData));



       List<CustomerModel> getRaw = databaseAccess.showRaw();
        CustomerModel myBio_Logs = new CustomerModel();
        myBio_Logs.setBioId(getRaw.get(0).getBioId());

        List<CustomerModel> getLogs = databaseAccess.viewLogs(myBio_Logs);

        getLogsData  = new ArrayList<CustomerModel>();
        for (int logs = 0;logs<=getLogs.size()-1; logs++)
        {
            CustomerModel LogsAct = new CustomerModel();
            LogsAct.setActvty(getLogs.get(logs).getActvty());
            LogsAct.setCustalias(getLogs.get(logs).getCustalias());
            LogsAct.setSbmtddte(getLogs.get(logs).getSbmtddte());
            getLogsData.add(LogsAct);
        }

        activityLogsAdapter = new ActivityLogsAdapter(ViewLogsActivity.this,R.layout.layout_logsdata,getLogsData);
        logsView.setAdapter(activityLogsAdapter);


        if (getLogsData.size() >=5)
        {
            searchTxt.setVisibility(View.VISIBLE);
        }



        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = searchTxt.getText().toString().toLowerCase(Locale.getDefault());
                activityLogsAdapter.filter(text);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            Intent intent = new Intent(ViewLogsActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return true;
    }
}
