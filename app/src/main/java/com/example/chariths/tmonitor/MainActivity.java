package com.example.chariths.tmonitor;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private MqttAndroidClient mqttClient;
    private EditText messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageView = (EditText) findViewById(R.id.messageView);
        mqttClient = new MqttAndroidClient(getApplicationContext(), MQTTContract.serverUri, "1780662352");
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                messageView.setText(messageView.getText()+"\nConnection Lost!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                messageView.setText(messageView.getText()+"\nMessage arrived : "+ new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                messageView.setText(messageView.getText()+"\nDelivered!");
            }
        });
    }

    public void connect(View v) throws MqttException {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);

        mqttClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {

                try {
                    subscribeToTopic();
                } catch (MqttException e) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Toast.makeText(getApplicationContext(), "Faliure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void subscribeToTopic() throws MqttException {
        final IMqttToken token = mqttClient.subscribe(MQTTContract.SubscriptionTopicPresent, 0);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                messageView.setText(messageView.getText()+ "\nSubd successfull!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                messageView.setText(messageView.getText()+"\nConnection Failed!");
            }
        });
    }

    public void unsubscribeTopic(){
        try {
            IMqttToken unsubToken = mqttClient.unsubscribe(MQTTContract.SubscriptionTopicPresent);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage() throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload("Hello".getBytes());
        mqttClient.publish(MQTTContract.publishTopic, mqttMessage);
    }

    @Override
    protected void onPause(){
        unsubscribeTopic();
        super.onPause();
    }

    protected void onResume(){

        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                subscribeToTopic();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        super.onResume();
    }

    public void nextPage(View v){
        Intent nextPage = new Intent(this, StatiscicsActivity.class);
        startActivity(nextPage);
    }
}
