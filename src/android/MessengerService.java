package com.log2c.cordova.plugin.mqtt;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessengerService extends Service {
    private static final String TAG = "MessengerService";
    public static final int MESSAGE_FROM_CLIENT = 0x01;
    public static final int MESSAGE_FROM_SERVICE = 0x02;
    private Gson mGson = new Gson();
    private MqttDelegate mMqttClient;
    public static final String INTENT_FILTER_LISTEN = "messenger_intent_filter";

    @Override
    public void onCreate() {
        super.onCreate();
        mMqttClient = new MqttDelegateImp(getApplicationContext());
    }

    /**
     * 处理来自客户端的消息，并用于构建Messenger
     * 构建Messenger对象
     */
    private final Messenger mMessenger = new Messenger(new Handler(new IncomingHandlerCallback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == MESSAGE_FROM_CLIENT) {
                String json = message.getData().getString("msg");
                String action = message.getData().getString("action");
                Log.e(TAG, "receive message from client:" + json);
                if (TextUtils.isEmpty(action) || TextUtils.isEmpty(json)) {
                    return false;
                }
                JSONArray args;
                try {
                    args = new JSONArray(json);
                    if ("listen".equals(action)) {
//                            mMqttClient.listen(callbackContext);
                        //TODO
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
//                        callbackContext.success(String.valueOf(mMqttClient.isConnected()));
                        //TODO
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                super.handleMessage(message);
            }
            return true;
        }
    }));

    @Override
    public IBinder onBind(Intent intent) {
        //将Messenger对象的Binder返回给客户端
        return mMessenger.getBinder();
    }

    static class IncomingHandlerCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message message) {
            return true;
        }
    }
}
