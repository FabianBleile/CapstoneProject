package com.fabianbleile.fordigitalimmigrants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.fabianbleile.fordigitalimmigrants.data.Contact;
import com.google.gson.Gson;

import java.util.ArrayList;

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

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    ArrayList<Switch> switches = new ArrayList<>();
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ShareContactWidgetConfigureActivity.this;

            //When the button is clicked create a file and save it. return the uri
            Contact contact = getShareContact();
            Gson gson = new Gson();
            String contactString = gson.toJson(contact);

            MainActivity.setDefaults("defaultContactString", contactString, getBaseContext());

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

    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.share_contact_widget_configure);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        Switch name = findViewById(ctv_name_widget); switches.add(name);
        Switch phonenumber = findViewById(ctv_phone_number_widget); switches.add(phonenumber);
        Switch email = findViewById(ctv_email_widget); switches.add(email);
        Switch birthday = findViewById(ctv_birthday_widget); switches.add(birthday);
        Switch hometown = findViewById(ctv_hometown_widget); switches.add(hometown);
        Switch instagram = findViewById(ctv_instagram_widget); switches.add(instagram);
        Switch facebook = findViewById(ctv_facebook_widget); switches.add(facebook);
        Switch snapchat = findViewById(ctv_snapchat_widget); switches.add(snapchat);
        Switch twitter = findViewById(ctv_twitter_widget); switches.add(twitter);
        Switch location = findViewById(ctv_currentLocation_widget); switches.add(location);

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
    }

    private Contact getShareContact(){
        Contact contact;
        String sname="";String sphonenumber="";String semail="";String sbirthday="";
        String shometown="";String sinstagram="";String sfacebook="";String ssnapchat="";
        String stwitter="";String slocation="";

        if(switches.get(0).isChecked()){
            String key = switches.get(0).getText().toString();
            sname = MainActivity.getDefaults(key, getApplicationContext());
        } else if (switches.get(1).isChecked()){
            String key = switches.get(1).getText().toString();
            sphonenumber = MainActivity.getDefaults(key, getApplicationContext());
        } else if (switches.get(2).isChecked()){
            String key = switches.get(2).getText().toString();
            semail = MainActivity.getDefaults(key, getApplicationContext());
        } else if (switches.get(3).isChecked()){
            String key = switches.get(3).getText().toString();
            sbirthday = MainActivity.getDefaults(key, getApplicationContext());
        } else if (switches.get(4).isChecked()){
            String key = switches.get(4).getText().toString();
            shometown = MainActivity.getDefaults(key, getApplicationContext());
        } else if (switches.get(5).isChecked()){
            String key = switches.get(5).getText().toString();
            sinstagram = MainActivity.getDefaults(key, getApplicationContext());
        } else if (switches.get(6).isChecked()){
            String key = switches.get(6).getText().toString();
            sfacebook = MainActivity.getDefaults(key, getApplicationContext());
        } else if (switches.get(7).isChecked()){
            String key = switches.get(7).getText().toString();
            ssnapchat = MainActivity.getDefaults(key, getApplicationContext());
        } else if (switches.get(8).isChecked()){
            String key = switches.get(8).getText().toString();
            stwitter = MainActivity.getDefaults(key, getApplicationContext());
        } else if (switches.get(9).isChecked()){
            String key = switches.get(9).getText().toString();
            slocation = MainActivity.getDefaults(key, getApplicationContext());
        }

        contact = new Contact(sname, sphonenumber, semail, sbirthday, shometown, sinstagram, sfacebook, ssnapchat, stwitter, slocation);

        return contact;
    }
}