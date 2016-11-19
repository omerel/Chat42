package com.example.omer.chat42;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by omer on 19/11/2016.
 */

public class ChatMessage {

    private String mSenderAddress;
    private String mReceiverAddress;
    private String mMessage;
    private Calendar mDateTime;

    public ChatMessage(String mSenderAddress, String mReceiverAddress, String message, Calendar mDateTime) {
        this.mSenderAddress = mSenderAddress;
        this.mReceiverAddress = mReceiverAddress;
        this.mMessage = message;
        this.mDateTime = mDateTime;
    }

    public String getSenderAddress() {
        return mSenderAddress;
    }

    public void setSenderAddress(String mSenderAddress) {
        this.mSenderAddress = mSenderAddress;
    }

    public String getReceiverAddress() {
        return mReceiverAddress;
    }

    public void setReceiverAddress(String mReceiverAddress) {
        this.mReceiverAddress = mReceiverAddress;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public Calendar getDateTime() {
        return mDateTime;
    }

    public void setDateTime(Calendar mDateTime) {
        this.mDateTime = mDateTime;
    }
}

