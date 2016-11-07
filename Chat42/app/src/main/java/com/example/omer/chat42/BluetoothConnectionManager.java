package com.example.omer.chat42;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;

/**
 * Created by omer on 07/11/2016.
 * this class does all the work with connectivity with bluetooth. all the process that happen until
 * the connection success with two devices
 */

public class BluetoothConnectionManager {

    // Member fields
     private Handler mHandler;

    BluetoothConnectionManager( Handler handler){

        this.mHandler = handler;
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
}
