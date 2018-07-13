package com.fabianbleile.fordigitalimmigrants;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements SendScreenFragment.OnReadyButtonClickedInterface {

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
    public static Uri[] mFileUris = new Uri[1];

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

    public MainActivity() {
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

    @Override
    public void OnReadyButtonClicked(Uri[] fileUris) {
        mFileUris = fileUris;
        handleViewIntent(mFileUris[0]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNfcAdapter.invokeBeam(this);
        }
    }

    /**
          * A simple pager adapter that represents 3 ScreenSlidePageFragment objects, in
          * sequence.
          */
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
                    // setup push complete callback
                    /*PushCompleteCallback mPushCompleteCallback = new PushCompleteCallback();
                    mNfcAdapter.setOnNdefPushCompleteCallback(mPushCompleteCallback, this);
                    // Set the dynamic callback for URI requests.
                    FileUriCallback mFileUriCallback = new FileUriCallback();
                    mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);*/

                    // NdefMessage Test
                } else {
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                }
            }
        }
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

    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
        private FileUriCallback() {

        }
        @Override
        public Uri[] createBeamUris(NfcEvent nfcEvent) {
            return mFileUris;
        }
    }

    private class PushCompleteCallback implements NfcAdapter.OnNdefPushCompleteCallback{
        @Override
        public void onNdefPushComplete(NfcEvent nfcEvent) {
            // show Snackbar
            showSnackbarForDefaultFile();
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(3500);
                        // As I am using LENGTH_LONG in Snackbar
                        File file = new File(mFileUris[0].getPath());
                        if (!file.delete())
                            if (file.exists()) {
                                if (!file.getCanonicalFile().delete())
                                    if (file.exists()) {
                                        getApplicationContext().deleteFile(file.getName());
                                    }
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    private void showSnackbarForDefaultFile() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, getResources().getText(R.string.question_setDefaultFile), Snackbar.LENGTH_LONG);
        snackbar.setAction(getResources().getText(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // yes is selected, store the file as default
                setDefaults("defaultFilePath", mFileUris[0].getPath(), getApplicationContext());
            }
        });
        snackbar.setActionTextColor(Color.MAGENTA);
        snackbar.show();
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

    // Incoming Intent
    private Intent mIntent;
    private File mCopiedFile = null;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);;
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get the Intent action
        Intent intent = getIntent();
        if(TextUtils.equals(intent.getAction(), "com.fabianbleile.fordigitalimmigrants.LAUNCH")
                || TextUtils.equals(intent.getAction(), Intent.ACTION_VIEW)){
            mIntent = intent;

            // Get the URI from the Intent
            Uri beamUri = mIntent.getData();
            Log.e("beamUri", beamUri +"");
            handleViewIntent(beamUri);
        }
    }

    /*
     * handleViewIntent called from onNewIntent() for a SINGLE_TOP Activity or onCreate() for a new Activity.
     * For onNewIntent(), remember to call setIntent() to store the most current Intent
     */
    private void handleViewIntent(Uri beamUri) {
        //show receive Fragment (kind of a test to show the user it is going to be processed immiediately)
        mPager.setCurrentItem(2);

        boolean isContentUri = true;

        if (TextUtils.equals(beamUri.getScheme(), "file")) {
            mCopiedFile = handleFileUri(beamUri);
            isContentUri = false;
        } else if (TextUtils.equals(beamUri.getScheme(), "content")) {
            mCopiedFile = handleContentUri(beamUri);
            isContentUri = true;
        }

        // setup an input stream for the file to retrieve the data
        String jsonStringIntentData = getJsonStringFromStorage(mCopiedFile, isContentUri);
        Log.e("jsonStringIntentData", jsonStringIntentData +"");

        // get Contact from the Json String with Gson
        Contact contact = getContactFromJsonString(jsonStringIntentData);
        Log.e("Contact", contact +"");

        // to Reiceive Fragment
        ReceiveScreenFragment.onFileIncome(contact);
    }

    private File handleFileUri(Uri beamUri) {
        // Get the path part of the URI
        String fileName = beamUri.getPath();
        // Create a File object for this filename
        return new File(fileName);
    }

    public File handleContentUri(Uri beamUri) {
        // Position of the filename in the query Cursor
        int filenameIndex;
        // File object for the filename
        File copiedFile;
        // The filename stored in MediaStore
        String fileName;
        // Test the authority of the URI
        if (!TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
            /*
             * Handle content URIs for other content providers
             */
            return null;
            // For a MediaStore content URI
        } else {
            // Get the column that contains the file name
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor pathCursor =
                    getContentResolver().query(beamUri, projection,
                            null, null, null);
            // Check for a valid cursor
            if (pathCursor != null &&
                    pathCursor.moveToFirst()) {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(
                        MediaStore.MediaColumns.DATA);
                // Get the full file name including path
                fileName = pathCursor.getString(filenameIndex);
                // Create a File object for the filename
                copiedFile = new File(fileName);
                copiedFile = new File(copiedFile.getPath());
                // Return the file
                return copiedFile;
            } else {
                // The query didn't work; return null
                return null;
            }
        }
    }



    private String getJsonStringFromStorage(File copiedFile, boolean isContentUri) {
        String ret = "";
        InputStream inputStream = null;
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader;

        try {
            if(isContentUri){
                Uri uri = android.net.Uri.parse(new java.net.URI(copiedFile.toURI().toString()).toString());
                inputStream = getContentResolver().openInputStream(uri);
                inputStreamReader = new InputStreamReader(inputStream);
            } else {
                //InputStream inputStream = mContext.openFileInput(copiedFile.getName());
                fileInputStream = new FileInputStream(copiedFile);
                inputStreamReader = new InputStreamReader(fileInputStream);
            }


            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            if(isContentUri){
                inputStream.close();
            } else {
                fileInputStream.close();
            }
            ret = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.e("readFromFileAsyncTask", "File not found: " + e.toString() + ", " + copiedFile.toURI());
        } catch (IOException e) {
            Log.e("readFromFileAsyncTask", "Can not read file: " + e.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private Contact getContactFromJsonString(String jsonStringIntentData) {
        // gson deserialization
        Gson gson = new Gson();
        return gson.fromJson(jsonStringIntentData, Contact.class);
    }




    //---------------------------------------------------------------------------------------------------------------------------------
    // test (if notification cannot be opened)
    private void test(){
        String test = getResources().getString(R.string.test_jsonString);
        new buildContactAsyncTask().execute(test);
    }

    public static String message;

    private static class buildContactAsyncTask extends AsyncTask<String, Void, Contact> {

        Contact contact; String name; String phonenumber; String email;
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
        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------
}
