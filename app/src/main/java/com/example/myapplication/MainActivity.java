package com.example.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    BarChart fbc,sbc;
    ViewPager Pager;
    ArrayList<View> Floor;
    ImageView[] ftoilet,stoilet;
    Intent intent;
    Myreceiver myreceiver;
    IntentFilter intentFilter;
    Button fguide,sguide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(MainActivity.this,Mysqlservice.class);
        startService(intent);

        intent = new Intent(MainActivity.this, Mqttservice.class);
        startService(intent);
        intentFilter = new IntentFilter();
        intentFilter.addAction("Mqttf");
        intentFilter.addAction("Mqtts");
        intentFilter.addAction("Mysqlservicemessage");
        myreceiver=new Myreceiver();
        registerReceiver(myreceiver,intentFilter);

        LayoutInflater layoutInflater= LayoutInflater.from(this);
        View firstfloor = layoutInflater.inflate(R.layout.firstfloor,null);
        View secondfloor= layoutInflater.inflate(R.layout.secondfloor,null);
        ftoilet=new ImageView[4];
        stoilet=new ImageView[4];
        ftoilet[0] = firstfloor.findViewById(R.id.toilet1);
        ftoilet[1] = firstfloor.findViewById(R.id.toilet2);
        ftoilet[2] = firstfloor.findViewById(R.id.toilet3);
        ftoilet[3] = firstfloor.findViewById(R.id.toilet4);
        stoilet[0]=secondfloor.findViewById(R.id.toilet1);
        stoilet[1]=secondfloor.findViewById(R.id.toilet2);
        stoilet[2]=secondfloor.findViewById(R.id.toilet3);
        stoilet[3]=secondfloor.findViewById(R.id.toilet4);


        fbc=firstfloor.findViewById(R.id.bc);
        sbc=secondfloor.findViewById(R.id.bc);

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MainActivity.this,ImageViewActivity.class);
                startActivity(intent);
            }
        };
        fguide=firstfloor.findViewById(R.id.guide);
        sguide=secondfloor.findViewById(R.id.guide);
        fguide.setOnClickListener(onClickListener);
        sguide.setOnClickListener(onClickListener);


        Floor = new ArrayList<View>();
        Floor.add(firstfloor);
        Floor.add(secondfloor);
        Pager=findViewById(R.id.pager);
        Pager.setAdapter(new myPagerAdapter(Floor));


    }


    public class Myreceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()=="Mqttf") {//判斷廣播名稱
                String situation = intent.getStringExtra("Floor1");
                for(int i=0;i<4;i++){
                    if(situation.charAt(i)== '1')
                        ftoilet[i].setImageResource(R.drawable.toiletleftbusy);
                    else
                        ftoilet[i].setImageResource(R.drawable.toiletleftok);
                }
            }
            else if(intent.getAction()=="Mqtts"){
                String situation = intent.getStringExtra("Floor2");
                for(int i=0;i<4;i++){
                    if(situation.charAt(i)== '1')
                        stoilet[i].setImageResource(R.drawable.toiletleftbusy);
                    else
                        stoilet[i].setImageResource(R.drawable.toiletleftok);
                }
            }else if(intent.getAction()=="Mysqlservicemessage"){
                String broadcast=intent.getStringExtra("Barvalue");
                String[] bv = broadcast.split(" ");
                String[] first=new String[24];
                String[] second=new String[24];
                for(int i=0;i<24;i++)
                    first[i]=bv[i];
                for(int i=24;i<48;i++)
                    second[i-24]=bv[i];
                Showfirstfloorbarchart(first);
                Showsecondfloorbarchart(second);
            }
        }
    }


    public void Showfirstfloorbarchart(String[] bv){
        ArrayList<BarEntry> arrayList = new ArrayList<BarEntry>();
        for(int i=0;i<24;i++)
            arrayList.add(new BarEntry(i,Integer.parseInt(bv[i])));
        MyBarDataSet barDataSet = new MyBarDataSet(arrayList,"");
        int[] color = {ContextCompat.getColor(this,R.color.green),
                ContextCompat.getColor(this, R.color.yellow),
                ContextCompat.getColor(this,R.color.orange),
                ContextCompat.getColor(this, R.color.red),
                ContextCompat.getColor(this, R.color.blue)};
        barDataSet.setColors(color);
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        BarData barData = new BarData(barDataSet);
        YAxis yAxis= fbc.getAxisRight();
        yAxis.setAxisMaximum(100);
        yAxis.setAxisMinimum(0);
        yAxis.setLabelCount(5,true);
        fbc.setData(barData);
        barData.setDrawValues(false);
        fbc.getAxisLeft().setDrawGridLines(false);
        fbc.getXAxis().setDrawGridLines(false);
        fbc.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        fbc.getDescription().setEnabled(false);
        fbc.getAxisLeft().setDrawLabels(false);
        MyMarkerView myMarkerView =new MyMarkerView(this,R.layout.custommarkerview);
        fbc.setMarker(myMarkerView);
        fbc.invalidate();
    }


    public void Showsecondfloorbarchart(String[] bv){
        ArrayList<BarEntry> arrayList = new ArrayList<BarEntry>();
        for(int i=0;i<24;i++)
            arrayList.add(new BarEntry(i,Integer.parseInt(bv[i])));
        MyBarDataSet barDataSet = new MyBarDataSet(arrayList,"");
        int[] color = {ContextCompat.getColor(this,R.color.green),ContextCompat.getColor(this, R.color.yellow),ContextCompat.getColor(this,R.color.orange),ContextCompat.getColor(this, R.color.red),ContextCompat.getColor(this, R.color.blue)};
        barDataSet.setColors(color);
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        BarData barData = new BarData(barDataSet);
        YAxis yAxis= sbc.getAxisRight();
        yAxis.setAxisMaximum(100);
        yAxis.setAxisMinimum(0);
        yAxis.setLabelCount(5,true);
        sbc.setData(barData);
        barData.setDrawValues(false);
        sbc.getAxisLeft().setDrawGridLines(false);
        sbc.getXAxis().setDrawGridLines(false);
        sbc.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        sbc.getDescription().setEnabled(false);
        sbc.getAxisLeft().setDrawLabels(false);
        MyMarkerView myMarkerView =new MyMarkerView(this,R.layout.custommarkerview);
        sbc.setMarker(myMarkerView);
        sbc.invalidate();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myreceiver);
    }


}
/*Pubbtn.setOnClickListener(new View.OnClickListener() {
            byte[] encodedPayload=new byte[0];
            @Override
            public void onClick(View view) {
                try {
                    encodedPayload=payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    client.publish(topic, message);
                } catch (MqttException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });*/