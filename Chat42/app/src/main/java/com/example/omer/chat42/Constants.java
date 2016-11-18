package com.example.omer.chat42;

import java.util.UUID;

/**
 * Created by omer on 06/11/2016.
 */

public interface Constants {

    // Message types communication between two Bluetooth devices
    int MESSAGE_READ = 1;
    int MESSAGE_WRITE = 2;
    int MESSAGE_DEVICE_NAME = 3;

    // Message types communication between BluetoothChatService incomingHandler to activity
    int REGISTER_ACTIVITY = 11;
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


    int TEST_MSG_OUT =123;
    int TEST_MSG_IN =124;


    // Unique UUID for this application
    UUID MY_UUID = UUID.fromString("ca87c0d0-afac-11de-8a39-0800200c9a66");
}
