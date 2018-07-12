package com.fabianbleile.fordigitalimmigrants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.fabianbleile.fordigitalimmigrants.data.Contact;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Time;
import java.util.List;

import static com.fabianbleile.fordigitalimmigrants.R.id.ctv_birthday_widget;
import static com.fabianbleile.fordigitalimmigrants.R.id.ctv_currentLocation_widget;
import static com.fabianbleile.fordigitalimmigrants.R.id.ctv_email_widget;
import static com.fabianbleile.fordigitalimmigrants.R.id.ctv_facebook_widget;
import static com.fabianbleile.fordigitalimmigrants.R.id.ctv_hometown_widget;
import static com.fabianbleile.fordigitalimmigrants.R.id.ctv_instagram_widget;
import static com.fabianbleile.fordigitalimmigrants.R.id.ctv_name_widget;
import static com.fabianbleile.fordigitalimmigrants.R.id.ctv_phone_number_widget;
import static com.fabianbleile.fordigitalimmigrants.R.id.ctv_snapchat_widget;
import static com.fabianbleile.fordigitalimmigrants.R.id.ctv_twitter_widget;

/**
 * The configuration screen for the {@link ShareContactWidget ShareContactWidget} AppWidget.
 */
public class ShareContactWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.fabianbleile.fordigitalimmigrants.ShareContactWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    Switch name, phonenumber, email, birthday, hometown, instagram, facebook, snapchat, twitter, location;
    List<Switch> switches;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ShareContactWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            saveTitlePref(context, mAppWidgetId, widgetText);

            //When the button is clicked create a file and save it. return the uri
            Contact contact = getShareContact();
            File requestFile = createTxtFile(contact);
            Uri fileUri = Uri.fromFile(requestFile);
            String fileUriString = fileUri.toString();
            MainActivity.setDefaults("fileUriString", fileUriString, getBaseContext());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ShareContactWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public ShareContactWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.share_contact_widget_configure);
        mAppWidgetText = findViewById(R.id.appwidget_text);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        name = findViewById(ctv_name_widget); switches.add(name);
        phonenumber = findViewById(ctv_phone_number_widget); switches.add(phonenumber);
        email = findViewById(ctv_email_widget); switches.add(email);
        birthday = findViewById(ctv_birthday_widget); switches.add(birthday);
        hometown = findViewById(ctv_hometown_widget); switches.add(hometown);
        instagram = findViewById(ctv_instagram_widget); switches.add(instagram);
        facebook = findViewById(ctv_facebook_widget); switches.add(facebook);
        snapchat = findViewById(ctv_snapchat_widget); switches.add(snapchat);
        twitter = findViewById(ctv_twitter_widget); switches.add(twitter);
        location = findViewById(ctv_currentLocation_widget); switches.add(location);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mAppWidgetText.setText(loadTitlePref(ShareContactWidgetConfigureActivity.this, mAppWidgetId));
    }

    private Contact getShareContact(){
        Contact contact;
        String sname="";String sphonenumber="";String semail="";String sbirthday="";
        String shometown="";String sinstagram="";String sfacebook="";String ssnapchat="";
        String stwitter="";String slocation="";

        if(name.isChecked()){
            String key = name.getText().toString();
            sname = MainActivity.getDefaults(key, getApplicationContext());
        } else if (phonenumber.isChecked()){
            String key = phonenumber.getText().toString();
            sphonenumber = MainActivity.getDefaults(key, getApplicationContext());
        } else if (email.isChecked()){
            String key = email.getText().toString();
            semail = MainActivity.getDefaults(key, getApplicationContext());
        } else if (birthday.isChecked()){
            String key = birthday.getText().toString();
            sbirthday = MainActivity.getDefaults(key, getApplicationContext());
        } else if (hometown.isChecked()){
            String key = hometown.getText().toString();
            shometown = MainActivity.getDefaults(key, getApplicationContext());
        } else if (instagram.isChecked()){
            String key = instagram.getText().toString();
            sinstagram = MainActivity.getDefaults(key, getApplicationContext());
        } else if (facebook.isChecked()){
            String key = facebook.getText().toString();
            sfacebook = MainActivity.getDefaults(key, getApplicationContext());
        } else if (snapchat.isChecked()){
            String key = snapchat.getText().toString();
            ssnapchat = MainActivity.getDefaults(key, getApplicationContext());
        } else if (twitter.isChecked()){
            String key = twitter.getText().toString();
            stwitter = MainActivity.getDefaults(key, getApplicationContext());
        } else if (location.isChecked()){
            String key = location.getText().toString();
            slocation = MainActivity.getDefaults(key, getApplicationContext());
        }

        contact = new Contact(sname, sphonenumber, semail, sbirthday, shometown, sinstagram, sfacebook, ssnapchat, stwitter, slocation);

        return contact;
    }

    //create json file to send
    public File createTxtFile(Contact contact) {
        File pathToExternalStorage = getBaseContext().getExternalFilesDir(null);
        //to this path add a new directory path and create new App dir (InstroList) in /documents Dir
        File appDirectory = new File(pathToExternalStorage.getAbsolutePath()  + "/sharedPersonalData");
        // have the object build the directory structure, if needed.
        appDirectory.mkdirs();
        // create new file with path and name
        File nfcDataFile = new File (appDirectory, makeNewFileName());
        //make gson from contactObject
        Gson gson = new Gson();
        String jsonString = gson.toJson(contact);

        try {
            Writer output = new BufferedWriter(new FileWriter(nfcDataFile));
            output.write(jsonString);
            output.close();
            Toast.makeText(getBaseContext(), "Composition saved!", Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), "" + nfcDataFile, Toast.LENGTH_LONG).show();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(MainActivity.mTagHandmade, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }

        nfcDataFile.setReadable(true, false);

        return nfcDataFile;
    }

    private String makeNewFileName() {
        // creates a new file name
        String fileName;
        Time time = new Time(System.currentTimeMillis());
        fileName = time.toString();
        //Replace (:) with (_)
        fileName = fileName.replaceAll(":", "_");
        fileName = "FastContactShare" + fileName + ".txt";

        return fileName;
    }
}