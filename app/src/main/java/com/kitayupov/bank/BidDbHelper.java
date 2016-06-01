package com.kitayupov.bank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.Locale;

public class BidDbHelper extends SQLiteOpenHelper {

    public static final String BID_DB = "bid.db";
    public static final String TABLE_NAME = "Bids";
    public static final int VERSION = 1;

    public BidDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public BidDbHelper(Context context) {
        super(context, BID_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BidTable.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format(Locale.ROOT, "drop table if exists %s", TABLE_NAME));
        onCreate(db);
    }

    public static class BidTable implements BaseColumns {
        public static final String CREATE_QUERY = String.format(Locale.ROOT,
                "create table %s (%s integer primary key autoincrement, %s text, %s text, %s numeric, %s text, %s text, %s numeric)",
                TABLE_NAME, _ID, Constants.DESCRIPTION, Constants.NAME, Constants.PHOTO, Constants.STATUS_A, Constants.STATUS_B, Constants.DATE
        );
    }
}
