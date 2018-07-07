package com.fabianbleile.fordigitalimmigrants;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Time;
import java.util.ArrayList;

public class SendScreenFragment extends android.support.v4.app.Fragment implements NfcAdapter.OnNdefPushCompleteCallback{

    public ArrayList<Uri> mFileUris = new ArrayList<Uri>();

    private ArrayList<CompoundButton> listCheckedSwitches = new ArrayList<>();

    OnReadyButtonClickedInterface mCallback;

    private Button buttonReady;
    private Switch switchName;
    private Switch switchPhoneNumber;
    private Switch switchEmail;
    private Switch switchBirthday;
    private Switch switchInstagram;
    private Switch switchFacebook;
    private Switch switchSnapchat;
    private EditText etSendMessage;
    private static boolean msgShown = false;

    private Button.OnClickListener buttonReadyOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            JSONObject createdJsonObject = createJsonObject();
            File requestFile = createJsonFile(createdJsonObject);
            Uri jsonDataUri = Uri.fromFile(requestFile);
            Log.e(MainActivity.mTagHandmade, "File URI: "+ jsonDataUri);
            mFileUris.add(jsonDataUri);

            /*
            ArrayList<String> mFileStrings = new ArrayList<String>();
            for (int i = 0; i < mFileUris.size(); i++) {
                mFileStrings.add(mFileUris.get(i).toString());
            }

            Intent intent = new Intent(getActivity(),nfcPushActivity.class);
            intent.putStringArrayListExtra("mFileString" ,mFileStrings);
            startActivity(intent);
            */

            //convert arrayList of Uris to Array of Uris
            Uri[] fileUris = new Uri[mFileUris.size()];
            mFileUris.toArray(fileUris);
            mCallback.OnReadyButtonClicked(fileUris);
        }
    };

    private Switch.OnCheckedChangeListener switchSendOnCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton switchView, boolean isChecked) {
            if(isChecked){
                listCheckedSwitches.add(switchView);
            } else {
                try {
                    listCheckedSwitches.remove(switchView);
                } catch (NullPointerException e){
                    Log.e(getContext().toString(), "Nullpointer on Switch. " + switchView.getText());
                }
            }
        }
    };

    public SendScreenFragment() {
        // Required empty public constructor
    }

    public static SendScreenFragment newInstance(String param1, String param2) {
        SendScreenFragment fragment = new SendScreenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnReadyButtonClickedInterface{
        public void OnReadyButtonClicked(Uri[] fileUris);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnReadyButtonClickedInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_send_screen, container, false);

        etSendMessage = rootView. findViewById(R.id.et_send_message);

        if(!msgShown){
            LinearLayout linearLayout = (LinearLayout)rootView.findViewById(R.id.linearLayout);
            for (int i = 0; i < MainActivity.mIcons.size(); i++) {
                Switch switchView = new Switch(getContext());
                switchView.setId(MainActivity.mIcons.get(i));
                switchView.setText(getResources().getText(MainActivity.mIcons.get(i)));
                switchView.setTextSize(18);
                switchView.setOnCheckedChangeListener(switchSendOnCheckedListener);
                switchView.setPadding(32,32,32,32);

                linearLayout.addView(switchView,
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            msgShown = true;
        }

        buttonReady = (Button) rootView.findViewById(R.id.bt_ready);
        buttonReady.setOnClickListener(buttonReadyOnClickListener);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        msgShown = false;
    }

    private JSONObject createJsonObject(){
        //create JsonObject with all the required information
        JSONObject jsonObject = new JSONObject();

        for (int i = 0; i < listCheckedSwitches.size(); i++) {
            String key = listCheckedSwitches.get(i).getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            if(value == null){
                Toast.makeText(getContext(), R.string.error_noValueDefined + key, Toast.LENGTH_LONG).show();
            } else {
                try {
                    jsonObject.put(key, value);
                } catch (JSONException e) {
                    Log.e(MainActivity.mTagHandmade, Integer.toString(R.string.error_couldNotPutJsonObject));
                }
            }
        }
        try {
            if(etSendMessage.getText().equals("") == false){
                jsonObject.put(getResources().getString(R.string.ctv_message), etSendMessage.getText());
            }
        } catch (JSONException e) {
            Log.e(MainActivity.mTagHandmade, Integer.toString(R.string.error_couldNotPutJsonObject));
        }

        Log.i(MainActivity.mTagHandmade, jsonObject.toString());

        return jsonObject;
    }

    //create json file to send
    private File createJsonFile(JSONObject jsonObject) {
        File pathToExternalStorage = getContext().getExternalFilesDir(null);
        //to this path add a new directory path and create new App dir (InstroList) in /documents Dir
        File appDirectory = new File(pathToExternalStorage.getAbsolutePath()  + "/sharedPersonalData");
        // have the object build the directory structure, if needed.
        appDirectory.mkdirs();

        File nfcDataFile = new File (appDirectory, BuildNewFileName());

        try {
            Writer output = new BufferedWriter(new FileWriter(nfcDataFile));
            output.write(jsonObject.toString());
            output.close();
            Toast.makeText(getContext(), "Composition saved!", Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(), "" + nfcDataFile, Toast.LENGTH_LONG).show();


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

    private String BuildNewFileName()
    { // creates a new file name
        String fileName;
        Time time = new Time(System.currentTimeMillis());
        fileName = time.toString();
        //Replace (:) with (_)
        fileName = fileName.replaceAll(":", "_");
        fileName = "nfcSharedData_" + fileName + ".json";

        return fileName;
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        //do something
    }
}
