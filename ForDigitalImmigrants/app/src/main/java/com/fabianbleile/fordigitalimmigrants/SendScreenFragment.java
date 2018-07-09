package com.fabianbleile.fordigitalimmigrants;

import android.content.Context;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Time;
import java.util.ArrayList;

public class SendScreenFragment extends android.support.v4.app.Fragment implements NfcAdapter.OnNdefPushCompleteCallback{

    public ArrayList<Uri> mFileUris = new ArrayList<Uri>();

    private ArrayList<CompoundButton> listCheckedSwitches = new ArrayList<>();

    OnSendButtonClickedInterface mCallback;

    private Button buttonSend;
    private ProgressBar progressBarSend;
    private EditText etSendMessage;
    private static boolean initializeOnlyOnce = false;

    private Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mCallback.OnSendButtonClickedCheckNfcSettings()){
                JSONObject createdJsonObject = createJsonObject();
                File requestFile = createJsonFile(createdJsonObject);
                Uri jsonDataUri = Uri.fromFile(requestFile);
                Log.e(MainActivity.mTagHandmade, "File URI: "+ jsonDataUri);
                mFileUris.add(jsonDataUri);

                //convert arrayList of Uris to Array of Uris
                Uri[] fileUris = new Uri[mFileUris.size()];
                mFileUris.toArray(fileUris);
                mCallback.OnSendButtonClickedStartSending(fileUris);
            }
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

    public interface OnSendButtonClickedInterface{
        public void OnSendButtonClickedStartSending(Uri[] fileUris);
        public boolean OnSendButtonClickedCheckNfcSettings();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnSendButtonClickedInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        initializeOnlyOnce = false;
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


        if(!initializeOnlyOnce){
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
            initializeOnlyOnce = true;
        }

        buttonSend = (Button) rootView.findViewById(R.id.bt_send);
        buttonSend.setOnClickListener(buttonSendOnClickListener);

        return rootView;
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
            Toast.makeText(getContext(), R.string.toast_fileSaved, Toast.LENGTH_LONG).show();


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
