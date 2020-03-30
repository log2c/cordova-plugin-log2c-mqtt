package com.log2c.cordova.plugin.mqtt;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MqttHelper.getInstance().init(this);
    }
}
