package com.fabianbleile.fordigitalimmigrants.Fragment;

import android.content.Context;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.fabianbleile.fordigitalimmigrants.MainActivity;
import com.fabianbleile.fordigitalimmigrants.R;
import com.fabianbleile.fordigitalimmigrants.data.Contact;
import com.google.gson.Gson;

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

    OnReadyButtonClickedInterface mCallback;

    private Button buttonReady;
    private Switch name;
    private Switch phonenumber;
    private Switch email;
    private Switch birthday;
    private Switch hometown;
    private Switch instagram;
    private Switch facebook;
    private Switch snapchat;
    private Switch twitter;
    private Switch location;
    private static boolean msgShown = false;

    private Button.OnClickListener buttonReadyOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Contact contact = getShareContact();
            File requestFile = createTxtFile(contact);
            Uri fileUri = Uri.fromFile(requestFile);
            Log.e(MainActivity.mTagHandmade, "File URI: "+ fileUri);
            mFileUris.add(fileUri);
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
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        msgShown = false;
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

        name = rootView.findViewById(R.id.ctv_name);
        phonenumber = rootView.findViewById(R.id.ctv_phone_number);
        email = rootView.findViewById(R.id.ctv_email);
        birthday = rootView.findViewById(R.id.ctv_birthday);
        hometown = rootView.findViewById(R.id.ctv_hometown);
        instagram = rootView.findViewById(R.id.ctv_instagram);
        facebook = rootView.findViewById(R.id.ctv_facebook);
        snapchat = rootView.findViewById(R.id.ctv_snapchat);
        twitter = rootView.findViewById(R.id.ctv_twitter);
        location = rootView.findViewById(R.id.ctv_currentLocation);

        name.setChecked(true);
        phonenumber.setChecked(true);
        email.setChecked(true);
        hometown.setChecked(true);

        buttonReady = (Button) rootView.findViewById(R.id.bt_ready);
        buttonReady.setOnClickListener(buttonReadyOnClickListener);

        return rootView;
    }

    private Contact getShareContact(){
        Contact contact;
        String sname="";String sphonenumber="";String semail="";String sbirthday="";
        String shometown="";String sinstagram="";String sfacebook="";String ssnapchat="";
        String stwitter="";String slocation="";

        if(name.isChecked()){
            String key = name.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            sname = value;
        } else if (phonenumber.isChecked()){
            String key = phonenumber.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            sphonenumber = value;
        } else if (email.isChecked()){
            String key = email.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            semail = value;
        } else if (birthday.isChecked()){
            String key = birthday.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            sbirthday = value;
        } else if (hometown.isChecked()){
            String key = hometown.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            shometown = value;
        } else if (instagram.isChecked()){
            String key = instagram.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            sinstagram = value;
        } else if (facebook.isChecked()){
            String key = facebook.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            sfacebook = value;
        } else if (snapchat.isChecked()){
            String key = snapchat.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            ssnapchat = value;
        } else if (twitter.isChecked()){
            String key = twitter.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            stwitter = value;
        } else if (location.isChecked()){
            String key = location.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            slocation = value;
        }

        contact = new Contact(sname, sphonenumber, semail, sbirthday, shometown, sinstagram, sfacebook, ssnapchat, stwitter, slocation);

        return contact;
    }

    //create json file to send
    public File createTxtFile(Contact contact) {
        File pathToExternalStorage = getContext().getExternalFilesDir(null);
        //to this path add a new directory path and create new App dir (InstroList) in /documents Dir
        File appDirectory = new File(pathToExternalStorage.getAbsolutePath()  + "/sharedPersonalData");
        // have the object build the directory structure, if needed.
        appDirectory.mkdirs();
        // create new file with path and name
        File nfcDataFile = new File (appDirectory, makeNewFileName());
        //make gson from contactObject
        Gson gson = new Gson();
        String jsonString = gson.toJson(contact);

        try {
            Writer output = new BufferedWriter(new FileWriter(nfcDataFile));
            output.write(jsonString);
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

    private String makeNewFileName() {
        // creates a new file name
        String fileName;
        Time time = new Time(System.currentTimeMillis());
        fileName = time.toString();
        //Replace (:) with (_)
        fileName = fileName.replaceAll(":", "_");
        fileName = "FastContactShare" + fileName + ".txt";

        return fileName;
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        //do something
    }
}
