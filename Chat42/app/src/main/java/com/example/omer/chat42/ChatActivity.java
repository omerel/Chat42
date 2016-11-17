package com.example.omer.chat42;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity  implements View.OnClickListener,Constants {

    // Layout's views
    private Toolbar mMyToolbar;

    // fileds
    private static BroadcastReceiver mReceiver;
    private static IntentFilter mIntentFilter;
    boolean mServiceBound = false;
    static BluetoothService mBluetoothService;

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

        // General init
        generalInit();
    }

    private void generalInit() {
        // setup toolbar
        mMyToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mMyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_bluetooth_black_24dp);
        getSupportActionBar().setTitle("Chat with");


        // Create a BroadcastReceiver
        createBroadcastReceiver();

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
    }



    /**
     * Make the action button appear in old devices
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Listener to the menu
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Go to setting
            case R.id.action_settings:
                //check service
                //  if (mIsServiceBound)
                //TODO
                Toast.makeText(this, "Replace me!", Toast.LENGTH_SHORT).show();
                return true;

            // Log out from the current user
            case R.id.action_chat_history:
                //TODO
                Toast.makeText(this, "Replace me!", Toast.LENGTH_SHORT).show();
                return true;

            // Log out from the current user
            case R.id.action_logout:
                //TODO
                Toast.makeText(this, "Replace me!", Toast.LENGTH_SHORT).show();
                return true;

            // go back;
            case android.R.id.home:
                goToMainActivity();
                return true;

            // Exit app
            case R.id.action_exit:
                finish();
                System.exit(0);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onClick(View v) {

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
        }
    };

    private void goToMainActivity() {

        sendRequestToService(CLOSE_SOCKET);

        unregisterReceiver(mReceiver);

        unbindService(mServiceConnection);
        mServiceBound = false;

        finish();

        Intent goToMainActivity = new Intent(this,MainActivity.class);
        startActivity(goToMainActivity);
    }

    /**
     * Handler of incoming messages from service
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTING_FAILURE:
                    Toast.makeText(getApplicationContext(),"Connecting failed",
                            Toast.LENGTH_LONG).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
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

}
