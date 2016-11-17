package com.example.omer.chat42;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import com.example.omer.chat42.BluetoothService.MyBinder;
import java.util.ArrayList;

/**
 * This activity does all the work of setting up UI and connecting to other user
 **/
public class MainActivity extends AppCompatActivity implements View.OnClickListener,Constants {

    private  View mDistanceLayout;
    private  View mDiscoverableLayout;
    private static Switch mDiscoverable;
    private  ListView mDevicesList;

    // Member fields
    private ArrayList<BluetoothDevice> mArrayDevices;
    private ArrayAdapter<String> mArrayAdapter;
    private static BroadcastReceiver mReceiver;
    private static int mDeviceMode;
    private String mUserName = "user";
    private static IntentFilter mIntentFilter;


    private static BluetoothService mBluetoothService;
    private static boolean  mIsServiceBound = false;
    protected final Messenger mMessenger = new Messenger(new IncomingHandler());
    private static Messenger mServiceMessenger;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start bluetooth service and bind it to this activity
        Intent intent = new Intent(this, BluetoothService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        // General init
        generalInit();

        // Check the device state - wifi or bluetooth
        mDeviceMode = checkState();

        // initilaize the app with with device mode
        if (mDeviceMode == BLUETOOTH_MODE)
            changeUIToBluetoothMode();
        else
            changeUIToWifiMode();
    }

