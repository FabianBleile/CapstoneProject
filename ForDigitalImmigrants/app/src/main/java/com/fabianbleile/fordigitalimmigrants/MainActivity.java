package com.fabianbleile.fordigitalimmigrants;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fabianbleile.fordigitalimmigrants.Fragment.ReceiveScreenFragment;
import com.fabianbleile.fordigitalimmigrants.Fragment.SendScreenFragment;
import com.fabianbleile.fordigitalimmigrants.Fragment.SettingsScreenFragment;
import com.fabianbleile.fordigitalimmigrants.data.Contact;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
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

public class MainActivity extends FragmentActivity implements SendScreenFragment.OnReadyButtonClickedInterface, NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    //general
    public static final String mTagHandmade = "HANDMADETAG";
    public Context mContext;

    //content related
    public static ArrayList<Integer> mIcons = new ArrayList<>();
    private String message;

    //final variables
    private static final int REQUEST_ACCESS_CORASE_LOCATION = 1;
    private static final String TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String MY_ADMOB_APP_ID = "ca-app-pub-6856957073988410~7948042904";
    private static final String MY_AD_UNIT_ID = "ca-app-pub-6856957073988410/9779999321";

    //viewPager with fragments
    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private CoordinatorLayout coordinatorLayout;

    // NFC
    NfcAdapter mNfcAdapter;
    public static String mSendNdefMessage;
    public static String mReceiveNdefMessage;

    //Firebase
    private InterstitialAd mInterstitialAd;

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
        Log.d("TAG", "send message is set");

        /*
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Log.d("TAG", "The interstitial is loaded.");
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
         */
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { ndefRecord });
        return msg;
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

            return new NdefRecord(NdefRecord.TNF_MIME_MEDIA, ("application/vnd.com.fabianbleile.fordigitalimmigrants").getBytes(), new byte[0], payload.toByteArray());
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        /*
        MobileAds.initialize(this, MY_ADMOB_APP_ID);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(MY_AD_UNIT_ID);
         */

        navigation = this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        mPager = findViewById(R.id.viewPager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(viewPageOnPageListener);
        mPager.setCurrentItem(1);

        if(mIcons.size() == 0){
            mIcons.add(R.string.ctv_name);
            mIcons.add(R.string.ctv_phone_number);
            mIcons.add(R.string.ctv_email);
            mIcons.add(R.string.ctv_birthday);
            mIcons.add(R.string.ctv_hometown);
            mIcons.add(R.string.ctv_instagram);
            mIcons.add(R.string.ctv_facebook);
            mIcons.add(R.string.ctv_snapchat);
            mIcons.add(R.string.ctv_twitter);
            mIcons.add(R.string.ctv_currentLocation);
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
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("TAG", "intent received" + getIntent().getAction()+ getIntent().getDataString());
        // Check to see that the Activity started due to an Android Beam

        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            Log.d("TAG", "nfc intent received");
            processNfcIntent(getIntent());
        }
        /*
        else if (Intent.ACTION_SEND.equals(getIntent().getAction())){
            processWidgetIntent();
        }
         */
    }

    private void processWidgetIntent() {
        mSendNdefMessage = getDefaults("defaultContactString", this);
        Toast.makeText(mContext, R.string.action_pressSend, Toast.LENGTH_SHORT).show();
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processNfcIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
         message = new String(msg.getRecords()[0].getPayload());
        mReceiveNdefMessage = message;

        // parse String to contact object
        Contact contact = getContactFromJsonString(message);

        // add contact to room database
        ReceiveScreenFragment.onFileIncome(contact);
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

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
        } } else {FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.e("location", location.toString());
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

                                setDefaults("Location", Location, getApplicationContext());
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

    //-------------------------------------------------------------------------------------------------------------------
    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        showSnackbarForDefaultFile();
    }

    private void showSnackbarForDefaultFile() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, getResources().getText(R.string.question_setDefaultFile), Snackbar.LENGTH_LONG);
        snackbar.setAction(getResources().getText(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // yes is selected, store the file as default
                setDefaults("defaultContactString", mSendNdefMessage, getApplicationContext());
            }
        });
        snackbar.setActionTextColor(Color.MAGENTA);
        snackbar.show();
    }

    //-------------------------------------------------------------------------------------------------------------------
    //This part handles all the navigation through the menu and the fragments
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_settings:
                    mPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_send:
                    mPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_receive:
                    mPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    private ViewPager.OnPageChangeListener viewPageOnPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            int itemId = -1;
            switch (i) {
                case 0:
                    itemId = R.id.navigation_settings;
                    break;
                case 1:
                    itemId = R.id.navigation_send;
                    break;
                case 2:
                    itemId = R.id.navigation_receive;
                    break;
            }

            navigation.setSelectedItemId(itemId);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

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

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SettingsScreenFragment();
                case 1:
                    return new SendScreenFragment();
                case 2:
                    return new ReceiveScreenFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
    //-------------------------------------------------------------------------------------------------------------------
}
