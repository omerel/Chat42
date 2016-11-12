package com.example.omer.chat42;

import java.util.UUID;

/**
 * Created by omer on 06/11/2016.
 */

public interface Constants {
    // Message types sent from the BluetoothChatService Handler
    int SOCKET_RECEIVED = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;
    int CONNECTING_FAILURE = 6;
    int NO_BLUETOOTH_SUPPORT = 7;

    // Constants that indicate the current mode
    int WIFI_MODE = 0;
    int BLUETOOTH_MODE = 1;
    int REQUEST_ENABLE_BT = 1; // request to enable bluetooth

    // Constants that indicate the current connection state
    int STATE_OFF = 0;
    int STATE_TURNING_OFF = 1;
    int STATE_ON = 2;
    int STATE_TURNING_ON = 3;

    // Constants that indicate the current scan mode
    int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 4;
    int SCAN_MODE_CONNECTABLE = 5;
    int SCAN_MODE_NONE = 6;

    // Constants that indicate the connection status
    int ACTION_ACL_CONNECTED = 7;
    int ACTION_ACL_DISCONNECTED = 8;

    // Unique UUID for this application
    UUID MY_UUID = UUID.fromString("ca87c0d0-afac-11de-8a39-0800200c9a66");
}
