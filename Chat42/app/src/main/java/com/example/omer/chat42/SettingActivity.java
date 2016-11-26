package com.example.omer.chat42;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,Constants {


    private String activity;
    private Toolbar mMyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        activity =  getIntent().getStringExtra("ACTIVITY");

        generalInit();
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
        menu.findItem(R.id.action_profile).setVisible(false);
        menu.findItem(R.id.action_mode_logo).setVisible(false);
        menu.findItem(R.id.action_chat_history).setVisible(false);
        menu.findItem(R.id.action_exit).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    private void generalInit() {
        // setup toolbar
        Toolbar mMyToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mMyToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Setting");
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * Listener to the menu
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // go back;
            case android.R.id.home:
                goToActivity();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
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


    private void goToActivity() {

        finish();

        if (activity.equals("main")) {
            Intent goToActivity = new Intent(this, MainActivity.class);
            startActivity(goToActivity);
        }
        else{
            Intent goToActivity = new Intent(this, ChatActivity.class);
            startActivity(goToActivity);
        }

    }

    @Override
    public void onClick(View v) {

    }
}
