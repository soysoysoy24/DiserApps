package com.lemonsquare.diserapps;

import static com.lemonsquare.diserapps.Constant.locate;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.lemonsquare.diserapps.DataAccess.DataLogic;
import com.lemonsquare.diserapps.Models.DiserModel;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.lemonsquare.diserapps.Constant.*;



import java.lang.ref.WeakReference;
import java.util.List;

public class MyService extends Service {

    private DataLogic dataLogic;
    public static final String CHANNEL_2_ID = "channel2";
    private String CHANNEL_ID = "Diserbackground";

    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private LocationEngine locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);


    private String locs;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isGpsEnabled())
        {
            stopSelf();
            stopForeground(true);
        }
        else
        {
            initLocationEngine();
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataLogic = DataLogic.getInstance(this);
        createNotification();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Diser",
                    NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);


            notif();

        }
        else
        {
            notif();
        }


    }

    void notif()
    {
        List<DiserModel> getInfo = dataLogic.showDiserInfo();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Hello, "+getInfo.get(0).getFname())
                .setSmallIcon(R.drawable.diserlogo)
                .setColor(Color.parseColor("#EC9400"))
                //.setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();
        startForeground(1, notification);
    }


    void createNotification()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,"Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("This is Channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            //  manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }


    void prompt() {
        cntDown.cancel();
        alert();
    }

    void alert()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tap OK to re-open Diser Apps")
                .setTitle("GPS is disable")
                .setCancelable(false)
                .setIcon(R.mipmap.diser_app_logo2)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //intent();
                        stopSelf();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopSelf();
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        else {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
        alert.show();
    }


    boolean isGpsEnabled() {
        LocationManager service = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void initLocationEngine() {
        if (!isGpsEnabled())
        {
            prompt();
        }
        else
        {
            locationEngine = LocationEngineProvider.getBestLocationEngine(this);

            LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                    .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

            locationEngine.requestLocationUpdates(request, callback, getMainLooper());
            locationEngine.getLastLocation(callback);
        }
    }


    private class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MyService> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(MyService activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            MyService activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location != null) {
                    //locs = result.getLastLocation().getLatitude()+","+result.getLastLocation().getLongitude();
                    locate = result.getLastLocation().getLongitude()+","+result.getLastLocation().getLatitude();
                  //  Toast.makeText(getApplicationContext(), locs, Toast.LENGTH_SHORT).show();
                }

            }
        }
        @Override
        public void onFailure(@NonNull Exception exception) {

        }
    }


    CountDownTimer cntDown = new CountDownTimer(2000, 1000) {
        public void onTick(long millisUntilFinished) {
            if (!isGpsEnabled()) {
                stopSelf();
                stopForeground(true);
                prompt();
            }
        }
        public void onFinish() {
            cntDown.start();
        }
    };

}
