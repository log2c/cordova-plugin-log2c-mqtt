package com.log2c.cordova.plugin.mqtt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttDelegateImp implements MqttDelegate, MqttCallback, IMqttActionListener {
    private static final String TAG = MqttDelegateImp.class.getSimpleName();
    private static final long RECONNECT_DELAY = 5 * 1000;
    private MqttAsyncClient mMqttClient;
    private Context mContext;
    private String mIntentFilter;
    private ConnectConfigModel mConfigModel;

    public MqttDelegateImp(Context context) {
        this(context, MessengerService.INTENT_FILTER_LISTEN);
    }

    public MqttDelegateImp(Context context, String intentFilter) {
        this.mContext = context;
        this.mIntentFilter = intentFilter;
    }

    @Override
    public void connect(ConnectConfigModel configModel) {
        mConfigModel = configModel;
        connectInternal();
    }

    private void connectInternal() {
        try {
            if (mMqttClient == null) {
                mMqttClient = new MqttAsyncClient(mConfigModel.getUrl() + ":" + mConfigModel.getPort(), mConfigModel.getClientId(), new MemoryPersistence());
            } else {
                if (mMqttClient.isConnected()) {
                    return;
                }
            }
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAG, "connectInternal: ", e);
        }
        MqttConnectOptions options = new MqttConnectOptions();
        if (!TextUtils.isEmpty(mConfigModel.getUsername()) && !TextUtils.isEmpty(mConfigModel.getPassword())) {
            options.setUserName(mConfigModel.getUsername());
            options.setPassword(mConfigModel.getPassword().toCharArray());
        }
        options.setCleanSession(true);
        options.setConnectionTimeout(mConfigModel.getConnectionTimeout());
        options.setKeepAliveInterval(mConfigModel.getKeepAliveInterval());
        options.setAutomaticReconnect(mConfigModel.isAutomaticReconnect());//设置自动重连
        try {
//            options.setSocketFactory(SslUtil.getSocketFactory("/UserProfile/ca/ca.crt", "/UserProfile/ca/client.crt", "/UserProfile/ca/client.key", "123456"));
            mMqttClient.setCallback(this);
            mMqttClient.connect(options, null, this);
        } catch (Exception e) {
            e.printStackTrace();
            postErrorEvent("connect", e.getMessage());
        }
    }

    @Override
    public boolean subscribe(String topicName, int qos) {
        Log.i(TAG, String.format("subscribe: topic = %s, qos = %d", topicName, qos));
        if (mMqttClient.isConnected()) {
            try {
                mMqttClient.subscribe(topicName, qos);
                return true;
            } catch (MqttException e) {
                e.printStackTrace();
                Log.e(TAG, "subscribe: ", e);
                postErrorEvent("subscribe", e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return mMqttClient != null && mMqttClient.isConnected();
    }

    @Override
    public void disConnect() {
        if (isConnected()) {
            try {
                mMqttClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
                postErrorEvent("disConnect", e.getMessage());
            }
        }
    }

    @Override
    public void unsubscribe(String topicName) {
        if (isConnected()) {
            try {
                mMqttClient.unsubscribe(topicName);
            } catch (MqttException e) {
                e.printStackTrace();
                postErrorEvent("unsubscribe", e.getMessage());
            }
        }
    }

    @Override
    public void publish(String topic, String payload, int qos, boolean retain) {
        if (isConnected()) {
            try {
                mMqttClient.publish(topic, payload.getBytes(), qos, retain);
            } catch (MqttException e) {
                e.printStackTrace();
                postErrorEvent("publish", e.getMessage());
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.i(TAG, "connectionLost: ", cause);
        JsonObject data = new JsonObject();
        postSuccessEvent("connectionLost", data);

        if (!mConfigModel.isAutomaticReconnect()) {
            return;
        }

        new Thread(() -> {
            Log.i(TAG, "connectionLost: 5s后尝试重新连接！");
            try {
                Thread.sleep(Math.max(RECONNECT_DELAY, mConfigModel.getReconnectDelay()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connectInternal();
        }).start();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.i(TAG, String.format("messageArrived: topic = %s, payload = %s", topic, message.toString()));
        JsonObject data = new JsonObject();
        data.addProperty("topic", topic);
        data.addProperty("payload", message.toString());
        data.addProperty("qos", message.getQos());
        data.addProperty("id", message.getId());
        data.addProperty("retained", message.isRetained());
        postSuccessEvent("message", data);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i(TAG, "deliveryComplete: " + token.toString());
        JsonObject data = new JsonObject();
        postSuccessEvent("deliveryComplete", data);
    }

    private void postSuccessEvent(String event, JsonElement data) {
        JsonObject resultData = new JsonObject();
        resultData.addProperty("event", event);
        resultData.add("data", data);
//        try {
//            PluginResult result = new PluginResult(PluginResult.Status.OK, new JSONObject(resultData.toString()));
//            result.setKeepCallback(true);
//            mCallbackContext.sendPluginResult(result);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        sendByBroadcast(mContext, resultData.toString(), true);
    }

    protected void sendByBroadcast(Context context, String data, boolean success) {
        Intent intent = new Intent(mIntentFilter);
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        bundle.putBoolean("success", success);
        intent.putExtras(bundle);
        Log.d(TAG, "sendByBroadcast: " + success);
        context.sendBroadcast(intent);
    }

    private void postErrorEvent(String event, String reason) {
        JsonObject resultData = new JsonObject();
        resultData.addProperty("event", event);
        resultData.addProperty("reason", reason);
//        try {
//            PluginResult result = new PluginResult(PluginResult.Status.ERROR, new JSONObject(resultData.toString()));
//            result.setKeepCallback(true);
////            mCallbackContext.sendPluginResult(result);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        sendByBroadcast(mContext, resultData.toString(), false);
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        postSuccessEvent("connected", new JsonObject());
        Log.i(TAG, "connected");
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//        postSuccessEvent("connectFailure", new JsonObject());
        postErrorEvent("connectFailure", exception.getMessage());
        Log.e(TAG, "connectFailure");
    }
}
