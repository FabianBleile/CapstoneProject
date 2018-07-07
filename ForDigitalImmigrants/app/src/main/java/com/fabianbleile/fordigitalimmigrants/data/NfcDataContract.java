package com.fabianbleile.fordigitalimmigrants.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Fabian on 07.03.2018.
 */

public class NfcDataContract {

    public static final String CONTENT_AUTHORITY = "com.fabianbleile.fordigitalimmigrants";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NFCDATA = "nfcdata";


    public static final class NfcDataEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NFCDATA)
                .build();

        public static final String TABLE_NAME = "nfcdata";

        public static final String COLUMN_NFCDATA_FIRSTNAME = "nfcdata_firstname";

        public static final String COLUMN_NFCDATA_LASTNAME = "nfcdata_lastname";

        public static final String COLUMN_NFCDATA_PHONENUMBER = "nfcdata_phonenumber";

        public static final String COLUMN_NFCDATA_EMAIL = "nfcdata_email";

        public static final String COLUMN_NFCDATA_INSTAGRAM = "nfcdata_instagram";

        public static final String COLUMN_NFCDATA_FACEBOOK = "nfcdata_facebook";

        public static final String COLUMN_NFCDATA_SNAPCHAT = "nfcdata_snapchat";

        public static Uri buildNfcDataUri() {
            return CONTENT_URI.buildUpon()
                    .build();
        }
    }
}
