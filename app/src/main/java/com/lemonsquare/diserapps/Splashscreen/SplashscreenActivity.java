package com.lemonsquare.diserapps.Splashscreen;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadingview.LoadingView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lemonsquare.diserapps.BuildConfig;
import androidx.appcompat.app.AppCompatActivity;

import com.lemonsquare.diserapps.Controls.ViewAttendance.ViewAttendanceActivity;
import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.DialogControl;
import com.lemonsquare.diserapps.JsonInterface.JsonAPI;
import com.lemonsquare.diserapps.ListAdapter.AttendanceAdapter;
import com.lemonsquare.diserapps.LogIn.LogInActivity;
import com.lemonsquare.diserapps.MainActivity;
import com.lemonsquare.diserapps.Models.CustomerModel;
import com.lemonsquare.diserapps.Models.DiserModel;
import com.lemonsquare.diserapps.Models.InOutModel;
import com.lemonsquare.diserapps.Models.MaterialModel;
import com.lemonsquare.diserapps.Models.ResultModel;
import com.lemonsquare.diserapps.Models.StatusReportModel;
import com.lemonsquare.diserapps.R;
import com.lemonsquare.diserapps.Validation.ValidationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;



public class SplashscreenActivity extends AppCompatActivity {
    private JsonAPI jsonPlaceHolderApi;
    private List<String> paramArray;

    private boolean firstStart;
    private SharedPreferences prefs;

    private DataLogic dataLogic;
    private Dialog loadingDialog;
    private LoadingView loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dataLogic = DataLogic.getInstance(this);

        loadingDialog = new Dialog(this);

        transparentStatusAndNavigation();

        prefs = getSharedPreferences(BuildConfig.VERSION_NAME, MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstStart", true);
//      dataLogic.showFilingCatbl();
  //      List<CustomerModel> getRaw = dataLogic.showRaw();

        if (firstStart)
        {
            dataLogic.CREATETABLES();

            prefs = getSharedPreferences(BuildConfig.VERSION_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();

            Intent intent = new Intent(SplashscreenActivity.this, ValidationActivity.class);
            startActivity(intent);

        }
        else
        {
            List<DiserModel> getDataDser = dataLogic.getRaw();
            if (getDataDser.size()  == 0)
            {
                Intent intent = new Intent(SplashscreenActivity.this, ValidationActivity.class);
                startActivity(intent);
            }
            else
            {
                DiserModel dsr = new DiserModel();
                dsr.setBioId(getDataDser.get(0).getBioId());

                int Cnter = dataLogic.getCntVlidateUser(dsr,0);

                if (Cnter == 0)
                {
                    Intent intent = new Intent(SplashscreenActivity.this, LogInActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(SplashscreenActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }

    }


    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }


    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


}
