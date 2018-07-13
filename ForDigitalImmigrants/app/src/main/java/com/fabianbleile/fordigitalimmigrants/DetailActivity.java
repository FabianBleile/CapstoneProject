package com.fabianbleile.fordigitalimmigrants;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.fabianbleile.fordigitalimmigrants.Adapter.ImageAdapter;
import com.fabianbleile.fordigitalimmigrants.data.Contact;

public class DetailActivity extends AppCompatActivity {
    private GridView gridViewSettingsIcons;
    private Contact mContact;

    private ImageAdapter mImageAdapter;
    private AdapterView.OnItemLongClickListener gvOnLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            return false;
        }
    };
    private AdapterView.OnItemClickListener gvOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle b = getIntent().getBundleExtra("bundle");
        b.setClassLoader(Contact.class.getClassLoader());
        mContact = (Contact) b.getParcelable("selectedContact");

        Log.e("Test", mContact.toString());
        mImageAdapter = new ImageAdapter(this, true, mContact);

        gridViewSettingsIcons = (GridView) findViewById(R.id.gv_settings_screen);
        gridViewSettingsIcons.setAdapter(mImageAdapter);
        gridViewSettingsIcons.setOnItemClickListener(gvOnClickListener);
        gridViewSettingsIcons.setOnItemLongClickListener(gvOnLongClickListener);
    }
}