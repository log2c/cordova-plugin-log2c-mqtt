package com.log2c.cordova.plugin.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MqttPlugin extends CordovaPlugin {
    private static final String TAG = MqttPlugin.class.getSimpleName();
    private CallbackContext listenCallback;
    private boolean isConnected = false;

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        cordova.getActivity().registerReceiver(messengerReceiver,
                new IntentFilter(MessengerService.INTENT_FILTER_LISTEN));
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        Log.d(TAG, "execute: " + action);
        if ("listen".equals(action)) {
            listenCallback = callbackContext;
            return true;
        } else if ("isConnected".equals(action)) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, isConnected);
            callbackContext.sendPluginResult(result);
            return true;
        }
        Message message = Message.obtain(null, MessengerService.MESSAGE_FROM_CLIENT);
        Bundle bundle = new Bundle();
        bundle.putString("msg", args.toString());
        bundle.putString("action", action);
        message.setData(bundle);
        try {
            MqttHelper.getInstance().getService().send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG, "execute#send: ", e);
        }
        return true;
    }

    private BroadcastReceiver messengerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final boolean success = intent.getBooleanExtra("success", false);
            final String json = intent.getStringExtra("data");
            Log.d(TAG, "onReceive: " + json);
            if (TextUtils.isEmpty(json)) {
                return;
            }
            if (listenCallback == null) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(json);

                String event = jsonObject.getString("event");
                if ("connected".equals(event)) {
                    isConnected = true;
                } else if ("connectionLost".equals(event)) {
                    isConnected = false;
                }
                PluginResult result;
                if (success) {
                    result = new PluginResult(PluginResult.Status.OK, jsonObject);
                    listenCallback.success(jsonObject);
                } else {
                    result = new PluginResult(PluginResult.Status.ERROR, jsonObject);
                    listenCallback.error(jsonObject);
                }
                result.setKeepCallback(true);
                listenCallback.sendPluginResult(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

}
