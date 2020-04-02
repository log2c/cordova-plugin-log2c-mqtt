package com.log2c.cordova.plugin.mqtt;

public interface MqttDelegate {

    void connect(ConnectConfigModel configModel);

    boolean subscribe(String topicName, int qos);

    boolean isConnected();

    void disConnect();

    void unsubscribe(String topicName);

    void publish(String topic, String payload, int qos, boolean retain);
}
