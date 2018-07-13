package com.fabianbleile.fordigitalimmigrants.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.fabianbleile.fordigitalimmigrants.MainActivity;
import com.fabianbleile.fordigitalimmigrants.R;
import com.fabianbleile.fordigitalimmigrants.data.Contact;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SendScreenFragment extends android.support.v4.app.Fragment {

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
            Gson gson = new Gson();
            String contactString = gson.toJson(contact);

            mCallback.OnReadyButtonClicked(contactString);
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
        public void OnReadyButtonClicked(String fileUris);
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
        } if (phonenumber.isChecked()){
            String key = phonenumber.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            sphonenumber = value;
        } if (email.isChecked()){
            String key = email.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            semail = value;
        } if (birthday.isChecked()){
            String key = birthday.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            sbirthday = value;
        } if (hometown.isChecked()){
            String key = hometown.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            shometown = value;
        } if (instagram.isChecked()){
            String key = instagram.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            sinstagram = value;
        } if (facebook.isChecked()){
            String key = facebook.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            sfacebook = value;
        } if (snapchat.isChecked()){
            String key = snapchat.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            ssnapchat = value;
        } if (twitter.isChecked()){
            String key = twitter.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            stwitter = value;
        } if (location.isChecked()){
            String key = location.getText().toString();
            String value = MainActivity.getDefaults(key, getActivity());
            slocation = value;
        }

        contact = new Contact(sbirthday, semail, sfacebook, shometown, sinstagram, slocation, sname, sphonenumber, ssnapchat, stwitter);
        Log.e("contact", contact.toString());

        return contact;
    }
}
