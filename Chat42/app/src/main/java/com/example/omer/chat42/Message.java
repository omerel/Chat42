package com.example.omer.chat42;

import java.util.Date;

/**
 * Created by omer on 19/11/2016.
 */

public class Message {

    private int mSenderAddress;
    private int mReceiverAddress;
    private String mMessage;
    private Date mDateTime;

    public Message(int mSenderAddress, int mReceiverAddress, String message, Date mDateTime) {
        this.mSenderAddress = mSenderAddress;
        this.mReceiverAddress = mReceiverAddress;
        this.mMessage = message;
        this.mDateTime = mDateTime;
    }

    public int getmSenderAddress() {
        return mSenderAddress;
    }

    public void setmSenderAddress(int mSenderAddress) {
        this.mSenderAddress = mSenderAddress;
    }

    public int getmReceiverAddress() {
        return mReceiverAddress;
    }

    public void setmReceiverAddress(int mReceiverAddress) {
        this.mReceiverAddress = mReceiverAddress;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public Date getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(Date mDateTime) {
        this.mDateTime = mDateTime;
    }
}

