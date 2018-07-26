package com.fabianbleile.fordigitalimmigrants;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fabianbleile.fordigitalimmigrants.Fragment.ReceiveScreenFragment;
import com.fabianbleile.fordigitalimmigrants.Fragment.SendScreenFragment;
import com.fabianbleile.fordigitalimmigrants.Fragment.SettingsScreenFragment;
import com.fabianbleile.fordigitalimmigrants.data.Contact;
import com.fabianbleile.fordigitalimmigrants.data.ContactListViewModel;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements
        SendScreenFragment.OnReadyButtonClickedInterface,
        NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback,
        ContactListViewModel.AsyncResponse{

    //general
    public static final String mTagHandmade = "HANDMADETAG";
    public static Context mContext;
    //content related
    public static ArrayList<Integer> mIcons = new ArrayList<>();
    //final variables
    private static final int REQUEST_ACCESS_CORASE_LOCATION = 1;
    //viewPager with fragments
    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private CoordinatorLayout coordinatorLayout;
    // NFC
    NfcAdapter mNfcAdapter;
    public static String mSendNdefMessage;
    public static String mReceiveNdefMessage;

    public MainActivity() {
    }

    @Override
    public void OnReadyButtonClicked(String NdefMessage) {
        mSendNdefMessage = NdefMessage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNfcAdapter.invokeBeam(this);
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        NdefRecord ndefRecord = createTextRecord(mSendNdefMessage);

        return new NdefMessage(
                new NdefRecord[] { ndefRecord });
    }
    private NdefRecord createTextRecord (String message)
    {
        try
        {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = message.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;

            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_MIME_MEDIA, (getResources().getString(R.string.mimetype)).getBytes(), new byte[0], payload.toByteArray());
        }
        catch (UnsupportedEncodingException e)
        {
            //Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        MobileAds.initialize(this, getResources().getString(R.string.TEST_ADMOB_APP_ID));

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        mPager = findViewById(R.id.viewPager);
        PagerAdapter mPagerAdapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);

        TabLayout tabLayout = this.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        if(mIcons.size() == 0){ mIcons.add(R.string.ctv_name);mIcons.add(R.string.ctv_phone_number);mIcons.add(R.string.ctv_email);
            mIcons.add(R.string.ctv_birthday);mIcons.add(R.string.ctv_hometown);mIcons.add(R.string.ctv_instagram);mIcons.add(R.string.ctv_facebook);
            mIcons.add(R.string.ctv_snapchat);mIcons.add(R.string.ctv_twitter);mIcons.add(R.string.ctv_currentLocation);
        }

        getLastKnownLocation();
        if (isExternalStorageReadable() && isExternalStorageWritable()) {
            // Android Beam file transfer is available, continue
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

            if (mNfcAdapter != null) {
                if (mNfcAdapter.isEnabled()) {
                    mNfcAdapter.setNdefPushMessageCallback(this, this);
                    mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
                } else {
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                }
            }
        }

        int INT = 7;
        Long output = (long) INT;
        //sendInsertedContactId(output);
        updateLastContactWidget();
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check to see that the Activity started due to an Android Beam

        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            processNfcIntent(getIntent());
        }else if (Intent.ACTION_SEND.equals(getIntent().getAction())){
            processWidgetIntent();
        }
    }

    private void processWidgetIntent() {
        mSendNdefMessage = getDefaults(getResources().getString(R.string.defaultContactString), this);
        Toast.makeText(mContext, R.string.action_pressSend, Toast.LENGTH_SHORT).show();
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processNfcIntent(Intent intent) {
        mPager.setCurrentItem(2);

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String message = new String(msg.getRecords()[0].getPayload());
        mReceiveNdefMessage = message;

        // parse String to contact object
        Contact contact = getContactFromJsonString(message);

        // add contact to room database
        ReceiveScreenFragment.viewModel.addItem(contact);
        // when Asnc Task is done onProcessFinish is called
    }

    @Override
    public void onProcessFinish(Long output) {
        sendInsertedContactId(output);
        updateLastContactWidget();
    }

    private void sendInsertedContactId(Long output) {
        Intent intent = new Intent(this, LastContactWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), LastContactWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        int outputInt = output.intValue();
        intent.putExtra(LastContactWidget.LAST_CONTACT_ID, outputInt);
        sendBroadcast(intent);
    }

    private void updateLastContactWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(mContext, LastContactWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView);
        Log.e(mTagHandmade, "appWidgetManager updated");
    }

    private Contact getContactFromJsonString(String stringIntentData) {
        String jsonStringIntentData = stringIntentData.substring(3);
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(jsonStringIntentData).getAsJsonObject();
        Gson gson = new Gson();
        Contact contact = gson.fromJson(obj.toString() , Contact.class);
        Log.e(mTagHandmade, " " + jsonStringIntentData + ", " + obj.toString() + ", " + contact.toString());
        return contact;

    }


    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_CORASE_LOCATION);

        } } else {FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Geocoder geocoder;
                                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                String Location = "";

                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    String city = addresses.get(0).getLocality();
                                    String country = addresses.get(0).getCountryName();
                                    Location = city + ", " + country;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                setDefaults(getResources().getString(R.string.Location), Location, getApplicationContext());
                            } else {
                                //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_CORASE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLastKnownLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    //-------------------------------------------------------------------------------------------------------------------
    //This part handles all the storage in SharedPreferences
    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
    //-------------------------------------------------------------------------------------------------------------------
    //Method to check whether external media available and writable.
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //-------------------------------------------------------------------------------------------------------------------
    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        showSnackbarForDefaultFile();
        //testing ads.
        if(isNetworkAvailable()){
            Intent intent = new Intent(this, AdMobActivity.class);
            startActivity(intent);
        }
    }

    private void showSnackbarForDefaultFile() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, getResources().getText(R.string.question_setDefaultFile), Snackbar.LENGTH_LONG);
        snackbar.setAction(getResources().getText(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // yes is selected, store the file as default
                setDefaults(getResources().getString(R.string.defaultContactString), mSendNdefMessage, getApplicationContext());
            }
        });
        snackbar.setActionTextColor(Color.MAGENTA);
        snackbar.show();
    }

    //-------------------------------------------------------------------------------------------------------------------
    //This part handles all the navigation through the menu and the fragments

    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        // This determines the fragment for each tab
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new SettingsScreenFragment();
            } else if (position == 1){
                return new SendScreenFragment();
            } else {
                return new ReceiveScreenFragment();
            }
        }

        // This determines the number of tabs
        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        // This determines the title for each tab
        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            switch (position) {
                case 0:
                    return mContext.getString(R.string.title_settings);
                case 1:
                    return mContext.getString(R.string.title_send);
                case 2:
                    return mContext.getString(R.string.title_receive);
                default:
                    return null;
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------
}
