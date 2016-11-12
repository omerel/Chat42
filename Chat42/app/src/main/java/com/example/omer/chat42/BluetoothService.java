package com.example.omer.chat42;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by omer on 12/11/2016.
 */

public class BluetoothService extends Service {

    private static String LOG_TAG = "BoundService";
    private IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public String getCheck() {
        return "working";
    }

    public class MyBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }


}
