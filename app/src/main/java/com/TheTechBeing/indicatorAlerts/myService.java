package com.TheTechBeing.indicatorAlerts;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class myService extends IntentService {

    RequestQueue mRequestQueue;
    myDBHelper myDB;
    ArrayList<String> times = new ArrayList<>();
    ArrayList<String> coins = new ArrayList<>();
    NotificationManager manager;
//    public interface volleyCallbackInterface{
//        void success(double MA,String limit,String symbol, String time);
//    }

    public myService() {
        super("myService");
        setIntentRedelivery(true);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) { //2nd method call
        assert intent != null;
        if(Objects.requireNonNull(intent.getExtras()).getBoolean("start")) {
//            fetchMA1("BTCUSDT", "1m", "9");
            fetchMA1FromTaapi("BTCUSDT", "1m", "9");

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(new Thread(new Runnable() {
                @Override
                public void run() {
                    fetchEMA();
                }
            }), 0, 1, TimeUnit.MINUTES);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) { // 3rd method call

        // making app in foreground by foreground notification only
//        createNotificationChannel();
        createNotification();
    }

    @Override
    public void onCreate() { // 1st method call
        super.onCreate();
        // prepare coins list
        coins.add("BTCUSDT");
        coins.add("ETHUSDT");
        coins.add("RVNBTC");
        coins.add("XTZBTC");

//        mRequestQueue = volleySingleton.getInstance(this).getMrequestQueue();
        mRequestQueue = Volley.newRequestQueue(this);

        myDB = new myDBHelper(this, coins);
        //adding Moving average to DB

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    private void fetchMA1(final String symbol, final String time, final String limit) {
        String url = "https://api.binance.com/api/v3/klines?symbol=" + symbol + "&interval=" + time + "&limit=" + limit;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    double sum = 0;
                    for (int i = 0; i < response.length(); i++) {
                        JSONArray klineData = response.getJSONArray(i);
                        String closeP = klineData.getString(4); // 5th index has close price
                        double closingPrice = Double.parseDouble(closeP);
                        // calculating MA
                        sum = sum + closingPrice;
                    }
                    double lim = Double.parseDouble(limit);
                    double MA = sum / lim;

                    Log.d("mytag", "MA= " + MA);
                    if (limit.equals("9")) {
                        myDB.updateEMADataBase1(symbol, time, MA);
                    } else {
                        myDB.updateEMADataBase2(symbol, time, MA);
                    }
                        fetchMA2(symbol,time,"24");
//                    fetchMA("BTCUSDT", "1m", "24");
//                    callback.success(MA,limit,symbol,time);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("myTAG", "ERROR: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myTAG", "onErrorResponse: ");
                Log.d("myTAG", "status code: " + error.getMessage());
//                Log.d("myTAG", "status code: " + error.networkResponse.statusCode);
            }
        });
        mRequestQueue.add(request);
    }
    private void fetchMA2(final String symbol, final String time, final String limit) {
        String url = "https://api.binance.com/api/v3/klines?symbol=" + symbol + "&interval=" + time + "&limit=" + limit;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    double sum = 0;
                    for (int i = 0; i < response.length(); i++) {
                        JSONArray klineData = response.getJSONArray(i);
                        String closeP = klineData.getString(4); // 5th index has close price
                        double closingPrice = Double.parseDouble(closeP);
                        // calculating MA
                        sum = sum + closingPrice;
                    }
                    double lim = Double.parseDouble(limit);
                    double MA = sum / lim;

                    Log.d("mytag", "MA= " + MA);
                    if (limit.equals("9")) {
                        myDB.updateEMADataBase1(symbol, time, MA);
                    } else {
                        myDB.updateEMADataBase2(symbol, time, MA);
                    }

//                    fetchMA("BTCUSDT", "1m", "24");
//                    callback.success(MA,limit,symbol,time);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("myTAG", "ERROR: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myTAG", "onErrorResponse: ");
                Log.d("myTAG", "status code: " + error.getMessage());
//                Log.d("myTAG", "status code: " + error.networkResponse.statusCode);
            }
        });
        mRequestQueue.add(request);
    }

    private void fetchMA1FromTaapi(final String symbol, final String time, final String limit) {
        String url = "https://api.taapi.io/ema?secret=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InZnZW1pc3Npb25AZ21haWwuY29tIiwiaWF0IjoxNjA1MjU1NDUwLCJleHAiOjc5MTI0NTU0NTB9.LG2PO7fWi2NVKL7SUC33-dd5k1rVULjhmXibibGRylk&exchange=binance&symbol=BTC/USDT&interval=1m&optInTimePeriod="+limit;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String taapiEMA = response.getString("value");

                    double tEMA = Double.parseDouble(taapiEMA);

                    Log.d("mytag", "taapiEMA= " + tEMA);
                    if (limit.equals("9")) {
                        myDB.updateEMADataBase1(symbol, time, tEMA);
                    } else {
                        myDB.updateEMADataBase2(symbol, time, tEMA);
                    }

                    fetchMA2FromTaapi(symbol,time,"24");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("myTAG", "ERROR: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myTAG", "onErrorResponse: ");
                Log.d("myTAG", "status code: " + error.getMessage());
//                Log.d("myTAG", "status code: " + error.networkResponse.statusCode);
            }
        });
        mRequestQueue.add(request);
    }
    private void fetchMA2FromTaapi(final String symbol, final String time, final String limit) {
        String url = "https://api.taapi.io/ema?secret=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InZnZW1pc3Npb25AZ21haWwuY29tIiwiaWF0IjoxNjA1MjU1NDUwLCJleHAiOjc5MTI0NTU0NTB9.LG2PO7fWi2NVKL7SUC33-dd5k1rVULjhmXibibGRylk&exchange=binance&symbol=BTC/USDT&interval=1m&optInTimePeriod="+limit;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String taapiEMA = response.getString("value");
                    double tEMA = Double.parseDouble(taapiEMA);

//
                    Log.d("mytag", "taapiEMA= " + tEMA);
                    if (limit.equals("9")) {
                        myDB.updateEMADataBase1(symbol, time, tEMA);
                    } else {
                        myDB.updateEMADataBase2(symbol, time, tEMA);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("myTAG", "ERROR: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myTAG", "onErrorResponse: ");
                Log.d("myTAG", "status code: " + error.getMessage());
//                Log.d("myTAG", "status code: " + error.networkResponse.statusCode);
            }
        });
        mRequestQueue.add(request);
    }

    private void fetchEMA() {
        for (int i = 0; i < coins.size(); i++) {
            String symbol = coins.get(i);
            times = myDB.getTimeList(symbol);
            for (int j = 0; j < times.size(); j++) {
                String time = times.get(j);
                String url = "https://api.binance.com/api/v3/klines?symbol=" + symbol + "&interval=" + time + "&limit=1";
                double preEMA1 = myDB.getEMA(symbol, time, "EMA1");
                double preEMA2 = myDB.getEMA(symbol, time, "EMA2");
                Log.d("mytag", "preEMA1 : preEMA2 " + preEMA1 + " : " + preEMA2);
                getClosingPrice(url, preEMA1, preEMA2, symbol, time);
            }
        }
    }

    private void getClosingPrice(String url, final double preEMA1, final double preEMA2, final String symbol, final String time) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONArray klineData = response.getJSONArray(response.length() - 1);
                    String closeP = klineData.getString(4); // 5th index has close price
                    double closingPrice = Double.parseDouble(closeP);
                    calcEMA(preEMA1, preEMA2, closingPrice, symbol, time);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("myTAG", "ERROR: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myTAG", "onErrorResponse: ");
                Log.d("myTAG", "status code: " + error.getMessage());
