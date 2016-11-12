package com.example.omer.chat42;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by omer on 12/11/2016.
 */

public class BluetoothService extends Service implements Constants {

    // Bluetooth fields
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private BluetoothDevice mConnectedDevice;
    private AcceptThread mServersideThread;
    private ConnectThread mConnectThread;

    private IBinder mBinder = new MyBinder();

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
    }

    public String getCheck() {
        return "working";
    }

    public class MyBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }


    /**
     * Initilaize the bluetooth connection
     */
    private void bluetoothInit() {

        //TODO check if the device is already connected to somebody else
        //TODO add option to disconnect
        //TODO add status line in the bottom screen

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        }

        // Set name for user
        mBluetoothAdapter.setName("user");

        // Init serverside thread
        mServersideThread = new AcceptThread();

    }


    public boolean isEnabled(){
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isDiscovering(){
        return mBluetoothAdapter.isDiscovering();
    }

    public void cancelDiscovery(){
         mBluetoothAdapter.cancelDiscovery();
    }
    public void startDiscovery(){
        mBluetoothAdapter.startDiscovery();
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

        if (mServersideThread.isAlive())
            mServersideThread.cancel();

        mServersideThread = new AcceptThread();
    }

    /** Start discoverable mode **/
    public void startDiscoverable() {mServersideThread.start();}


    /**
     * Turnoff discoverable mode
     */
    public void stopDiscoverable(){
        mServersideThread.cancel();
        mServersideThread = new AcceptThread();
    }


    /**
     * The Handler that gets information back from the threads
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SOCKET_RECEIVED:

                    // close discover devices
                    cancelDiscovery();
                    // close discoverable
                    if (MainActivity.isDiscoverableSwitchOn()){
                        MainActivity.turnOffDiscoverableButton();
                    }
                    // close the server side socket
                    cancelThreadIfAlive();

                    //TODO go to chatActivity and pass the relevant info
                    break;

                case CONNECTING_FAILURE:

                    // close the thread
                    // mConnectThread.cancel();

                    Toast.makeText(getApplicationContext(),"Connecting failed",
                            Toast.LENGTH_LONG).show();
                    break;

                case NO_BLUETOOTH_SUPPORT:

                    break;

            }

        }
    };
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

                    // TODO find who is the device

                    // Send message back to the Activity for a new Socket received
                    mHandler.obtainMessage(Constants.SOCKET_RECEIVED).sendToTarget();
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
                // Send failure message back to the  main Activity
                mHandler.obtainMessage(Constants.CONNECTING_FAILURE).sendToTarget();
                return;
            }
            // update socket
            mBluetoothSocket = mmSocket;

            // update the connected device
            mConnectedDevice = mmDevice;
            // Send message back to the Activity for a new Socket received
            mHandler.obtainMessage(Constants.SOCKET_RECEIVED).sendToTarget();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


}
