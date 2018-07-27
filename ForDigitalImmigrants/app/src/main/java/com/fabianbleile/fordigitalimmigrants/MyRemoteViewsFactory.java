package com.fabianbleile.fordigitalimmigrants;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Database;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.fabianbleile.fordigitalimmigrants.data.AppDatabase;
import com.fabianbleile.fordigitalimmigrants.data.Contact;

import java.util.List;

class MyRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    static LiveData<List<Contact>> contactsLiveData;
    static List<Contact> contacts;
    private static int lastContactId;

    public MyRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        lastContactId = intent.getIntExtra(LastContactWidget.LAST_CONTACT_ID, -1);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        AppDatabase database = AppDatabase.getDatabase(mContext);
        contactsLiveData = database.contactDao().getAll();
        contacts = (List<Contact>) contactsLiveData;
        /*
        int[] contactIdList = new int[1];
        contactIdList[0] = lastContactId;
        contactIdList[0] = 7;
        contacts = database.contactDao().loadAllByIds(contactIdList);
         */
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return contacts == null ? 0 : contacts.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        Contact contact = contacts.get(i);
        RemoteViews rv = new RemoteViews(
                mContext.getPackageName(),
                R.layout.contact_list_widget_item)
                ;
        rv.setTextViewText(R.id.name, contact.getName());
        rv.setTextViewText(R.id.number, contact.getPhonenumber());
        rv.setTextViewText(R.id.email, contact.getEmail());
        rv.setTextViewText(R.id.birthday, contact.getBirthday());
        rv.setTextViewText(R.id.hometown, contact.getHometown());
        rv.setTextViewText(R.id.instagram, contact.getInstagram());
        rv.setTextViewText(R.id.facebook, contact.getFacebook());
        rv.setTextViewText(R.id.snapchat, contact.getSnapchat());
        rv.setTextViewText(R.id.twitter, contact.getTwitter());
        rv.setTextViewText(R.id.location, contact.getLocation());
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
}
