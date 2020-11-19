package com.TheTechBeing.indicatorAlerts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.SimpleTimeZone;

public class MainActivity extends AppCompatActivity {
    myDBHelper myDB;
    TextView showTime;
    CheckBox min1, min5, min15, min30, hr1, hr4, d1;
    AutoCompleteTextView atv;
    String selectedSymbol = "BTCUSDT";
    EditText EMA1_1min, EMA1_5min, EMA1_15min, EMA1_30min, EMA1_1hr, EMA1_4hr, EMA1_1d, EMA2_1min, EMA2_5min, EMA2_15min, EMA2_30min, EMA2_1hr, EMA2_4hr, EMA2_1d;
    ArrayList<String> timeList;
    ArrayList<String> coinsList = new ArrayList<>();
    private static long back_pressed_time;
    private static long period = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, myService.class);
//        ContextCompat.startForegroundService(this, intent);

        EMA1_1min = findViewById(R.id.EMA1_1min);
        EMA1_5min = findViewById(R.id.EMA1_5min);
        EMA1_15min = findViewById(R.id.EMA1_15min);
        EMA1_30min = findViewById(R.id.EMA1_30min);
        EMA1_1hr = findViewById(R.id.EMA1_1hr);
        EMA1_4hr = findViewById(R.id.EMA1_4hr);
        EMA1_1d = findViewById(R.id.EMA1_1d);
        EMA2_1min = findViewById(R.id.EMA2_1min);
        EMA2_5min = findViewById(R.id.EMA2_5min);
        EMA2_15min = findViewById(R.id.EMA2_15min);
        EMA2_30min = findViewById(R.id.EMA2_30min);
        EMA2_1hr = findViewById(R.id.EMA2_1hr);
        EMA2_4hr = findViewById(R.id.EMA2_4hr);
        EMA2_1d = findViewById(R.id.EMA2_1d);
        atv = findViewById(R.id.atv);
        showTime = findViewById(R.id.showTime);


        //showtime
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    showTime.setText("" + new SimpleDateFormat("mm:ss").format(new Date()));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        // prepare coins list
        coinsList.add("BTCUSDT");
        coinsList.add("ETHUSDT");
        coinsList.add("RVNBTC");
        coinsList.add("XTZBTC");

        loadCoinsInATV();

        myDB = new myDBHelper(this, coinsList);
        findCheckBoxes();
        timeList = myDB.getCBTimeList();
        // checking time intervals are present in DB or not
        for (String s : timeList) {
            switch (s) {
                case "1m": {
                    min1.setChecked(true);
                    break;
                }
                case "5m": {
                    min5.setChecked(true);
                    break;
                }
                case "15m": {
                    min15.setChecked(true);
                    break;
                }
                case "30m": {
                    min30.setChecked(true);
                    break;
                }
                case "1h": {
                    hr1.setChecked(true);
                    break;
                }
                case "4h": {
                    hr4.setChecked(true);
                    break;
                }
                case "1d": {
                    d1.setChecked(true);
                    break;
                }
            }
        }


        atv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSymbol = (String) parent.getItemAtPosition(position);
            }
        });
    }

    public void run(View view) {
        Intent intent = new Intent(this, myService.class);
        intent.putExtra("start",true);
        ContextCompat.startForegroundService(this, intent);
//        if (timeList.isEmpty())
//            Toast.makeText(MainActivity.this, "List empty", Toast.LENGTH_SHORT).show();
//        for (String t : timeList) {
//            switch (t) {
//                case "1m": {
//                    if (EMA1_1min.getText().toString().equals("")) {
//                        EMA1_1min.setError("required");
//                        return;
//                    }
//                    if (EMA2_1min.getText().toString().equals("")) {
//                        EMA2_1min.setError("required");
//                        return;
//                    }
//
//                    boolean r = myDB.addData(selectedSymbol, t, Double.parseDouble(EMA1_1min.getText().toString()), Double.parseDouble(EMA2_1min.getText().toString()));
//                    Log.d("mytag", "EMA1 & EMA2 added=" + r);
//                    break;
//                }
//                case "5m": {
//                    if (EMA1_5min.getText().toString().equals("")) {
//                        EMA1_5min.setError("required");
//                        return;
//                    }
//                    if (EMA2_5min.getText().toString().equals("")) {
//                        EMA2_5min.setError("required");
//                        return;
//                    }
//
//                    boolean r = myDB.addData(selectedSymbol, t, Double.parseDouble(EMA1_5min.getText().toString()), Double.parseDouble(EMA2_5min.getText().toString()));
//                    Log.d("mytag", "EMA1 & EMA2 added=" + r);
//                    break;
//                }
//                case "15m": {
//                    if (EMA1_15min.getText().toString().equals("")) {
//                        EMA1_15min.setError("required");
//                        return;
//                    }
//                    if (EMA2_15min.getText().toString().equals("")) {
//                        EMA2_15min.setError("required");
//                        return;
//                    }
//
//                    boolean r = myDB.addData(selectedSymbol, t, Double.parseDouble(EMA1_15min.getText().toString()), Double.parseDouble(EMA2_15min.getText().toString()));
//                    Log.d("mytag", "EMA1 & EMA2 added=" + r);
//                    break;
//                }
//                case "30m": {
//                    if (EMA1_30min.getText().toString().equals("")) {
//                        EMA1_30min.setError("required");
//                        return;
//                    }
//                    if (EMA2_30min.getText().toString().equals("")) {
//                        EMA2_30min.setError("required");
//                        return;
//                    }
//
//                    boolean r = myDB.addData(selectedSymbol, t, Double.parseDouble(EMA1_30min.getText().toString()), Double.parseDouble(EMA2_30min.getText().toString()));
//                    Log.d("mytag", "EMA1 & EMA2 added=" + r);
//                    break;
//                }
//                case "1h": {
//                    if (EMA1_1hr.getText().toString().equals("")) {
//                        EMA1_1hr.setError("required");
//                        return;
//                    }
//                    if (EMA2_1hr.getText().toString().equals("")) {
//                        EMA2_1hr.setError("required");
//                        return;
//                    }
//
//                    boolean r = myDB.addData(selectedSymbol, t, Double.parseDouble(EMA1_1hr.getText().toString()), Double.parseDouble(EMA2_1hr.getText().toString()));
//                    Log.d("mytag", "EMA1 & EMA2 added=" + r);
//                    break;
//                }
//                case "4h": {
//                    if (EMA1_4hr.getText().toString().equals("")) {
//                        EMA1_4hr.setError("required");
//                        return;
//                    }
//                    if (EMA2_4hr.getText().toString().equals("")) {
//                        EMA2_4hr.setError("required");
//                        return;
//                    }
//
//                    boolean r = myDB.addData(selectedSymbol, t, Double.parseDouble(EMA1_4hr.getText().toString()), Double.parseDouble(EMA2_4hr.getText().toString()));
//                    Log.d("mytag", "EMA1 & EMA2 added=" + r);
//                    break;
//                }
//                case "1d": {
//                    if (EMA1_1d.getText().toString().equals("")) {
//                        EMA1_1d.setError("required");
//                        return;
//                    }
//                    if (EMA2_1d.getText().toString().equals("")) {
//                        EMA2_1d.setError("required");
//                        return;
//                    }
//
//                    boolean r = myDB.addData(selectedSymbol, t, Double.parseDouble(EMA1_1d.getText().toString()), Double.parseDouble(EMA2_1d.getText().toString()));
//                    Log.d("mytag", "EMA1 & EMA2 added=" + r);
//                    break;
//                }
//            }
//        }

    }

    private void findCheckBoxes() {
        min1 = findViewById(R.id.min1);
        min5 = findViewById(R.id.min5);
        min15 = findViewById(R.id.min15);
        min30 = findViewById(R.id.min30);
        hr1 = findViewById(R.id.hr1);
        hr4 = findViewById(R.id.hr4);
        d1 = findViewById(R.id.d1);


        // handling notifications in DB
        min1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean r = myDB.addCheckBoxTime("1m", 0);
                    timeList = myDB.getCBTimeList();
                    Log.d("mytag", "checkBoxTime added=" + r);
                    Log.d("mytag", "isTimeAvailable= " + myDB.isTimeSelected("1m"));
                } else {
                    if (myDB.isTimeSelected("1m")) {
                        boolean b1 = myDB.removeCheckBoxTime("1m", "0");
                        Log.d("mytag", "checkBoxTime 1min removed= " + b1);
                        timeList = myDB.getCBTimeList();
                    }
                }
            }
        });
        min5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean r = myDB.addCheckBoxTime("5m", 1);
                    timeList = myDB.getCBTimeList();
                    Log.d("mytag", "checkBoxTime added=" + r);
                    Log.d("mytag", "isTimeAvailable= " + myDB.isTimeSelected("5m"));
                } else {
                    if (myDB.isTimeSelected("5m")) {
                        boolean b1 = myDB.removeCheckBoxTime("5m", "1");
                        Log.d("mytag", "checkBoxTime 5min removed= " + b1);
                        timeList = myDB.getCBTimeList();
                    }
                }
            }
        });
        min15.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myDB.addCheckBoxTime("15m", 2);
                    timeList = myDB.getCBTimeList();
                } else {
                    if (myDB.isTimeSelected("15m")) {
                        myDB.removeCheckBoxTime("15m", "2");
                        timeList = myDB.getCBTimeList();
                    }
                }
            }
        });
        min30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myDB.addCheckBoxTime("30m", 3);
                } else {
                    if (myDB.isTimeSelected("30m")) {
                        myDB.removeCheckBoxTime("30m", "3");
                        timeList = myDB.getCBTimeList();
                    }
                }
            }
        });
        hr1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myDB.addCheckBoxTime("1h", 4);
                    timeList = myDB.getCBTimeList();
                } else {
                    if (myDB.isTimeSelected("1h")) {
                        myDB.removeCheckBoxTime("1h", "4");
                        timeList = myDB.getCBTimeList();
                    }
                }
            }
        });
        hr4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myDB.addCheckBoxTime("4h", 5);
                    timeList = myDB.getCBTimeList();
                } else {
                    if (myDB.isTimeSelected("4h")) {
                        myDB.removeCheckBoxTime("4h", "5");
                        timeList = myDB.getCBTimeList();
                    }
                }
            }
        });
        d1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myDB.addCheckBoxTime("1d", 6);
                    timeList = myDB.getCBTimeList();
                } else {
                    if (myDB.isTimeSelected("1d")) {
                        myDB.removeCheckBoxTime("1d", "6");
                        timeList = myDB.getCBTimeList();
                    }
                }
            }
        });

    }

    public void showEMAs(View view) {
        timeList = myDB.getCBTimeList();
        if (!timeList.isEmpty())
                    for (String t : timeList) {
                        switch (t) {
                            case "1m": {
                                double EMA1 = myDB.getEMA(selectedSymbol, t, "EMA1");
                                double EMA2 = myDB.getEMA(selectedSymbol, t, "EMA2");
                                EMA1_1min.setText("" + EMA1);
                                EMA2_1min.setText("" + EMA2);
                                break;
                            }
                            case "5m": {
                                double EMA1 = myDB.getEMA(selectedSymbol, t, "EMA1");
                                double EMA2 = myDB.getEMA(selectedSymbol, t, "EMA2");
                                EMA1_5min.setText(new DecimalFormat("#.#").format(EMA1));
                                EMA2_5min.setText(new DecimalFormat("#.#").format(EMA2));
                                break;
                            }
                            case "15m": {
                                double EMA1 = myDB.getEMA(selectedSymbol, t, "EMA1");
                                double EMA2 = myDB.getEMA(selectedSymbol, t, "EMA2");
                                EMA1_15min.setText(new DecimalFormat("#.#").format(EMA1));
                                EMA2_15min.setText(new DecimalFormat("#.#").format(EMA2));
                                break;
                            }
                            case "30m": {
                                double EMA1 = myDB.getEMA(selectedSymbol, t, "EMA1");
                                double EMA2 = myDB.getEMA(selectedSymbol, t, "EMA2");
                                EMA1_30min.setText(new DecimalFormat("#.#").format(EMA1));
                                EMA2_30min.setText(new DecimalFormat("#.#").format(EMA2));
                                break;
                            }
                            case "1h": {
                                double EMA1 = myDB.getEMA(selectedSymbol, t, "EMA1");
                                double EMA2 = myDB.getEMA(selectedSymbol, t, "EMA2");
                                EMA1_1hr.setText(new DecimalFormat("#.#").format(EMA1));
                                EMA2_1hr.setText(new DecimalFormat("#.#").format(EMA2));
                                break;
                            }
                            case "4h": {
                                double EMA1 = myDB.getEMA(selectedSymbol, t, "EMA1");
                                double EMA2 = myDB.getEMA(selectedSymbol, t, "EMA2");
                                EMA1_4hr.setText(new DecimalFormat("#.#").format(EMA1));
                                EMA2_4hr.setText(new DecimalFormat("#.#").format(EMA2));
                                break;
                            }
                            case "1d": {
                                double EMA1 = myDB.getEMA(selectedSymbol, t, "EMA1");
                                double EMA2 = myDB.getEMA(selectedSymbol, t, "EMA2");
                                EMA1_1d.setText(new DecimalFormat("#.#").format(EMA1));
                                EMA2_1d.setText(new DecimalFormat("#.#").format(EMA2));
                                break;
                            }
                        }
                    }
    }

    private void loadCoinsInATV() {
        atv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, coinsList));

        atv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                atv.showDropDown();
                return false;
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (back_pressed_time + period > System.currentTimeMillis())
            super.onBackPressed();
        else {
            Toast.makeText(this, "Press again to Exit", Toast.LENGTH_SHORT).show();
            back_pressed_time = System.currentTimeMillis();
        }
    }
}