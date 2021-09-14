package com.example.spoofer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MockService extends Service {

    private LocationManager locationManager;

    private BufferedWriter log;

    private NotificationChannel chan;
    private NotificationManager manager;

    public MockService() {
    }

    private void logWrite(String text){
        try {
            log.write(text);
            log.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        chan = new NotificationChannel("notifs", "Spoofer", NotificationManager.IMPORTANCE_NONE);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chan);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.addTestProvider("gps",false, false, false, false, false, false, false, Criteria.POWER_HIGH, Criteria.ACCURACY_MEDIUM);
        locationManager.setTestProviderEnabled("gps", true);
        locationManager.addTestProvider("network",false, false, false, false, false, false, false, Criteria.POWER_HIGH, Criteria.ACCURACY_MEDIUM);
        locationManager.setTestProviderEnabled("network", true);
        //locationManager.addTestProvider("passive",false, false, false, false, false, false, false, 1, 1);
        //locationManager.setTestProviderEnabled("passive", true);



        try {
            File logFile = new File(getFilesDir(), "service.log");
            logFile.setWritable(true);
            if(!logFile.exists()){
                logFile.createNewFile();
            }
            log = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notifs")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Spoofer")
                .setContentText("Your location is being spoofed with Spoofer!");
        Notification n = builder.build();

        startForeground(1, n);

        logWrite("Location service created.\n");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String logMessage = "Service Invoked:\n";
        double lat = intent.getDoubleExtra("lat", 0);
        logMessage = logMessage + "Latitude:  " + lat + "\n";
        double lon = intent.getDoubleExtra("lon", 0);
        logMessage = logMessage + "Longitude: " + lon + "\n";
        for(String prov : locationManager.getAllProviders()) {
            if(!prov.equalsIgnoreCase("passive")) {
                Location loc = new Location(prov);
                loc.setLatitude(lat);
                loc.setLongitude(lon);
                loc.setAccuracy(1);
                loc.setTime(System.currentTimeMillis());
                loc.setElapsedRealtimeNanos(SystemClock.elapsedRealtime());
                logMessage = logMessage + "Location Object: " + loc.toString() + "\n";
                locationManager.setTestProviderLocation(prov, loc);
                logMessage = logMessage + "Test provider [" + prov + "] location set.\n";
                //logMessage = logMessage + "Location manager providers: " + locationManager.getAllProviders().toString();
                logWrite(logMessage);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            log.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}