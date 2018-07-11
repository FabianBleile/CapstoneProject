package com.fabianbleile.fordigitalimmigrants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.fabianbleile.fordigitalimmigrants.Adapter.ImageAdapter;

public class SettingsScreenFragment extends android.support.v4.app.Fragment {

    private GridView gridViewSettingsIcons;
    private EditText etInsertInfo;

    private ImageAdapter mImageAdapter;

    private AdapterView.OnItemClickListener gvSettingsScreenOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            createSettingsAlertDialog(view, i);
        }
    };

    public SettingsScreenFragment() {
        // Required empty public constructor
    }
    public static SettingsScreenFragment newInstance(String param1, String param2) {
        SettingsScreenFragment fragment = new SettingsScreenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
         mImageAdapter = new ImageAdapter(getActivity());

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_settings_screen, container, false);

        gridViewSettingsIcons = (GridView) rootView.findViewById(R.id.gv_settings_screen);
        gridViewSettingsIcons.setAdapter(mImageAdapter);
        gridViewSettingsIcons.setOnItemClickListener(gvSettingsScreenOnClickListener);

        return rootView;
    }

    //creates AlertDialog for entering the information
    private void createSettingsAlertDialog(final View view, final int i){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_settings, null);
        dialogBuilder.setView(dialogView);

        etInsertInfo = (EditText) dialogView.findViewById(R.id.et_insert_info);
        etInsertInfo.setText(MainActivity.getDefaults(view.getTag().toString(), getContext()));

        dialogBuilder.setCancelable(true);
        dialogBuilder.setTitle(view.getTag().toString());
        dialogBuilder.setPositiveButton(R.string.bt_ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if(etInsertInfo.getText().toString() != null || etInsertInfo.getText().toString().isEmpty() == false){
                    insertSharedPreferences(view, etInsertInfo.getText().toString(), i);
                }
            }
        });
        AlertDialog ad = dialogBuilder.create();
        ad.show();
    }

    //opens onClick in the AlertDialog and stores the information from the EditText in SharedPreferences
    private void insertSharedPreferences(View view, String editTextContent, int i) {
        String key = view.getTag().toString();
        MainActivity.setDefaults(key, editTextContent, getContext());
        mImageAdapter.notifyDataSetChanged();
    }
}
