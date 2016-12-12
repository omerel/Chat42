package com.example.omer.chat42;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Notification.VISIBILITY_PUBLIC;

public class ChatActivity extends AppCompatActivity  implements View.OnClickListener,Constants {

    // Layout's views
    private EditText mCommandChat;
    private ImageButton mSendButton;
    private ImageButton mSendPicture;
    private ListView mListViewChat;

    // fileds
    private Toolbar mMyToolbar;
    private String mDeviceAddress;
    private String mConnectedDeviceAddress;
    private static BroadcastReceiver mReceiver;
    private static IntentFilter mIntentFilter;
    private boolean mServiceBound = false;
    private static BluetoothService mBluetoothService;
    private String mUserName;
    private String mConnectedName;
    private int mConnectedGender;
    private ChatMessage mTempMessage;
    private ChatAdapter mChatAdapter;
    private ArrayList<ChatMessage> mChatDialogList;
    private boolean mIsInFront;
    private DBManager mDBManager;
    private int mGender;
    private Bitmap mProfilePicture;
    private Bitmap mConnectedProfilePicture;
    private Bitmap mTempPicture; // save the picture from actionResult
    private boolean isFirstPicturReceived = true;  // to define between profile picture to msgPic

    protected final Messenger mMessenger = new Messenger(new IncomingHandler());
    private static Messenger mServiceMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // start bluetooth service and bind it to this activity
        Intent intent = new Intent(this, BluetoothService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        // set off discoverability
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION_STOP);
        startActivity(discoverableIntent);

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsInFront = true;

    }

    @Override
    public void onPause() {
        super.onPause();
        mIsInFront = false;
    }

    private void generalInit() {

        // Set profile picture
        // TODO get extra picture
        mConnectedProfilePicture = null;

        loadSharedPreferences();

        // setup toolbar
        mMyToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mMyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat with "+mConnectedName);

        // Bind layout's view to class
        mCommandChat = (EditText)findViewById(R.id.editText_command_chat);
        mSendButton = (ImageButton)findViewById(R.id.imageButton_send_msg);
        mListViewChat = (ListView)findViewById(R.id.listview_chat);
        mSendPicture = (ImageButton)findViewById(R.id.imageButton_send_pic);

        // Init ChatDialog list
        mChatDialogList =  new ArrayList<ChatMessage>();
        mChatAdapter = new ChatAdapter(this,mChatDialogList,mMessenger);


        mListViewChat.setAdapter(mChatAdapter);

        // update address in adapter
        mChatAdapter.setMyAddress(mDeviceAddress);

        // Set on click listener
        mSendButton.setOnClickListener(this);
        mSendPicture.setOnClickListener(this);

        // Create a BroadcastReceiver
        createBroadcastReceiver();

        // setup Data base
        mDBManager = new DBManager(this);

        // restore chat history
        restoreChatHistory();

        // register to Receiver
        mIntentFilter = new IntentFilter();
        // Add all all the actions to filter
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mReceiver,mIntentFilter);

        // wait 1 second and the send profile picture (because handler not ready yet)
        Delay start = new Delay(this);
        start.execute();
    }


    private void loadSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE, 0);
       // mConnectedName = sharedPref.getString("DEVICE_CONNECTED_NAME", "user");
        mGender = sharedPref.getInt("GENDER", MALE);
        mUserName = sharedPref.getString("NAME","user");
        mProfilePicture = getIntent().getParcelableExtra("IMAGE");
        getDeviceNameAndGender();
    }
        /**
         *  Create  Exit alert dialog
         */
    private AlertDialog createExitAlertDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Quit");
        alertDialog.setMessage("Are you sure you want to quit?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                        System.exit(0);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        return alertDialog;
    }

    private void openDialogWithPicture(){
        // TODO
        AlertDialog.Builder builderType = new AlertDialog.Builder(this);
        builderType.setTitle(mConnectedName+" picture :");
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.profile_picture,null);
        ImageView im = (ImageView)view.findViewById(R.id.imageButtonProfile);
        im.setImageBitmap(mConnectedProfilePicture);
        builderType.setView(view);
        builderType.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int something) {
            }
        });
        builderType.show();
    }

    /**
     *  Create home alert dialog
     */
    private AlertDialog createHomeAlertDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Exit");
        alertDialog.setMessage("Are you sure you want finish chat?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        goToMainActivity();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        return alertDialog;
    }

    /**
     *  Create general alert dialog
     */
    private AlertDialog createGenralAlertDialog(String content) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Notice");
        alertDialog.setMessage(content);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        return alertDialog;
    }

    /**
     * Make the action button appear in old devices
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // update menu titles and icons
        MenuItem menuIconMode = menu.findItem(R.id.action_mode_logo);
        menuIconMode.setIcon(R.drawable.bluetooth_connected);

        // hide setting
       menu.findItem(R.id.action_settings).setVisible(false);

        if (mConnectedGender == MALE)
            menu.findItem(R.id.action_profile).setIcon(R.drawable.boy);
        else
            menu.findItem(R.id.action_profile).setIcon(R.drawable.girl);


        return super.onCreateOptionsMenu(menu);
    }



    /**
     * Listener to the menu
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // show profile picture
            case R.id.action_profile:
                //TODO
                    openDialogWithPicture();
                return true;

            // show icon status
            case R.id.action_mode_logo:
                    Toast.makeText(this, "You are connected to "+mConnectedName, Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_settings:
                //TODO
                return true;

            // Clear chat history
            case R.id.action_chat_history:
                mDBManager.deleteAllMessages(mConnectedDeviceAddress);
                mChatDialogList.clear();
                mChatAdapter.notifyDataSetChanged();
                return true;

            // Log out from the current user
            case R.id.action_logout:
                logOut();
                return true;

            // go back;
            case android.R.id.home:
                createHomeAlertDialog();
                return true;
            // Exit app
            case R.id.action_exit:
                createExitAlertDialog();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.imageButton_send_msg):
                String content = mCommandChat.getText().toString();
                mCommandChat.setText("");
                if (!content.isEmpty()){
                    // send message to other service
                    sendMessageToService(content);

                    // set current time
                    Date date = Calendar.getInstance().getTime();

                    // create message;
                    mTempMessage = new ChatMessage(mDeviceAddress,mConnectedDeviceAddress,"Me:\n    "+content,null,date) ;

                    // add message to data base
                    mDBManager.insertMessage(mTempMessage);

                    //  add message to listview
                    addMessageToConversation(mTempMessage);
                }
                break;
            case (R.id.imageButton_send_pic):
                getPictureFromGallery();
                break;
        }
    }

    private void addMessageToConversation(ChatMessage chatMessage) {

        // add to adapter
        mChatAdapter.add(chatMessage);
        mChatAdapter.notifyDataSetChanged();
        mListViewChat.setSelection(mListViewChat.getAdapter().getCount()-1);
    }

    /**
     *  Create bounding between service class to this activity
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.MyBinder myBinder = (BluetoothService.MyBinder) service;
            mBluetoothService = myBinder.getService();
            mServiceMessenger = myBinder.getMessenger();
            mServiceBound = true;
            // register Messenger in the service
            registerToServiceMessenger();
            // get devices addresses
            mDeviceAddress = mBluetoothService.getAddress();
            mConnectedDeviceAddress = mBluetoothService.getConnectedAddress();

            // General init
            generalInit();

        }
    };

    /**
     * TODO
     * this method will connect to dataBase and will restore the chat history between the two device
     */
    private void restoreChatHistory() {

        ArrayList<ChatMessage> array = mDBManager.getAllMessages(mConnectedDeviceAddress);
        mChatAdapter.add(array);

    }


    private void goToMainActivity() {

        sendRequestToService(CLOSE_SOCKET);

        unregisterReceiver(mReceiver);

        unbindService(mServiceConnection);
        mServiceBound = false;

        finish();

        Intent goToMainActivity = new Intent(this,MainActivity.class);
        // pass the image
        goToMainActivity.putExtra("IMAGE", mProfilePicture);
        startActivity(goToMainActivity);
    }

    private void logOut() {

        sendRequestToService(CLOSE_SOCKET);

        unregisterReceiver(mReceiver);

        unbindService(mServiceConnection);
        mServiceBound = false;

        finish();

        Intent goToMainActivity = new Intent(this,LoginActivity.class);
        startActivity(goToMainActivity);
    }

    private void goToSettingActivity() {
        Intent goToSettingActivity = new Intent(this,SettingActivity.class);
        goToSettingActivity.putExtra("ACTIVITY","chat");
       // goToSettingActivity.putExtra("PICTURE",pic);
        startActivity(goToSettingActivity);
    }

    /**
     * Handler of incoming messages from service
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    String content = msg.getData().getString("string");

                        // set current time
                        Date date = Calendar.getInstance().getTime();
                        // create message;
                        mTempMessage = new ChatMessage(mConnectedDeviceAddress, mDeviceAddress, mConnectedName + ":\n   " + content, null, date);

                        mDBManager.insertMessage(mTempMessage);

                        addMessageToConversation(mTempMessage);

                        // notify message arrived
                        notifyMessageArrived(content);
                    break;

                case PICTURE_READ:

                    byte[] picContent = msg.getData().getByteArray("picture");

                    Bitmap picture =  getBitmapFromBytes(picContent);

                    if (isFirstPicturReceived){
                        // Set picture on profile picture
                        mConnectedProfilePicture = picture ;

                        isFirstPicturReceived = false;
                    }
                    else {
                        // set current time
                        Date datePic = Calendar.getInstance().getTime();

                        // create message;
                        mTempMessage = new ChatMessage(mConnectedDeviceAddress, mDeviceAddress, mConnectedName + ":" , picture, datePic);

                        mDBManager.insertMessage(mTempMessage);

                        addMessageToConversation(mTempMessage);

                        // notify message arrived
                        notifyMessageArrived("Picture arrived");
                    }
                    break;

                case OPEN_PICTURE:
                    Bitmap profilePic = msg.getData().getParcelable("picture");
                    openPicture(profilePic);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    public void openPicture(Bitmap pic){

        // TODO
        AlertDialog.Builder builderType = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.profile_picture,null);
        ImageView im = (ImageView)view.findViewById(R.id.imageButtonProfile);
        im.setImageBitmap(pic);
        builderType.setView(view);
        builderType.show();

    }
    /**
     * Send string ChatMessage value to service
     */
    private void sendMessageToService(String message)  {

        // Send data as a String
        Bundle bundle = new Bundle();
        bundle.putString("string",message);
        Message msg = Message.obtain(null, MESSAGE_WRITE);
        msg.setData(bundle);
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send Bitmap value to service
     */
    private void sendPictureToService(Bitmap picMessage)  {

        // Send data as a String
        Bundle bundle = new Bundle();
        bundle.putByteArray("picture",getBytesFromBitmap(picMessage));
        Message msg = Message.obtain(null, PICTURE_WRITE);
        msg.setData(bundle);
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,stream);

       // createGenralAlertDialog("10% -"+String.valueOf(stream.toByteArray().length));

        return stream.toByteArray();
    }

    public Bitmap getBitmapFromBytes(byte[] byteArray){
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
       // createGenralAlertDialog("received- "+String.valueOf(byteArray.length));
        return bitmap;
    }

    /**
     *   Send request to service
     */
    private void sendRequestToService(int request)  {
        try {
            mServiceMessenger.send(Message.obtain(null, request));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void registerToServiceMessenger(){
        Message msg = Message.obtain(null, REGISTER_ACTIVITY);
        msg.replyTo = mMessenger;
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     *  Notify when new message arrived
     */

    public void notifyMessageArrived(String content){

            //Define Notification Manager
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            //Define sound URI
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (!mIsInFront) {

            // set notification
            Intent notificationIntent = new Intent(this, ChatActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.bluetooth_logo)
                    .setContentTitle("Chat42")
                    .setContentText(content)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(soundUri)
                    .setContentIntent(intent);

            //Display notification
            notificationManager.notify(0, mBuilder.build());
        }
        else{
            NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                    .setSound(soundUri); //This sets the sound to play
            //Display notification
            notificationManager.notify(0, mBuilder.build());
        }
    }

    /**
     * Create a BroadcastReceiver for ACTION and STATES of the bluetooth
     * and register to the activity
     */
    private void createBroadcastReceiver(){

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                switch (action){
                    // When bluetooth state changed
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        switch(state) {
                            case BluetoothAdapter.STATE_OFF:
                                Toast.makeText(getApplicationContext(),"Bluetooth: STATE_OFF",
                                        Toast.LENGTH_LONG).show();
                                goToMainActivity();
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                Toast.makeText(getApplicationContext(),"Bluetooth: STATE_TURNING_OFF",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case BluetoothAdapter.STATE_ON:
                                Toast.makeText(getApplicationContext(),"Bluetooth: STATE_ON",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Toast.makeText(getApplicationContext(),"Bluetooth: STATE_TURNING_ON",
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;

                    // When bluetooth mode changed
                    case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                        int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                        switch(mode) {
                            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                                break;
                            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                                break;
                            case BluetoothAdapter.SCAN_MODE_NONE:
                                break;
                        }
                        break;

                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        Toast.makeText(getApplicationContext(),"CONNECTED",
                                Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        Toast.makeText(getApplicationContext(),"DISCONNECTED",
                                Toast.LENGTH_LONG).show();
                        goToMainActivity();
                        break;
                }
            }
        };
    }

    public void getPictureFromGallery(){

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
        /*
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),GALLERY);
        */
    }

    // get result - picture from gallery
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case GALLERY:
                if (data != null) {
                    try {
                        // get uri from result
                        Uri selectedImage = data.getData();

                        // decode it to picture
                        Bitmap tempPic = decodeUri(selectedImage);

                        // Put it in the mTempPicture;
                        mTempPicture = tempPic;

                        // send it to service as msg with picture
                        mCommandChat.setText(null);
                        if (mTempPicture != null){

                            // send message to other service
                            sendPictureToService(mTempPicture);

                            // set current time
                            Date date = Calendar.getInstance().getTime();

                            // create message;
                            mTempMessage = new ChatMessage(mDeviceAddress,mConnectedDeviceAddress,"Me:",mTempPicture,date) ;

                            // add message to data base
                            mDBManager.insertMessage(mTempMessage);

                            //  add message to listview
                            addMessageToConversation(mTempMessage);

                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * Decode the picture from uri and return bitmap picture with required  to prevent out of memory
     * problem
     */
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 200;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o2);
    }

    public class Delay extends AsyncTask<Void,Void,Void> {
        Context context;
        public Delay(Context context){
            this.context = context;
        }
        @Override
        protected Void doInBackground(Void[] objects) {
            try {
                publishProgress();
                Thread.sleep(1000);
            }catch (Exception ex){
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // update connected device with your profile picture
            if (mProfilePicture!= null)
                sendPictureToService(mProfilePicture);
            else{
                if (mGender == MALE)
                    sendPictureToService(BitmapFactory.decodeResource(getResources(),R.drawable.boy_pic));
                else
                    sendPictureToService(BitmapFactory.decodeResource(getResources(),R.drawable.girl_pic));
            }
            this.cancel(true);
        }
    }

    private void getDeviceNameAndGender() {
        String[] srtingArray = (mBluetoothService.getConnectedName()).split("_");
        int code = Integer.valueOf(srtingArray[1]);
        int chatType = code / 100;
        int gender = (code / 10) % 10;
        mConnectedGender = gender; // set connected gender
        mConnectedName = srtingArray[2];
    }

}
