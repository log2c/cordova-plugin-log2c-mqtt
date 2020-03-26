package com.log2c.cordova.plugin.mqtt;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MqttPlugin extends CordovaPlugin {
    private static final String TAG = MqttPlugin.class.getSimpleName();
    private Gson mGson = new Gson();
    private MqttDelegate mMqttClient = new MqttDelegateImp();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("listen".equals(action)) {
            mMqttClient.listen(callbackContext);
        } else if ("connect".equals(action)) {
            JSONObject config = args.getJSONObject(0);
            Log.i(TAG, "connect: " + args.toString());
            ConnectConfigModel connectConfig = mGson.fromJson(config.toString(), ConnectConfigModel.class);
            mMqttClient.connect(connectConfig);
        } else if ("subscribe".equals(action)) {
            JSONObject config = args.getJSONObject(0);
            String topicName = config.getString("topic");
            int qos = config.getInt("qos");
            mMqttClient.subscribe(topicName, qos);
        } else if ("disconnect".equals(action)) {
            mMqttClient.disConnect();
        } else if ("unsubscribe".equals(action)) {
            final String topicName = args.getString(0);
            mMqttClient.unsubscribe(topicName);
        } else if ("publish".equals(action)) {
            JSONObject obj = args.getJSONObject(0);
            final String topicName = obj.getString("topic");
            final String payload = obj.getString("payload");
            final int qos = obj.getInt("qos");
            final boolean retain = obj.getBoolean("retain");
            mMqttClient.publish(topicName, payload, qos, retain);
        } else if ("isConnected".equals(action)) {
            callbackContext.success(String.valueOf(mMqttClient.isConnected()));
        }
        return true;
    }
}
