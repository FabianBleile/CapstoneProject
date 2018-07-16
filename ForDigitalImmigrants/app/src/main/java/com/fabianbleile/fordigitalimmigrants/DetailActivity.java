package com.fabianbleile.fordigitalimmigrants;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import com.fabianbleile.fordigitalimmigrants.Adapter.ImageAdapter;
import com.fabianbleile.fordigitalimmigrants.Fragment.ReceiveScreenFragment;
import com.fabianbleile.fordigitalimmigrants.data.Contact;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private GridView gridViewSettingsIcons;
    private Contact mContact;
    private boolean editMode;

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
            if(editMode){
                createSettingsAlertDialog(view, i);
            } else {
                setIntentsOnItems(view.getTag());
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*
            case R.id.save_to_contacts :
                Toast.makeText(this, "Save contact to contacts", Toast.LENGTH_SHORT).show();
                return true;
             */
            /*
            case R.id.switch_edit_mode :
                Switch editModeSwitch = findViewById(R.id.switch_edit_mode);
                editMode = editModeSwitch.isChecked();
                return true;
                */
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_detail, menu);
        return true;
    }

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

    // save to contacts
    private void saveContactToPhone(){
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                //.withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "1-800-GOOG-411")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "free directory assistance")
                .build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    //editMode activated creates AlertDialog for entering the information
    private void createSettingsAlertDialog(final View view, final int i){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_settings, null);
        dialogBuilder.setView(dialogView);

        final EditText etInsertInfo = (EditText) dialogView.findViewById(R.id.et_insert_info);
        etInsertInfo.setText(setTextOfAlertDialog(i));

        dialogBuilder.setCancelable(true);
        dialogBuilder.setTitle(view.getTag().toString());
        dialogBuilder.setPositiveButton(R.string.bt_ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                updateContact(i, etInsertInfo.getText().toString());
                ReceiveScreenFragment.viewModel.addItem(mContact);
            }
        });
        AlertDialog ad = dialogBuilder.create();
        ad.show();
    }
    private void updateContact(int position, String etInfo){
        switch (position){
            case 0 : mContact.setName(etInfo);
            case 1 : mContact.setPhonenumber(etInfo);
            case 2 : mContact.setEmail(etInfo);
            case 3 : mContact.setBirthday(etInfo);
            case 4 : mContact.setHometown(etInfo);
            case 5 : mContact.setInstagram(etInfo);
            case 6 : mContact.setFacebook(etInfo);
            case 7 : mContact.setSnapchat(etInfo);
            case 8 : mContact.setTwitter(etInfo);
            case 9 : mContact.setLocation(etInfo);
        }
    }
    private String setTextOfAlertDialog(int position){
        String string = "";
        switch (position){
            case 0 :
                string = mContact.getName();
            case 1 :
                string = mContact.getPhonenumber();
            case 2 :
                string = mContact.getEmail();
            case 3 :
                string = mContact.getBirthday();
            case 4 :
                string = mContact.getHometown();
            case 5 :
                string = mContact.getInstagram();
            case 6 :
                string = mContact.getFacebook();
            case 7 :
                string = mContact.getSnapchat();
            case 8 :
                string = mContact.getTwitter();
            case 9 :
                string = mContact.getLocation();
                default:
                    string = "";
        }
        return string;
    }
    // editMode deactivated
    private void setIntentsOnItems(Object tag) {
        String tagResource = (String) tag;
        Intent intent = new Intent();

        if(tagResource.equals(this.getResources().getString(R.string.ctv_facebook))){

            String facebook = mContact.getFacebook();
            String facebookKeyWords = facebook.replace(" ", "+");
            intent = new Intent(Intent.ACTION_VIEW, getFacebookUri(facebookKeyWords));

        } else if(tagResource.equals(this.getResources().getString(R.string.ctv_instagram))){

            intent = new Intent(Intent.ACTION_VIEW, getInstagramUri(mContact.getInstagram()));

        }else if(tagResource.equals(this.getResources().getString(R.string.ctv_snapchat))){

            addToClipboard(tagResource, mContact.getSnapchat());
            intent = getPackageManager().getLaunchIntentForPackage("com.snapchat.android");

        }else if(tagResource.equals(this.getResources().getString(R.string.ctv_twitter))){

            intent = new Intent(Intent.ACTION_VIEW, getTwitterUri(mContact.getTwitter()));
        }
    }
    private Uri getFacebookUri(String facebookKeyWords) {
        // example link             https://  www.facebook.com       /search   /str     /prename+lastname /keywords_search
        // stackoverflow example    https://  www.myawesomesite.com  /turtles  /types   ?type=1     &sort=relevance     #section-name
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.facebook.com")
                .appendPath("search")
                .appendPath("str")
                .appendPath(facebookKeyWords)
                .appendPath("keywords_search")
        ;
        return builder.build();
    }
    private Uri getInstagramUri(String username){
        //https://www.instagram.com/kaddabra.ina/
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.instagram.com")
                .appendPath(username)
        ;
        return builder.build();
    }
    private Uri getTwitterUri(String username){
        //https://twitter.com/realdonaldtrump
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("twitter.com")
                .appendPath(username)
        ;
        return builder.build();
    }

    private void addToClipboard(String label, String text){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(this.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, text + R.string.action_addedToClipboard, Toast.LENGTH_SHORT).show();
    }
}