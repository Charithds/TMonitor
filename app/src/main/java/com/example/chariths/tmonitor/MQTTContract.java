package com.example.chariths.tmonitor;

/**
 * Created by Charith on 7/21/2017.
 */

public class MQTTContract {
    // Server and topic declarations
    public static final String serverUri = "tcp://broker.hivemq.com:1883";
    public static final String clientID = "iotAndroidsbjkdajsbvkjvkxfgd";
    public final String subscriptionTopic = "ServerMaathrukawa";
    public final String publishTopic = "ClientMaathrukawa";
    public final String publishMessage = "Hello World!";
}
