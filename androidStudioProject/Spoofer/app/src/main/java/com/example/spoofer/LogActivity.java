package com.example.spoofer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogActivity extends AppCompatActivity {

    private BufferedReader mapLog = null;
    private BufferedReader serviceLog = null;

    private TextView mapLogText;
    private TextView serviceLogText;

    private void readFileIntoTextView(BufferedReader br, TextView tv){
        String allText = "";
        for(Object line : br.lines().collect(Collectors.toList())){
            String strLine = (String) line + "\n";
            allText += strLine;
        }
        tv.append(allText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            File logFile = new File(getFilesDir(), "map.log");
            logFile.setReadable(true);
            mapLog = new BufferedReader(new FileReader(logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File logFile = new File(getFilesDir(), "service.log");
            logFile.setReadable(true);
            serviceLog = new BufferedReader(new FileReader(logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        mapLogText = (TextView) findViewById(R.id.mapLog);
        serviceLogText = (TextView) findViewById(R.id.serviceLog);

        readFileIntoTextView(mapLog, mapLogText);
        readFileIntoTextView(serviceLog, serviceLogText);

    }

    @Override
    protected void onDestroy() {
        try {
            mapLog.close();
            serviceLog.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}