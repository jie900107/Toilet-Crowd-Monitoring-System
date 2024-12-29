package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Mqttservice extends Service implements IMqttActionListener,MqttCallback{
    private static final String TAG = "Mqttservice";
    String serverURI="tcp://broker.hivemq.com:1883";
    String clientId= MqttClient.generateClientId();
    String topic="toilet/+";
    String fFloor="0000";
    String sFloor="0000";
    int qos=1;
    final MqttAndroidClient client = new MqttAndroidClient(Mqttservice.this, serverURI,clientId);
    @Override
    public void onCreate() {
        super.onCreate();
        try{
            IMqttToken contoken = client.connect();
            contoken.setActionCallback(this);
            client.setCallback(this);
        }catch(MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        try {
            client.subscribe(topic,qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        Log.d("TAG","Mqtt Connection success.");
    }
    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.d("TAG","Mqtt Connection fail.");
    }
    @Override
    public void connectionLost(Throwable cause) {

    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String situation=message.toString();
        if(topic.equals("toilet/F1")){
            fFloor=situation;
            Intent intent = new Intent("Mqttf").putExtra("Floor1",fFloor);
            sendBroadcast(intent);
        }
        else{
            sFloor=situation;
            Intent intent = new Intent("Mqtts").putExtra("Floor2",sFloor);
            sendBroadcast(intent);
        }
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
/*if(client.isConnected()){
            try {
                IMqttToken subtoken =
                subtoken.setActionCallback(this);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }*/
