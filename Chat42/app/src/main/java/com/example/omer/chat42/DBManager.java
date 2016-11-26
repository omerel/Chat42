package com.example.omer.chat42;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * Created by omer on 20/11/2016.
 */

public class DBManager extends SQLiteOpenHelper implements Constants{

    public static final String TABLE = "messages";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String COLUMN_SENDER = "sender";
    public static final String COLUMN_RECEIVER = "receiver";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_DATE = "time";
    public static final String COLUMN_IMAGE = "image";
    private HashMap hp;

    public DBManager(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table "+TABLE+
                        " (id integer primary key, sender text,receiver text,message text,time text,image blob)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }

    public void insertMessage (ChatMessage chatMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MESSAGE, chatMessage.getMessage());
        contentValues.put(COLUMN_SENDER, chatMessage.getSenderAddress());
        contentValues.put(COLUMN_RECEIVER, chatMessage.getReceiverAddress());
        contentValues.put(COLUMN_DATE, convertDateToString(chatMessage.getDateTime()));
        contentValues.put(COLUMN_IMAGE, getBytes(chatMessage.getPicture()));

        db.insert(TABLE, null, contentValues);
    }

    private String convertDateToString(Date dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_TEMPLATE);
        String date = sdf.format(dateTime);
        return date;
    }

    private Date convertStringToDate(String dateTime){

        SimpleDateFormat format = new SimpleDateFormat(TIME_TEMPLATE);
        Date date = null;
        try {
            date = format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE+" where id="+id+"", null );
        return res;
    }


    public boolean updateMessage (Integer id, ChatMessage chatMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MESSAGE, chatMessage.getMessage());
        contentValues.put(COLUMN_SENDER, chatMessage.getSenderAddress());
        contentValues.put(COLUMN_RECEIVER, chatMessage.getReceiverAddress());
        contentValues.put(COLUMN_DATE, convertDateToString(chatMessage.getDateTime()));
        db.update(TABLE, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteMessage(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("messages",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }


    public void deleteDataBase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE);
    }


    public void deleteAllMessages(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
         db.execSQL( "delete from "+TABLE+" where "+COLUMN_RECEIVER+" = '"+address+"'");
        db.execSQL( "delete from "+TABLE+" where "+COLUMN_SENDER+" = '"+address+"'");
    }

    public ArrayList<ChatMessage> getAllMessages(String connectedDeviceAddress) {
        ArrayList<ChatMessage> array_list = new ArrayList<ChatMessage>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE+" where "+COLUMN_SENDER+" = '"+
                connectedDeviceAddress+"' or "+COLUMN_RECEIVER+
                " = '"+connectedDeviceAddress+"'", null );
              //  " or receiver = "+connectedDeviceAddress+"", null );
        res.moveToFirst();


        while(res.isAfterLast() == false){

            String message = res.getString(res.getColumnIndex(COLUMN_MESSAGE));
            String sender = res.getString(res.getColumnIndex(COLUMN_SENDER));
            String receiver = res.getString(res.getColumnIndex(COLUMN_RECEIVER));
            Date date = convertStringToDate(res.getString(res.getColumnIndex(COLUMN_DATE)));
            Bitmap image = getImage(res.getBlob(res.getColumnIndex(COLUMN_IMAGE)));
            ChatMessage tempChatMessage = new ChatMessage(sender,receiver,message,image,date);
            array_list.add(tempChatMessage);
            res.moveToNext();
        }
        return array_list;
    }

    // convert from bitmap to byte array
    public  byte[] getBytes(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public  Bitmap getImage(byte[] image) {
        if (image == null)
            return null;
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
