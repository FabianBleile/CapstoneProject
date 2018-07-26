package com.fabianbleile.fordigitalimmigrants;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
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
                ArrayList<Intent> intentArrayList = setIntentsOnItems(view.getTag());
                try {
                    if (intentArrayList != null){
                        if(intentArrayList.get(0) != null){
                            if (intentArrayList.get(0).resolveActivity(getPackageManager()) != null){
                                startActivity(intentArrayList.get(0));
                            }
                        } else if (intentArrayList.get(1) != null){
                            if (intentArrayList.get(1).resolveActivity(getPackageManager()) != null){
                                startActivity(intentArrayList.get(1));
                            }
                        }
                }
                } catch (NullPointerException e){
                    Toast.makeText(DetailActivity.this, getResources().getString(R.string.info_notPossibleToExecuteIntent), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.save_to_contacts :
                Toast.makeText(this, getResources().getString(R.string.saveContactToContacts), Toast.LENGTH_SHORT).show();
                insertContact();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }
        return true;
    }
    @Override
    public void onClick(View view) {
        editMode = !editMode;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_detail, menu);
        MenuItem switchCompatItem = menu.findItem(R.id.switch_edit_mode);
        SwitchCompat switchCompat = (SwitchCompat) switchCompatItem.getActionView();
        switchCompat.setOnClickListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle b = getIntent().getBundleExtra(getResources().getString(R.string.bundle));
        b.setClassLoader(Contact.class.getClassLoader());
        mContact = b.getParcelable(getResources().getString(R.string.selectedContact));

        mImageAdapter = new ImageAdapter(this, true, mContact);

        GridView gridViewSettingsIcons = findViewById(R.id.gv_settings_screen);
        gridViewSettingsIcons.setAdapter(mImageAdapter);
        gridViewSettingsIcons.setOnItemClickListener(gvOnClickListener);
        gridViewSettingsIcons.setOnItemLongClickListener(gvOnLongClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReceiveScreenFragment.viewModel.addItem(mContact);
    }

    // save to contacts
    private void insertContact(){
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setType(ContactsContract.Contacts.CONTENT_TYPE)
                    .putExtra(ContactsContract.Intents.Insert.NAME, mContact.getName())
                    .putExtra(ContactsContract.Intents.Insert.EMAIL, mContact.getEmail())
                    .putExtra(ContactsContract.Intents.Insert.PHONE, mContact.getPhonenumber())
                    .putExtra(ContactsContract.Intents.Insert.POSTAL, mContact.getLocation())
                    .putExtra(ContactsContract.Intents.Insert.NOTES, mContact.getFacebook() + mContact.getInstagram() + mContact.getSnapchat() + mContact.getTwitter())
                    ;
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
    }
    //----------------------------------------------------------------------------------------------
    //editMode activated creates AlertDialog for entering the information
    private void createSettingsAlertDialog(final View view, final int i){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialog_settings, null);
        dialogBuilder.setView(dialogView);

        final EditText etInsertInfo = dialogView.findViewById(R.id.et_insert_info);
        etInsertInfo.setText(setTextOfAlertDialog(i));

        dialogBuilder.setCancelable(true);
        dialogBuilder.setTitle(view.getTag().toString());
        dialogBuilder.setPositiveButton(R.string.bt_ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                updateContact(i, etInsertInfo.getText().toString());
                mImageAdapter.notifyDataChange(mContact);
            }
        });
        AlertDialog ad = dialogBuilder.create();
        ad.show();
    }
    private void updateContact(int position, String etInfo){
        switch (position){
            case 0 :
                mContact.setName(etInfo);
                break;
            case 1 :
                mContact.setPhonenumber(etInfo);
                break;
            case 2 :
                mContact.setEmail(etInfo);
                break;
            case 3 :
                mContact.setBirthday(etInfo);
                break;
            case 4 :
                mContact.setHometown(etInfo);
                break;
            case 5 :
                mContact.setInstagram(etInfo);
                break;
            case 6 :
                mContact.setFacebook(etInfo);
                break;
            case 7 :
                mContact.setSnapchat(etInfo);
                break;
            case 8 :
                mContact.setTwitter(etInfo);
                break;
            case 9 :
                mContact.setLocation(etInfo);
                break;
        }
    }
    private String setTextOfAlertDialog(int position){
        String string = "";
        switch (position){
            case 0 :
                try{
                    string = mContact.getName();
                    break;
                }catch (NullPointerException e){};
            case 1 :
                try{
                    string = mContact.getPhonenumber();
                    break;
                }catch (NullPointerException e){};
            case 2 :
                try{
                    string = mContact.getEmail();
                    break;
                }catch (NullPointerException e){};
            case 3 :
                try{
                    string = mContact.getBirthday();
                    break;
                }catch (NullPointerException e){};
            case 4 :
                try{
                    string = mContact.getHometown();
                    break;}
                    catch (NullPointerException e){};
            case 5 :
                try{
                    string = mContact.getInstagram();
                    break;
                }catch (NullPointerException e){};
            case 6 :
                try{
                    string = mContact.getFacebook();
                    break;
                }catch (NullPointerException e){};
            case 7 :
                try{
                    string = mContact.getSnapchat();
                    break;
                }catch (NullPointerException e){};
            case 8 :
                try{
                    string = mContact.getTwitter();
                    break;
                }catch (NullPointerException e){};
            case 9 :
                try{
                    string = mContact.getLocation();
                    break;
                }catch (NullPointerException e){};
                default:
                    string = "";
        }
        return string;
    }

    //----------------------------------------------------------------------------------------------
    // editMode deactivated
    private ArrayList<Intent> setIntentsOnItems(Object tag) {
        String tagResource = (String) tag;
        ArrayList<Intent> intentArray = new ArrayList<>();

        if(tagResource.equals(this.getResources().getString(R.string.ctv_facebook))){                           // facebook intent is working
            if (mContact.getFacebook() != null){
                addToClipboard(tagResource, mContact.getFacebook());
                intentArray.add(getPackageManager().getLaunchIntentForPackage(getResources().getString(R.string.comfacebookkatana)));
                String facebook = mContact.getFacebook();
                String facebookKeyWords = facebook.replace(" ", "+");
                intentArray.add(new Intent(Intent.ACTION_VIEW, getFacebookUri(facebookKeyWords)));
                return intentArray;
            } else {
                return  null;
            }

        } else if(tagResource.equals(this.getResources().getString(R.string.ctv_instagram))){
            if(mContact.getInstagram() != null){
                addToClipboard(tagResource, mContact.getInstagram());
                intentArray.add(getPackageManager().getLaunchIntentForPackage( getResources().getString(R.string.cominstagramandroid)));
                intentArray.add(new Intent(Intent.ACTION_VIEW, getInstagramUri(mContact.getInstagram())));
                return intentArray;
            } else {
                return null;
            }

        }else if(tagResource.equals(this.getResources().getString(R.string.ctv_twitter))){
            if(mContact.getTwitter() != null){
                addToClipboard(tagResource, mContact.getTwitter());
                intentArray.add(new Intent(Intent.ACTION_VIEW, getTwitterUri(mContact.getTwitter())));
                return intentArray;
            } else {
                return null;
            }
        } else if (tagResource.equals(this.getResources().getString(R.string.ctv_email))){
            if(mContact.getEmail() != null){
                addToClipboard(tagResource, mContact.getEmail());
                Intent intent = new Intent(Intent.ACTION_SENDTO)
                        .setData(Uri.parse(getResources().getString(R.string.mailto)))
                        .putExtra(Intent.EXTRA_EMAIL, mContact.getEmail());
                //intent.putExtra(Intent.EXTRA_SUBJECT, "Hey there");
                intentArray.add(intent);
                return intentArray;
            } else {
                return null;
            }
        } else if (tagResource.equals(this.getResources().getString(R.string.ctv_birthday))){
            if (mContact.getBirthday() != null){
                try{
                    addToClipboard(tagResource, mContact.getBirthday());

                    Date date = new SimpleDateFormat(getResources().getString(R.string.dateFromPattern), Locale.GERMAN).parse(mContact.getBirthday());
                    long dateMillis = date.getTime();

                    String name = "";
                    if (mContact.getName() != null){
                        name = mContact.getName();
                    }

                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.Events.TITLE, getResources().getString(R.string.action_calender_title) + name)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateMillis)
                            .putExtra(CalendarContract.Instances.EVENT_ID, 1);

                    Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                    builder.appendPath("time");
                    ContentUris.appendId(builder, dateMillis);
                    intentArray.add(new Intent(Intent.ACTION_VIEW).setData(builder.build()));
                    return intentArray;
                } catch (NullPointerException e){
                    Toast.makeText(this, getResources().getString(R.string.info_wrongDateFormat), Toast.LENGTH_SHORT).show();
                    return null;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                return null;
            }
        }
        return null;

        /*
        else if(tagResource.equals(this.getResources().getString(R.string.ctv_snapchat))){                         // snapchat intent ??
            if (mContact.getSnapchat() != null) {
                addToClipboard(tagResource, mContact.getSnapchat());
                intentArray.add(getPackageManager().getLaunchIntentForPackage("com.snapchat.android"));
                return intentArray;
            } else {
                return null;
            }

        }
        */
    }
    private Uri getFacebookUri(String facebookKeyWords) {
        // example link             https://  www.facebook.com       /search   /str     /prename+lastname /keywords_search
        // stackoverflow example    https://  www.myawesomesite.com  /turtles  /types   ?type=1     &sort=relevance     #section-name
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getResources().getString(R.string.httpsScheme))
                .authority(getResources().getString(R.string.facebookWebsite))
                .appendPath(getResources().getString(R.string.search))
                .appendPath(getResources().getString(R.string.str))
                .appendPath(facebookKeyWords)
                .appendPath(getResources().getString(R.string.keywords_search))
        ;
        return builder.build();
    }
    private Uri getInstagramUri(String username){
        //https://www.instagram.com/kaddabra.ina/
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getResources().getString(R.string.httpsScheme))
                .authority(getResources().getString(R.string.instagramWebsite))
                .appendPath(username)
        ;
        return builder.build();
    }
    private Uri getTwitterUri(String username){
        //https://twitter.com/realdonaldtrump
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getResources().getString(R.string.httpsScheme))
                .authority(getResources().getString(R.string.twitterWebsite))
                .appendPath(username)
        ;
        return builder.build();
    }

    private void addToClipboard(String label, String text){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getBaseContext(), text + " " + getResources().getString(R.string.action_addedToClipboard), Toast.LENGTH_SHORT).show();
    }
    //----------------------------------------------------------------------------------------------
}