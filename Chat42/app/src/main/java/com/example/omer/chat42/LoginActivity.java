package com.example.omer.chat42;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,Constants {

    // Views
    private EditText mEditTextUserName;
    private TextView mChatTypeView;
    private CheckBox mCheckBoxGenderMale;
    private CheckBox mCheckBoxGenderFemale;
    private CheckBox mCheckBoxMale;
    private CheckBox mCheckBoxFemale;
    private ImageButton mImageButtonPicture;
    private Button mImageButtonStart;
    private Bitmap mProfilePicture;


    // Member fields
    private String[] mArrayInterest;
    private String mUserName;
    private int mIsMale = MALE;
    private int mChatType = STANDART;
    private int mInterestIn = INFEMALE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        generalInit();
    }

    private void generalInit() {

        // Bind layout's view to class
        mEditTextUserName = (EditText)findViewById(R.id.editText_user_name);
        mChatTypeView = (TextView) findViewById(R.id.textView_list_interest);
        mCheckBoxGenderMale = (CheckBox) findViewById(R.id.checkBox_gender_male);
        mCheckBoxGenderFemale = (CheckBox) findViewById(R.id.checkBox_gender_female);
        mCheckBoxMale = (CheckBox) findViewById(R.id.checkBox_male);
        mCheckBoxFemale = (CheckBox) findViewById(R.id.checkBox_female);
        mImageButtonPicture = (ImageButton) findViewById(R.id.imageButton_profile_pic);
        mImageButtonStart = (Button) findViewById(R.id.imageButton_start);


        // Set on click listener
        mCheckBoxGenderMale.setOnClickListener(this);
        mCheckBoxGenderFemale.setOnClickListener(this);
        mCheckBoxMale.setOnClickListener(this);
        mCheckBoxFemale.setOnClickListener(this);
        mImageButtonPicture.setOnClickListener(this);
        mImageButtonStart.setOnClickListener(this);
        mChatTypeView.setOnClickListener(this);

        showInterestIn(false);

        // Set interest options
        mArrayInterest = new String[] {"Just for fun","Share a taxi","Flirt in a bar" };

        // Set radioOptions
        mCheckBoxGenderMale.setChecked(true);
        mCheckBoxGenderFemale.setChecked(false);
        mCheckBoxMale.setChecked(false);
        mCheckBoxFemale.setChecked(true);

    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }
    private void showInterestIn(boolean bool){
        if (bool){
            mCheckBoxMale.setVisibility(View.VISIBLE);
            mCheckBoxFemale.setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.textView_interest_in)).setVisibility(View.VISIBLE);
        }
        else{
            mCheckBoxMale.setVisibility(View.INVISIBLE);
            mCheckBoxFemale.setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.textView_interest_in)).setVisibility(View.INVISIBLE);
        }
    }

    private void saveSharedPreferences(){

        // use SharedPreferences pass all string and int data
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE, 0);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("NAME", mUserName);
        editor.putInt("CHAT_TYPE", mChatType);
        editor.putInt("GENDER", mIsMale);
        editor.putInt("GENDER_INTEREST", mInterestIn);

        editor.commit();

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

    private void chooseGender(CheckBox rMale,CheckBox rFemale) {

        if (rMale.isChecked()) {
            rFemale.setChecked(false);
            mIsMale = MALE;
        }
        else {
            rFemale.setChecked(true);
            mIsMale = FEMALE;
        }

        // update chosen
        if(mCheckBoxGenderMale.isChecked())
            mIsMale = MALE;
        else
            mIsMale = FEMALE;

        if(mCheckBoxMale.isChecked())
            mInterestIn = MALE;
        else
            mInterestIn = FEMALE;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case (R.id.checkBox_male):
                chooseGender(mCheckBoxMale, mCheckBoxFemale);
                break;
            case (R.id.checkBox_female):
                chooseGender(mCheckBoxFemale, mCheckBoxMale);
                break;
            case (R.id.checkBox_gender_female):
                chooseGender(mCheckBoxGenderFemale, mCheckBoxGenderMale);
                break;
            case (R.id.checkBox_gender_male):
                chooseGender(mCheckBoxGenderMale, mCheckBoxGenderFemale);
                break;
            case (R.id.imageButton_start):
                String tempName = mEditTextUserName.getText().toString();
                if (tempName.equals(""))
                    createGenralAlertDialog("Please Fill nickname");
                else {
                        mUserName = tempName;
                        saveSharedPreferences();
                        goToMainActivity();
                }
                break;

            case (R.id.textView_list_interest):

                AlertDialog.Builder builderType = new AlertDialog.Builder(this);
                builderType.setTitle("Select chat type");
                builderType.setItems(mArrayInterest, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                mChatTypeView.setText(mArrayInterest[0]);
                                mChatType = STANDART;
                                showInterestIn(false);
                                break;
                            case 1:
                                mChatTypeView.setText(mArrayInterest[1]);
                                mChatType = RIDE;
                                showInterestIn(false);
                                break;
                            case 2:
                                mChatTypeView.setText(mArrayInterest[2]);
                                mChatType = BAR;
                                showInterestIn(true);
                                break;
                        }
                    }
                });
                builderType.show();

                break;
            case (R.id.imageButton_profile_pic):

                String options[] = new String[] {"Gallery", "Camera"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose source");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                getPictureFromGallery();
                                break;
                            case 1:
                                takePicture();
                                break;
                        }
                    }
                });
                builder.show();
                break;
        }
    }

    public void getPictureFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),GALLERY);
    }

    private void takePicture() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    // go to mainActivity
    private void goToMainActivity() {
        Intent goToMainActivity = new Intent(this,MainActivity.class);
        // pass the image
        goToMainActivity.putExtra("IMAGE", mProfilePicture);
        startActivity(goToMainActivity);
        finish();
    }

    // get results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case CAMERA:
                if (data != null) {

                    mProfilePicture = (Bitmap) data.getExtras().get("data");

                    mImageButtonPicture.setImageBitmap(mProfilePicture);
                }
                break;

            case GALLERY:
                if (data != null) {
                    try {
                        // get uri from result
                        Uri selectedImage = data.getData();

                        // decode it to picture
                        mProfilePicture = decodeUri(selectedImage);

                        // bind with view
                        mImageButtonPicture.setImageBitmap(mProfilePicture);

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

        final int REQUIRED_SIZE = 100;

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

}
