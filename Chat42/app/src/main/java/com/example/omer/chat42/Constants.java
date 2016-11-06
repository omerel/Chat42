package com.example.omer.chat42;

/**
 * Created by omer on 06/11/2016.
 */

public interface Constants {
    // Message types sent from the BluetoothChatService Handler
    public static final int SOCKET_RECEIVED = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int CONNECTING_FAILURE = 6;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
}
