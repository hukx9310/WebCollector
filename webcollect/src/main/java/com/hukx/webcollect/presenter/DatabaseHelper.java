package com.hukx.webcollect.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hkx on 17-3-18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    String dbName;
    SQLiteDatabase db;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        dbName = name;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL("create table " + WebsPresenter.TABLE_FAVORITES + "(_id integer primary key autoincrement, url text, title text, note text, icon text," +
                "  isFolder integer, updateTime integer(4), connect integet)");//connct = 0 for connecting, = 1 for success, = 2 for fail
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
