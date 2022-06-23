package com.lemonsquare.diserapps.Controls.Expense;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lemonsquare.diserapps.R;

public class ExpenseActivity extends AppCompatActivity {
    private BottomNavigationView bottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        bottomView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statsBar));


        bottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFrag = null;

                switch (menuItem.getItemId())
                {
                    case R.id.nav_load:
                      /*  Bundle bundle = new Bundle();
                        bundle.putString("Shipno", shipno);
                        bundle.putString("ToCollect",toColl);*/
                        selectedFrag = new LoadExpense();
                      //  selectedFrag.setArguments(bundle);
                        // selectedFrag = new BoPerSacks();
                        break;

                    case  R.id.nav_transpo:
                      /*  Bundle MatBundle = new Bundle();
                        MatBundle.putString("Shipno", shipno);
                        MatBundle.putString("ToCollect",toColl);*/
                        selectedFrag = new TransportationExpense();
             /*           selectedFrag.setArguments(MatBundle);
                        Toolbar toolbars = (Toolbar) findViewById(R.id.toolbar);
                        setSupportActionBar(toolbars);*/
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fLayout,selectedFrag).commit();
                return true;
            }
        });
    }
}