    /**
     *  Create bounding between service class to this activity
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
               mIsServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder myBinder = (MyBinder) service;
            mBluetoothService = myBinder.getService();
            mServiceMessenger = myBinder.getMessenger();

            mIsServiceBound = true;

            // enable bluetooth
            enableBluetooth();

            // Bluetooth init
            bluetoothInit();

            // register Messenger in the service
            registerToServiceMessenger();


        }

    };

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // start bluetooth service and bind it to this activity
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mIsServiceBound = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsServiceBound) {
            mIsServiceBound = false;
            unbindService(mServiceConnection);
        }
    }


    /** on click listener */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.button_search):
                startDiscoveringDevices();
                break;
            case (R.id.switch_discoverable):
                beDiscoverable();
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mIsServiceBound) {
           mIsServiceBound = false;
            unregisterReceiver(mReceiver);
        }
    }

    /** Listener to the menu **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Go to setting
            case R.id.action_settings:
                //check service
                if (mIsServiceBound)
                    //TODO
                    Toast.makeText(this,"Replace me!", Toast.LENGTH_SHORT).show();
                return true;

            // Log out from the current user
            case R.id.action_chat_history:
                //TODO
                Toast.makeText(this,"Replace me!", Toast.LENGTH_SHORT).show();
                return true;

            // Log out from the current user
            case R.id.action_logout:
                //TODO
                Toast.makeText(this,"Replace me!", Toast.LENGTH_SHORT).show();
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

    /** Make the action button appear in old devices **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * General initilaize of the activity
     */
    private void generalInit() {

        // setup toolbar
        Toolbar mMyToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mMyToolbar);

        getSupportActionBar().setTitle("Hello "+mUserName);

        // Bind layout's view to class
        mDistanceLayout = (View)findViewById(R.id.layout_distance);
        mDiscoverableLayout = (View)findViewById(R.id.layout_discoverable);
        Button mSearchButton = (Button) findViewById(R.id.button_search);
        mDiscoverable = (Switch)findViewById(R.id.switch_discoverable);
        mDevicesList = (ListView)findViewById(R.id.listview_available_devices);

        // bind device list
        mArrayDevices = new ArrayList<>();
        mArrayAdapter = new ArrayAdapter<>(this,R.layout.item_device);
        mDevicesList.setAdapter(mArrayAdapter);

        // Set on click listener
        mSearchButton.setOnClickListener(this);
        mDiscoverable.setOnClickListener(this);

        mDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this,"Connecting\n"+
                        mArrayDevices.get(position).getName(), Toast.LENGTH_SHORT).show();
                if(mIsServiceBound)
                    mBluetoothService.connectToDevice(mArrayDevices.get(position));
            }
        });
    }

    /**
     * Initilaize the bluetooth connection
     */
    private void bluetoothInit() {

        //TODO check if the device is already connected to somebody else- in other app

        //TODO add status line in the bottom screen

        // Create a BroadcastReceiver
        createBroadcastReceiver();

        // register to Receiver
        mIntentFilter = new IntentFilter();
        // Add all all the actions to filter
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
         registerReceiver(mReceiver,mIntentFilter);

        // Set name for user
        if (mIsServiceBound){
            mBluetoothService.setName(mUserName);
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
                    // When discovery finds a device
                    case BluetoothDevice.ACTION_FOUND:
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        // Add the name and address to an array adapter to show in a ListView
                        mArrayAdapter.add(device.getName()+" ("+device.getAddress()+")");
                        mArrayDevices.add(device);
                        mArrayAdapter.notifyDataSetChanged();
                        break;

                    // When bluetooth state changed
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        switch(state) {
                            case BluetoothAdapter.STATE_OFF:
                                Toast.makeText(getApplicationContext(),"Bluetooth: STATE_OFF",
                                        Toast.LENGTH_LONG).show();
                                mDiscoverable.setChecked(false);

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
                        break;
                }
            }
        };
    }

    /**
     * Enable bluetooth if not enable
     */
    private void enableBluetooth() {

        if(mIsServiceBound){
            if (!mBluetoothService.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    /**
     * Start discover other devices and update the device list
     */
    private void startDiscoveringDevices(){
        if(mIsServiceBound) {
            if (mBluetoothService.isDiscovering()) {
                // the button is pressed when it discovers, so cancel the discovery
                if (mIsServiceBound)
                    sendRequestToService(CANCEL_DISCOVERY);
                Toast.makeText(getApplicationContext(), "Stop discovering",
                        Toast.LENGTH_LONG).show();
            } else {
                mArrayAdapter.clear();  // clear adapter
                mArrayDevices.clear();  // clear bluetooth array devices
                if (mIsServiceBound)
                    sendRequestToService(START_DISCOVERY);
                Toast.makeText(getApplicationContext(), "Start discovering",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public static boolean isDiscoverableSwitchOn(){
        return mDiscoverable.isChecked();
    }


    /**
     * Change UI to WIFI mode
     */
    private void changeUIToWifiMode() {
        //TODO Change icon mode to wifi
        // Disable beDiscoverable function
        mDiscoverableLayout.setVisibility(View.GONE);
    }

    /**
     * init app in bluetooth mode
     */
    private void changeUIToBluetoothMode() {
        // TODO Change icon mode to bluetooth
        // Disable distance function
        mDistanceLayout.setVisibility(View.GONE);
    }

    /**
     *  The method checks if there is available internet in the device.
     *  if there is available internet bluetooth will set off
     *  otherwise the app will be on bluetooth mode.
     */
    private int checkState() {
        //TODO check if there is available internet
        return BLUETOOTH_MODE;
    }

    /**
     *  Make the device be
     */
    private void beDiscoverable(){
        if (mIsServiceBound) {
            if (!mDiscoverable.isChecked()) {
                sendRequestToService(STOP_DISCOVERABLE);

                // TODO - delete it when using API above 19
                Intent discoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
                startActivity(discoverableIntent);

            } else {

                if (mBluetoothService.isEnabled())
                    enableBluetooth();
                Intent discoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
                sendRequestToService(START_DISCOVERABLE);
            }
        }
    }
    /**
     * Turnoff Discoverable Button
     */
    public void turnOffDiscoverableButton() {
        if (mIsServiceBound) {
            sendRequestToService(STOP_DISCOVERABLE);
                mDiscoverable.setChecked(false);
        }
    }

    private void goToChatActivity() {
        unregisterReceiver(mReceiver);
        finish();
        unbindService(mServiceConnection);
        mIsServiceBound = false;
        Intent goToChatActivity = new Intent(this,ChatActivity.class);
        goToChatActivity.putExtra("USER",mUserName);
        startActivity(goToChatActivity);
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
                case CONNECTING_SUCCEEDS:

                    // close discover devices
                    sendRequestToService(CANCEL_DISCOVERY);
                    // close discoverable
                    if (isDiscoverableSwitchOn()){
                        turnOffDiscoverableButton();
                    }
                    // close the server side socket
                    mBluetoothService.cancelThreadIfAlive();
                    // go to chat activity
                    goToChatActivity();
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
}
