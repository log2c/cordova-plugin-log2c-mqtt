package com.log2c.cordova.plugin.mqtt;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

public class MqttHelper {
    private static final String TAG = MqttHelper.class.getSimpleName();
    private static MqttHelper mMqttHelper = null;
    private Messenger mService;
    private Application mApplication;

    private MqttHelper() {
    }

    public MqttHelper init(Application application) {
        mApplication = application;
        Intent intent = new Intent(mApplication, MessengerService.class);
        mApplication.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        return this;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e(TAG, "ServiceConnection-->" + System.currentTimeMillis());
            mService = new Messenger(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e(TAG, "onServiceDisconnected-->binder died");
        }
    };

    public static MqttHelper getInstance() {
        if (mMqttHelper == null) {
            mMqttHelper = new MqttHelper();
        }
        return mMqttHelper;
    }

    public Messenger getService() {
        return mService;
    }
}
