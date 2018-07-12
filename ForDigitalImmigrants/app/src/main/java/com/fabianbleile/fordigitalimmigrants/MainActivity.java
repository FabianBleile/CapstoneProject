package com.fabianbleile.fordigitalimmigrants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.fabianbleile.fordigitalimmigrants.data.Contact;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements SendScreenFragment.OnReadyButtonClickedInterface{

    public static final String mTagHandmade = "HANDMADETAG";
    public static Context mContext;
    public static ArrayList<Integer> mIcons = new ArrayList<Integer>();

    private Fragment sendFrag;
    private Fragment settFrag;
    private Fragment recFrag;

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
                    test();
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
                    settFrag = new SettingsScreenFragment();
                    return settFrag;
                case 1 :
                    sendFrag = new SendScreenFragment();
                    return sendFrag;
                case 2 :
                    recFrag = new ReceiveScreenFragment();
                    return recFrag;
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

        mContext = getApplicationContext();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(viewPageOnPageListener);
        mPager.setCurrentItem(1);

        mIcons.add(R.string.ctv_name); mIcons.add(R.string.ctv_phone_number); mIcons.add(R.string.ctv_email);
        mIcons.add(R.string.ctv_birthday); mIcons.add(R.string.ctv_hometown); mIcons.add(R.string.ctv_instagram);
        mIcons.add(R.string.ctv_facebook); mIcons.add(R.string.ctv_snapchat); mIcons.add(R.string.ctv_twitter);
        mIcons.add(R.string.ctv_currentLocation);

        if(isExternalStorageReadable() && isExternalStorageWritable()) {
            // Android Beam file transfer is available, continue
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

            if(mNfcAdapter != null){
                if(mNfcAdapter.isEnabled()){
                    mNfcAdapter.enableReaderMode(this, mReaderModeCallback, NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
                    mFileUriCallback = new FileUriCallback();
                    // Set the dynamic callback for URI requests.
                    mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback,this);
                } else {
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                }
            }
        }
    }

    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {

        }
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

    //-------------------------------------------------------------------------------------------------------------------

    // A File object containing the path to the transferred files
    private String mParentPath;
    // Incoming Intent
    private Intent mIntent;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Get the Intent action
        mIntent = getIntent();
        setIntent(mIntent);
        String action = mIntent.getAction();

        /*
         * For ACTION_VIEW, the Activity is being asked to display data.
         * Get the URI.
         */
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            handleViewIntent();
        }
    }

    /*
         * Called from onNewIntent() for a SINGLE_TOP Activity
         * or onCreate() for a new Activity. For onNewIntent(),
         * remember to call setIntent() to store the most
         * current Intent
         *
         */
    private void handleViewIntent() {
        //show receive Fragment (kind of a test to show the user it is going to be processed immiediately)
        mPager.setCurrentItem(2);

        // Get the URI from the Intent
        Uri beamUri = mIntent.getData();
            /*
             * Test for the type of URI, by getting its scheme value
             */
        if (TextUtils.equals(beamUri.getScheme(), "file")) {
            // Get the path part of the URI
            String fileName = beamUri.getPath();
            // Create a File object for this filename
            final File copiedFile = new File(fileName);
            mParentPath = copiedFile.getParent();
            //start AsyncTask to read from file
            new readFromFileAsyncTask().execute(fileName);
        }
    }

    private static class readFromFileAsyncTask extends AsyncTask<String, Void, String> {

        readFromFileAsyncTask() {}

        @Override
        protected String doInBackground(String... strings) {
            String ret = "";

            try {
                InputStream inputStream = mContext.openFileInput(strings[0]);

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            } catch (FileNotFoundException e) {
                Log.e("readFromFileAsyncTask", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("readFromFileAsyncTask", "Can not read file: " + e.toString());
            }

            return ret;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new buildContactAsyncTask().execute(s);
        }

    }

    // test (if notification cannot be opened)
    private void test(){
        String test = getResources().getString(R.string.test_jsonString);
        new buildContactAsyncTask().execute(test);
    }

    public static String message;

    private static class buildContactAsyncTask extends AsyncTask<String, Void, Contact> {

        Contact contact; int cid; String name; String phonenumber; String email;
        String birthday; String hometown; String instagram; String facebook;
        String snapchat; String twitter; String location; JSONObject jsonObject;

        buildContactAsyncTask() {
        }

        @Override
        protected Contact doInBackground(final String... params) {
            try {
                jsonObject = new JSONObject(params[0]);
            } catch (JSONException e) { e.printStackTrace(); }

            if(jsonObject != null){
                try { message = jsonObject.getString("Message"); } catch (NullPointerException e){message = "not given";} catch (JSONException e){}

                try { name = jsonObject.getString("Name");} catch (NullPointerException e){name = "not given";} catch (JSONException e){}
                try { phonenumber = jsonObject.getString("Phone Number"); } catch (NullPointerException e){phonenumber = "not given";} catch (JSONException e){}
                try { email = jsonObject.getString("E-Mail"); } catch (NullPointerException e){email = "not given";} catch (JSONException e){}
                try { birthday = jsonObject.getString("Birthday"); } catch (NullPointerException e){birthday = "not given";} catch (JSONException e){}
                try { hometown = jsonObject.getString("Hometown"); } catch (NullPointerException e){hometown = "not given";} catch (JSONException e){}
                try { instagram = jsonObject.getString("Instagram"); } catch (NullPointerException e){instagram = "not given";} catch (JSONException e){}
                try { facebook = jsonObject.getString("Facebook"); } catch (NullPointerException e){facebook = "not given";} catch (JSONException e){}
                try { snapchat = jsonObject.getString("Snapchat"); } catch (NullPointerException e){snapchat = "not given";} catch (JSONException e){}
                try { twitter = jsonObject.getString("Twitter"); } catch (NullPointerException e){twitter = "not given";} catch (JSONException e){}
                try { location = jsonObject.getString("Location"); } catch (NullPointerException e){location = "not given";} catch (JSONException e){}
            }

            contact = new Contact(name, phonenumber, email, birthday, hometown, instagram, facebook, snapchat, twitter, location);

            return contact;
        }

        @Override
        protected void onPostExecute(Contact contact) {
            super.onPostExecute(contact);
            ReceiveScreenFragment.onFileIncome(contact);
            Toast.makeText(mContext, "" + message, Toast.LENGTH_SHORT).show();
        }
    }
}
