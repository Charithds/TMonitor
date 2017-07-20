package com.example.chariths.tmonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    // Server and topic declarations
    private final String serverUri = "tcp://iot.eclipse.org:1833";
    private final String clientID = "iotAndroid";
    private final String subscriptionTopic = "exampleAndroidTopic";
    private final String publishTopic = "exampleAndroidPublishTopic";
    private final String publishMessage = "Hello World!";

    private MqttAndroidClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mqttClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientID);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        try {
            mqttClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        subscribeToTopic();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic() throws MqttException {
        mqttClient.subscribe(subscriptionTopic, 0);
    }

    public void publishMessage() throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(this.publishMessage.getBytes());
        mqttClient.publish(publishTopic, mqttMessage);
    }
}