//                Log.d("myTAG", "status code: " + error.networkResponse.statusCode);
            }
        });
        mRequestQueue.add(request);
    }

    private void calcEMA(double preEMA1, double preEMA2, double closingPrice, String symbol, String time) {
        Log.d("mytag", "close price= " + closingPrice);
        double factor1 = (double) 2 / (9 + 1);
        double EMA1 = (Math.abs(closingPrice - preEMA1)) * factor1 + preEMA1;
        Log.d("mytag", "EMA1= " + EMA1);

        double factor2 = (double) 2 / (24 + 1);
        double EMA2 = (Math.abs(closingPrice - preEMA2)) * factor2 + preEMA2;
        Log.d("mytag", "EMA2= " + EMA2);

        // updating database
        myDB.updateEMADataBase(symbol, time, EMA1, EMA2);

//        double preE1 = myDB.getEMA(symbol, time, "EMA1");
//        double preE2 = myDB.getEMA(symbol, time, "EMA2");
//        Log.d("mytag","EMA1= "+preE1);
//        Log.d("mytag","EMA2= "+preE2);
        checkForAlerts(EMA1, EMA2, symbol, time);
    }

    private void checkForAlerts(double ema9, double ema24, String symbol, String time) {
        if (Math.abs(ema9 - ema24) < 4 && Math.abs(ema9 - ema24) > 0) {
            String e9;
            if ((ema9 - ema24) < 0)
                e9 = "smaller";
            else
                e9 = "greater";

            if (e9.equals("smaller"))
                if (ema9 > ema24 + 1)
                    showNotification(symbol + ":" + time + ":Crossover!");
            if (e9.equals("greater"))
                if (ema9 < ema24 - 2)
                    showNotification(symbol + ":" + time + ":Crossdown!");
        }
    }

    //Alerts
    private void showNotification(String msg) {
        int m = new Random().nextInt(100) + 1;
        int n = new Random().nextInt(100) + 2;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannel.CHANNEL_NOTIFICATION_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL) // for lower versions
                .setSmallIcon(R.drawable.ic_baseline_autorenew_24)
                .setColor(Color.RED)
                .setContentTitle(msg)
//                .setContentText("("+time+")")
//                .setVibrate(new long[]{1000, 1000, 1000})
                .setOnlyAlertOnce(false);
        manager.notify(m + n, builder.build());
    }

    //foreground notification
    private void createNotification() {
        Intent Intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, Intent, 0);

        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this, notificationChannel.CHANNEL_FOREGROUND_ID)
                .setSmallIcon(R.drawable.ic_baseline_autorenew_24)
                .setContentTitle("Indicator Alerts active")
//                .setContentText("click to Stop")
                .setColor(Color.BLUE)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        startForeground(150, builder2.build());
    }
}