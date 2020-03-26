package com.log2c.cordova.plugin.mqtt;

import org.apache.cordova.CallbackContext;

public interface MqttDelegate {
    void listen(CallbackContext callbackContext);

    void connect(ConnectConfigModel configModel);

    boolean subscribe(String topicName, int qos);

    boolean isConnected();

    void disConnect();

    void unsubscribe(String topicName);

    void publish(String topic, String payload, int qos, boolean retain);
}
