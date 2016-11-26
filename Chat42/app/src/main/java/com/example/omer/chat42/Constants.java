package com.example.omer.chat42;

import java.util.UUID;

/**
 * Created by omer on 06/11/2016.
 */

public interface Constants {

    // ChatMessage types communication between two Bluetooth devices
    int MESSAGE_READ = 1;
    int MESSAGE_WRITE = 2;
    int PICTURE_WRITE = 3;
    int PICTURE_READ = 4;

    // ChatMessage types communication between BluetoothChatService incomingHandler to activity
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

    // Unique UUID for this application
    UUID MY_UUID = UUID.fromString("ca87c0d0-afac-11de-8a39-0800200c9a66");


    // DataBase name
    String DATABASE_NAME = "chat42.db";
    String TIME_TEMPLATE = "DD/MM/yyyy  hh:mm:ss";

    // Code intents
    int CAMERA = 30;
    int GALLERY = 31;

    // Login const
    int MALE = 1;
    int FEMALE = 2;
    int INMALE = 3;
    int INFEMALE = 4;
    int STANDART = 5;
    int RIDE = 6;
    int BAR = 7;

    // Shared preference
    String SHARED_PREFERENCE = "myData";


    int MAX_CHAR = 255;
}
