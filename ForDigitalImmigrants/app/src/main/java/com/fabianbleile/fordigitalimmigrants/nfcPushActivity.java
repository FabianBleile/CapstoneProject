package com.fabianbleile.fordigitalimmigrants;

import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class nfcPushActivity extends AppCompatActivity {

    private static NfcAdapter mNfcAdapter;
    private Uri[] mFileUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_push);

        ArrayList<String> intentStringArrayList = getIntent().getStringArrayListExtra("mFileString");
        mFileUris = new Uri[intentStringArrayList.size()];
        for (int i = 0; i < mFileUris.length; i++) {
            mFileUris[i] = Uri.parse(intentStringArrayList.get(i));
        }

        sendFile(mFileUris);
    }

    public void sendFile(Uri[] mFileUris) {
        if(isExternalStorageReadable() && isExternalStorageWritable()) {
            // Android Beam file transfer is available, continue
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

            if (mNfcAdapter != null) {
                Log.w(MainActivity.mTagHandmade, "NFC available.");

                // Set callback when push compllete
                //mNfcAdapter.setOnNdefPushCompleteCallback(this, getActivity());

                if(mFileUris[0] != null) {
                    // Set the dynamic callback for URI requests.
                    mNfcAdapter.setBeamPushUris(mFileUris, this);
                    Log.w(MainActivity.mTagHandmade, "Setting Beam Push URI callback");

                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mNfcAdapter.invokeBeam(nfcPushActivity.this);
                    }*/
                } else {
                    Log.w(MainActivity.mTagHandmade, "mFileUris is empty");
                }
            } else {
                Log.w(MainActivity.mTagHandmade, "NFC is not available");
            }
        }
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
