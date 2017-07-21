package com.example.chariths.tmonitor;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by Charith on 7/21/2017.
 */

public interface MQTTCallbackClient {
    void onSubscribeSuccess();
    void onSubscribeFailure(IMqttToken asyncActionToken, Throwable exception) ;
    void onConnectionSuccess();
    void onConnectionFaliure();
    void connectionLost(Throwable cause);
    void messageArrived(String topic, MqttMessage message) throws Exception ;
    void deliveryComplete();
}
