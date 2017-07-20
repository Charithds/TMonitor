package com.example.chariths.tmonitor;

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

    // Server and topic declarations
    private final String serverUri = "tcp://broker.hivemq.com:1883";
    private final String clientID = "iotAndroidsbjkdajsbvkjvkxfgd";
    private final String subscriptionTopic = "ServerMaathrukawa";
    private final String publishTopic = "ClientMaathrukawa";
    private final String publishMessage = "Hello World!";

    private MqttAndroidClient mqttClient;
    private EditText messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageView = (EditText) findViewById(R.id.messageView);
        mqttClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientID);
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
        //Toast.makeText(getApplicationContext(), "Button Pressed", Toast.LENGTH_SHORT).show();
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
        final IMqttToken token = mqttClient.subscribe(subscriptionTopic, 0);
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

    public void publishMessage() throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(this.publishMessage.getBytes());
        mqttClient.publish(publishTopic, mqttMessage);
    }
}
