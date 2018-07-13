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

import com.fabianbleile.fordigitalimmigrants.Fragment.ReceiveScreenFragment;
import com.fabianbleile.fordigitalimmigrants.Fragment.SendScreenFragment;
import com.fabianbleile.fordigitalimmigrants.Fragment.SettingsScreenFragment;
import com.fabianbleile.fordigitalimmigrants.data.Contact;
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

public class MainActivity extends FragmentActivity implements SendScreenFragment.OnReadyButtonClickedInterface, NfcAdapter.CreateNdefMessageCallback {

    public static final String mTagHandmade = "HANDMADETAG";
    public Context mContext;
    public static ArrayList<Integer> mIcons = new ArrayList<>();

    //Google Service
    private FusedLocationProviderClient mFusedLocationClient;

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

        navigation = this.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        mPager = findViewById(R.id.viewPager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(viewPageOnPageListener);
        mPager.setCurrentItem(1);

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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();

        if (isExternalStorageReadable() && isExternalStorageWritable()) {
            // Android Beam file transfer is available, continue
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

            if (mNfcAdapter != null) {
                if (mNfcAdapter.isEnabled()) {
                    mNfcAdapter.setNdefPushMessageCallback(this, this);
                    // NdefMessage Test
                } else {
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                    String place = addresses.get(0).getFeatureName();
                                    String city = addresses.get(0).getLocality();
                                    String country = addresses.get(0).getCountryName();
                                    Location = place + ", " + city + ", " + country;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                setDefaults("Location", Location, getApplicationContext());
                            }
                        }
                    });
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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

    private void showSnackbarForDefaultFile() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, getResources().getText(R.string.question_setDefaultFile), Snackbar.LENGTH_LONG);
        snackbar.setAction(getResources().getText(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // yes is selected, store the file as default
                setDefaults("defaultFilePath", "-----------------------------------------------------------------", getApplicationContext());
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
