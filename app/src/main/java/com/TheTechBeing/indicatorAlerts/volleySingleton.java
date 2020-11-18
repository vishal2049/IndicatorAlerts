package com.TheTechBeing.indicatorAlerts;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class volleySingleton {

    private RequestQueue mrequestQueue;
    private static volleySingleton mInstance;

    public volleySingleton(Context context) {
        mrequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized volleySingleton getInstance(Context context)
    {
        if(mInstance == null)
        {
            mInstance = new volleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getMrequestQueue(){
        return mrequestQueue;
    }
}

