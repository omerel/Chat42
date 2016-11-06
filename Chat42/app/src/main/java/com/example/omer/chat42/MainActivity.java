package com.example.omer.chat42;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * This activity does all the work of setting up and managing bluetooth and wifi connection between
 * two devices
 **/
public class MainActivity extends AppCompatActivity {

    // Define state types
    private static final int WIFI_MODE = 0;
    private static final int BLUETOOTH_MODE = 1;
    private static int deviceMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Check the device state - wifi or bluetooth
        deviceMode = checkState();
        // initilaize the app with with device mode
        if (deviceMode == BLUETOOTH_MODE)
            initBluetoothMode();
        else
            initWifiMode();


    }

    /**
     * TODO Barr
     */
    private void initWifiMode() {
    }

    /**
     * init  app in bluetooth mode
     */
    private void initBluetoothMode() {

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


    /** Make the action button appear in old devices **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /** Listener to the menu **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Go to setting
            case R.id.action_settings:
                Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
                return true;

            // Log out from the current user
            case R.id.action_chat_history:
                Toast.makeText(this, "Chat history", Toast.LENGTH_SHORT).show();
                return true;

            // Log out from the current user
            case R.id.action_logout:
                Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
