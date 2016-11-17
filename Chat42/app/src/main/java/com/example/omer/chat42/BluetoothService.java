package com.example.omer.chat42;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by omer on 12/11/2016.
 * this service control all the bluetooth connection functions and data transfer functions between
 * two devices that directly connected
 */

public class BluetoothService extends Service implements Constants {

    // Bluetooth fields
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private BluetoothDevice mConnectedDevice;
    private AcceptThread mServersideThread = null;
    private ConnectThread mConnectThread;

    private IBinder mBinder = new MyBinder();
    // Messenger that receive messages from activity;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    // activity's messenger
    private Messenger mActivityMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothInit();
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
        if(mServersideThread.isAlive())
            mServersideThread.cancel();
        if(mConnectThread.isAlive())
            mConnectThread.cancel();
    }

    public class MyBinder extends Binder {

        Messenger getMessenger(){
            return mMessenger;
        }
        BluetoothService getService() {
            return BluetoothService.this;
        }

    }

    /**
     * Initilaize the bluetooth connection
     */
    private void bluetoothInit() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Set name for user
     */
    public void setName(String name){
        mBluetoothAdapter.setName(name);
    }

    public String getName(){
        return mBluetoothAdapter.getName();
    }

    public boolean isEnabled(){
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isDiscovering(){
        return mBluetoothAdapter.isDiscovering();
    }

    /**
     * Connect to Selected device
     * @param bluetoothDevice
     */
    public void connectToDevice(BluetoothDevice bluetoothDevice){
        mConnectThread = new ConnectThread(bluetoothDevice);
        mConnectThread.start();
    }

    /**
     *  Destroy thread if it's alive
     */
    public void cancelThreadIfAlive(){
        if (mServersideThread != null)
            if (mServersideThread.isAlive())
                mServersideThread.cancel();
    }

    /**
     *  Thread connecting as a server side
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("RELAY", MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {

                    // update socket
                    mBluetoothSocket = socket;

                    mConnectedDevice = mBluetoothSocket.getRemoteDevice();

                    // Send message back to the Activity connecting succeeds
                  //  mHandler.obtainMessage(Constants.CONNECTING_SUCCEEDS).sendToTarget();
                    sendRequestToActivity(CONNECTING_SUCCEEDS);
                }
            }
        }
        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }


    /**
     *  Thread connecting as a client side
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {

            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out

                // close the thread
                mConnectThread.cancel();

                // Send message back to the Activity connecting failed
               // mHandler.obtainMessage(Constants.CONNECTING_FAILURE).sendToTarget();
                sendRequestToActivity(CONNECTING_FAILURE);

                return;
            }
            // update socket
            mBluetoothSocket = mmSocket;

            // update the connected device
            mConnectedDevice = mmDevice;

            // Send message back to the Activity connecting succeeds
           // mHandler.obtainMessage(Constants.CONNECTING_SUCCEEDS).sendToTarget();
            sendRequestToActivity(CONNECTING_SUCCEEDS);

        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

/*
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTING_SUCCEEDS:
                    sendRequestToActivity(CONNECTING_SUCCEEDS);
                   break;
                case CONNECTING_FAILURE:
                    sendRequestToActivity(CONNECTING_FAILURE);
                    break;

            }

        }
    };
*/
    /**
     * Handler of incoming messages from activity
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER_ACTIVITY:
                    mActivityMessenger = msg.replyTo;
                    break;
                case CANCEL_DISCOVERY:
                    mBluetoothAdapter.cancelDiscovery();
                break;
                case START_DISCOVERY:
                    mBluetoothAdapter.startDiscovery();
                    break;
                case STOP_DISCOVERABLE:
                    mServersideThread.cancel();
                    break;
                case START_DISCOVERABLE:
                    mServersideThread = new AcceptThread();
                    mServersideThread.start();
                break;
                case CLOSE_SOCKET:

                  //  unpairDevice(mConnectedDevice);

                    try {
                        mBluetoothSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cancelThreadIfAlive();

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Send string Message value to activity
     */
    private void sendMessageToActivity(String message)  {

        // Send data as a String
        Bundle bundle = new Bundle();
        bundle.putString("string",message);
        Message msg = Message.obtain(null, INCOMING_MSG);
        msg.setData(bundle);
        try {
            mActivityMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
    *   Send request to activity
    */
    private void sendRequestToActivity(int request)  {
        try {
            mActivityMessenger.send(Message.obtain(null, request));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
