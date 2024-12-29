package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Mysqlservice extends Service implements Response.Listener,Response.ErrorListener{
    private static final String TAG = "Mysqlservice";
    private final String url= "https://f91f-2001-b400-e456-9ecd-ad92-4654-9e37-e885.ngrok.io/mqtt/schedule.php";
    public Mysqlservice() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // ...
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,this,this);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    @Override
    public void onResponse(Object response) {
        Log.i(TAG,"Mysql Connection Success.");
        Intent intent = new Intent("Mysqlservicemessage").putExtra("Barvalue",(String)response);
        sendBroadcast(intent);
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        Log.i(TAG,"failed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}