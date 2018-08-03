package com.fabianbleile.fordigitalimmigrants;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.fabianbleile.fordigitalimmigrants.data.AppDatabase;
import com.fabianbleile.fordigitalimmigrants.data.Contact;

import java.util.Collections;
import java.util.List;

public class RemoteViewsService extends android.widget.RemoteViewsService {
    public RemoteViewsService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyRemoteViewsFactory(this.getApplicationContext(), intent) {
        };
    }
}

class MyRemoteViewsFactory implements android.widget.RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Contact mContact;
    private List<Contact> mContactList = null;

    public MyRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        AppDatabase database = AppDatabase.getDatabase(mContext);
        mContactList = database.contactDao().getAllForWidget();
        if(mContactList != null){
            Collections.reverse(mContactList);
            mContact = mContactList.get(0);
        }

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mContactList == null ? 0 : 10;
    }

    @Override
    public RemoteViews getViewAt(int pos) {

        RemoteViews rv = new RemoteViews(
                mContext.getPackageName(),
                R.layout.contact_list_widget_item)
                ;

        String info = contactInfo(pos);
        if(!TextUtils.isEmpty(info)){
            rv.setImageViewResource(R.id.iv_image, id(pos));
            rv.setTextViewText(R.id.tv_textView, info);
        } else {
            rv.setViewVisibility(R.id.linearLayout, View.GONE);
        }

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private String contactInfo(int pos){
        String[] contactInfoList = new String[]{
                mContact.getName(),
                mContact.getPhonenumber(),
                mContact.getEmail(),
                mContact.getBirthday(),
                mContact.getHometown(),
                mContact.getInstagram(),
                mContact.getFacebook(),
                mContact.getSnapchat(),
                mContact.getTwitter(),
                mContact.getLocation()
        };
        return contactInfoList[pos];
    }

    private int id(int pos){
        int[] idDrawable = new int[]{
                R.drawable.ic_contacts_24dp,
                R.drawable.ic_call_24dp,
                R.drawable.ic_email_24dp,
                R.drawable.ic_cake_24dp,
                R.drawable.ic_location_city_24dp,
                R.drawable.ic_instagram,
                R.drawable.ic_facebook,
                R.drawable.ic_snapchat,
                R.drawable.ic_twitter,
                R.drawable.ic_location_24dp
        };
        return idDrawable[pos];
    }
}