package com.example.omer.chat42;

import java.util.UUID;

/**
 * Created by omer on 06/11/2016.
 */

public interface Constants {

    // Message types communication between two Bluetooth devices
    int SOCKET_RECEIVED = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;
    int NO_BLUETOOTH_SUPPORT = 7;


    // Message types communication between BluetoothChatService incomingHandler to activity
    int REGISTER_ACTIVITY = 10;
    int INCOMING_MSG = 11;
    int CONNECTING_FAILURE = 12;
    int CANCEL_DISCOVERY = 13;
    int START_DISCOVERY = 14;
    int STOP_DISCOVERABLE = 15;
    int START_DISCOVERABLE = 16;
    int CONNECTING_SUCCEEDS =17;
    int CLOSE_SOCKET = 18;

    // Constants that indicate the current mode
    int WIFI_MODE = 21;
    int BLUETOOTH_MODE = 22;

    // request to enable bluetooth
    int REQUEST_ENABLE_BT = 23;

    // Constants that indicate the current connection state
    int STATE_OFF = 30;
    int STATE_TURNING_OFF = 31;
    int STATE_ON = 32;
    int STATE_TURNING_ON = 33;
    // Constants that indicate the current scan mode
    int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 34;
    int SCAN_MODE_CONNECTABLE = 35;
    int SCAN_MODE_NONE = 36;
    // Constants that indicate the connection status
    int ACTION_ACL_CONNECTED = 37;
    int ACTION_ACL_DISCONNECTED = 38;

    // Unique UUID for this application
    UUID MY_UUID = UUID.fromString("ca87c0d0-afac-11de-8a39-0800200c9a66");
}
