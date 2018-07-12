package com.fabianbleile.fordigitalimmigrants;

import android.content.Intent;
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

        Intent i = getIntent();
        Contact contact = (Contact) i.getParcelableExtra("selectedContact");
        Log.e("Test", contact.toString());
        mImageAdapter = new ImageAdapter(this, true, contact);

        gridViewSettingsIcons = (GridView) findViewById(R.id.gv_settings_screen);
        gridViewSettingsIcons.setAdapter(mImageAdapter);
        gridViewSettingsIcons.setOnItemClickListener(gvOnClickListener);
        gridViewSettingsIcons.setOnItemLongClickListener(gvOnLongClickListener);
    }
}
