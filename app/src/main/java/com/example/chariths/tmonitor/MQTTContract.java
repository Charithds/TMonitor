package com.example.chariths.tmonitor;

/**
 * Created by Charith on 7/21/2017.
 */

public class MQTTContract {
    // Server and topic declarations
    public static final String serverUri = "tcp://broker.hivemq.com:1883";
    public static final String clientID = "iotAndroidsbjkdajsbvkjvkxfgd";
    public static final String SubscriptionTopicPresent = "ServerMaathrukawa";
    public static final String SubscriptionTopicStat = "Serverthrukawa25893";
    public static final String publishTopic = "ClientMaathrukawa";
    public static final String publishMessage = "Hello World!";
}
