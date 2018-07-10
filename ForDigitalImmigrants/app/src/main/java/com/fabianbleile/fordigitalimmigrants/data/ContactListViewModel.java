package com.fabianbleile.fordigitalimmigrants.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fabianbleile.fordigitalimmigrants.MainActivity;

import java.util.List;

public class ContactListViewModel extends AndroidViewModel {

    private final LiveData<List<Contact>> contactList;

    private AppDatabase appDatabase;


    public ContactListViewModel(@NonNull Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

        contactList = appDatabase.contactDao().getAll();
    }

    public LiveData<List<Contact>> getContactList(){
        return contactList;
    }

    public void addItem(Contact contact){
        new insertAsyncTask(appDatabase).execute(contact);
    }

    private static class insertAsyncTask extends AsyncTask<Contact, Void, Void>{
        private AppDatabase db;

        insertAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Contact... params) {
            Log.e(MainActivity.mTagHandmade, ""+db);
            db.contactDao().insertContact(params[0]);
            return null;
        }
    }

    public void deleteItem(Contact contact){
        new deleteAsyncTask(appDatabase).execute(contact);
    }

    private static class deleteAsyncTask extends AsyncTask<Contact, Void, Void>{
        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Contact... params) {
            db.contactDao().delete(params[0]);
            return null;
        }
    }
}