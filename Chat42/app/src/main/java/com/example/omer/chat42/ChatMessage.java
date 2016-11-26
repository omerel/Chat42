package com.example.omer.chat42;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by omer on 19/11/2016.
 */

public class ChatMessage {

    private String mSenderAddress;
    private String mReceiverAddress;
    private String mMessage;
    private Bitmap mPicture;
    private Date mDateTime;

    public ChatMessage(String mSenderAddress, String mReceiverAddress, String message,Bitmap picture, Date mDateTime) {
        this.mSenderAddress = mSenderAddress;
        this.mReceiverAddress = mReceiverAddress;
        this.mMessage = message;
        this.mDateTime = mDateTime;
        this.mPicture = picture;
    }


    public String getSenderAddress() {
        return mSenderAddress;
    }

    public Bitmap getPicture() {
        return mPicture;
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

    public Date getDateTime() {
        return mDateTime;
    }

    public void setDateTime(Date mDateTime) {
        this.mDateTime = mDateTime;
    }
}

