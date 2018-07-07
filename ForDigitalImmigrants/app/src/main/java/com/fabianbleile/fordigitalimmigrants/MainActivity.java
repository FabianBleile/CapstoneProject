package com.fabianbleile.fordigitalimmigrants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fabianbleile.fordigitalimmigrants.SendScreenFragment;
import com.fabianbleile.fordigitalimmigrants.SettingsScreenFragment;
import com.fabianbleile.fordigitalimmigrants.R;
import com.fabianbleile.fordigitalimmigrants.dummy.DummyContent;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements ReceiveScreenFragment.OnListFragmentInteractionListener, SendScreenFragment.OnReadyButtonClickedInterface{

    public static final String mTagHandmade = "HANDMADETAG";
    public static ArrayList<Integer> mIcons = new ArrayList<Integer>();

    NfcAdapter mNfcAdapter;
    // Flag to indicate that Android Beam is available
    boolean mAndroidBeamAvailable = false;
    public static Uri[] mFileUris = new Uri[2];
    private FileUriCallback mFileUriCallback;
    private ReaderModeCallback mReaderModeCallback;

    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

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
            switch (i){
                case 0 :
                    itemId = R.id.navigation_settings;
                    break;
                case 1 :
                    itemId = R.id.navigation_send;
                    break;
                case 2 :
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

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void OnReadyButtonClicked(Uri[] fileUris) {
        mFileUris = fileUris;
        mNfcAdapter.disableReaderMode(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNfcAdapter.invokeBeam(this);
        }
    }

    /**
          * A simple pager adapter that represents 3 ScreenSlidePageFragment objects, in
          * sequence.
          */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    return new SettingsScreenFragment();
                case 1 :
                    return new SendScreenFragment();
                case 2 :
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(viewPageOnPageListener);

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

        if(isExternalStorageReadable() && isExternalStorageWritable()) {
            // Android Beam file transfer is available, continue
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

            if(mNfcAdapter != null){
                mNfcAdapter.enableReaderMode(this, mReaderModeCallback, NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
                mFileUriCallback = new FileUriCallback();
                // Set the dynamic callback for URI requests.
                mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback,this);
            }
        }
    }

    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {

        } /**
                  * Create content URIs as needed to share with another device
                  */
        @Override
        public Uri[] createBeamUris(NfcEvent nfcEvent) {
            return mFileUris;
        }
    }

    private class ReaderModeCallback implements NfcAdapter.ReaderCallback {
        public ReaderModeCallback() {
        }

        @Override
        public void onTagDiscovered(Tag tag) {
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

    // A File object containing the path to the transferred files
    private String mParentPath;
    // Incoming Intent
    private Intent mIntent;

    /*
     * Called from onNewIntent() for a SINGLE_TOP Activity
     * or onCreate() for a new Activity. For onNewIntent(),
     * remember to call setIntent() to store the most
     * current Intent
     *
     */
    private void handleViewIntent() {
        mPager.setCurrentItem(2);

        // Get the Intent action
        mIntent = getIntent();
        String action = mIntent.getAction();
        /*
         * For ACTION_VIEW, the Activity is being asked to display data.
         * Get the URI.
         */
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            // Get the URI from the Intent
            Uri beamUri = mIntent.getData();
            /*
             * Test for the type of URI, by getting its scheme value
             */
            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                mParentPath = handleFileUri(beamUri);
            }
        }
    }

    private String handleFileUri(Uri beamUri) {
        // Get the path part of the URI
        String fileName = beamUri.getPath();
        // Create a File object for this filename
        File copiedFile = new File(fileName);
        // Get a string containing the file's parent directory
        return copiedFile.getParent();
    }

    //Method to check whether external media available and writable. This is adapted from
    //   http://developer.android.com/guide/topics/data/data-storage.html#filesExternal */
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}