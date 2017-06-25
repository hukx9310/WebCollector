package com.hukx.webcollect.presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hukx.webcollect.ViewShower;
import com.hukx.webcollect.WbApplication;
import com.hukx.webcollect.utils.StringUtil;
import com.hukx.webcollect.utils.UrlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hkx on 17-3-18.
 */

public class WebsPresenter {

    List<WebRecord> mData;

    ViewShower mShower;
    Context mContext;
    DatabaseHelper mDBHelper;
    final static String TABLE_FAVORITES = "favorite_webs";
    final String DATABASE_NAME = "webs";

    public WebsPresenter(ViewShower shower,Context context){
        this.mShower = shower;
        this.mContext = context;
        mDBHelper = new DatabaseHelper(mContext , DATABASE_NAME , null , 1);
    }

    public void loadData(){

        mShower.showLoading();
        WbApplication.universalExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mData = fetchFactoryWebs();
                WbApplication.mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mShower.hideLoading();
                        mShower.loadContent(mData);
                    }
                });
            }
        });
    }

    public List<WebRecord> fetchFactoryWebs() {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        Cursor c = database.query(TABLE_FAVORITES, null, null, null, null, null, "updateTime desc", null);
        List<WebRecord>  result = new ArrayList<WebRecord>();
        while (c.moveToNext()){
            String url = c.getString(1);
            if(url == null || url.equals("")){
                continue;
            }
            String title = c.getString(2);
            String note = c.getString(3);
            String icon = c.getString(4);
            long updateTime = c.getLong(6);
            int connectState = c.getInt(7);
            WebRecord newItem = new WebRecord(url, title, note, icon, updateTime, connectState);
            if(connectState == WebRecord.STATE_CONNECT_UNDETERMINED){
                invalidTileAndIcon(newItem);
            }
            result.add(newItem);
        }
        c.close();
        database.close();

        return result;
    }

    public void add(String url, String note, long updateTime){

        final WebRecord newItem = new WebRecord(url, null, note, null, updateTime, WebRecord.STATE_CONNECTING);
        newItem.setUrlComplete(false);

        ContentValues cv = new ContentValues();
        cv.put("url", url);
        cv.put("note", note);
        cv.put("updateTime", updateTime);
        cv.put("updateTime", updateTime);
        cv.put("connect", WebRecord.STATE_CONNECTING);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.insert(TABLE_FAVORITES, null, cv);
        db.close();


        mData.add(0 ,newItem);
        mShower.onInsertItem(0);

        invalidTileAndIcon(newItem);
    }


    public void delete(WebRecord item){

        long updateTime = item.getUpdateTime();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int delRow = db.delete(TABLE_FAVORITES, "updateTime = ?", new String[]{String.valueOf(updateTime)});
        int itemIndex = mData.indexOf(item);
        mData.remove(item);
        mShower.onDeleteItem(itemIndex);
    }

    public void updateOnlyNote(WebRecord item) {

        long lastUpdateTime = item.getUpdateTime();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("note", item.getNote());
        long currTime = System.currentTimeMillis();
        item.setUpdateTime(currTime);
        values.put("updateTime", currTime);

        db.update(TABLE_FAVORITES, values, "updateTime = ?", new String[]{String.valueOf(lastUpdateTime)});
        int originIndex = mData.indexOf(item);

        mData.remove(item);
        mData.add(0, item);

        mShower.notifyItemRangeChangedFromTo(0, originIndex + 1);
    }

    public void update(WebRecord item) {

        long lastUpdateTime = item.getUpdateTime();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("url", item.getURL());
        values.put("note", item.getNote());
        long currTime = System.currentTimeMillis();
        item.setUpdateTime(currTime);
        values.put("updateTime", currTime);

        int updateRow = db.update(TABLE_FAVORITES, values, "updateTime = ?", new String[]{String.valueOf(lastUpdateTime)});

        int originIndex = mData.indexOf(item);
        mData.remove(item);
        mData.add(0, item);
        mShower.notifyItemRangeChangedFromTo(0, originIndex + 1);

        invalidTileAndIcon(item);
    }

    private void invalidTileAndIcon(final WebRecord item){

        WbApplication.universalExecutor.submit(new Runnable() {

            @Override
            public void run() {

                SQLiteDatabase db;

                String targetUrl = item.getURL();
                String autoCompleteUrl = UrlUtil.autoCompleteUrl(targetUrl);
                ContentValues cv;
                if(StringUtil.isNotNull(autoCompleteUrl)){
                    targetUrl = autoCompleteUrl;
                    item.setURL(targetUrl);
                    item.setUrlComplete(true);
                } else {
                    if(UrlUtil.isInernetAvailable()) {
                        item.setUrlComplete(true);
                        item.setConnectState(WebRecord.STATE_CONNECT_FAILED);
                        cv = new ContentValues();
                        cv.put("connect", WebRecord.STATE_CONNECT_FAILED);
                        db = mDBHelper.getWritableDatabase();
                        db.update(TABLE_FAVORITES, cv, "updateTime = ?", new String[]{String.valueOf(item.getUpdateTime())});
                        db.close();
                        return;
                    } else {
                        item.setUrlComplete(true);
                        item.setConnectState(WebRecord.STATE_CONNECT_UNDETERMINED);
                        cv = new ContentValues();
                        cv.put("connect", WebRecord.STATE_CONNECT_UNDETERMINED);
                        db = mDBHelper.getWritableDatabase();
                        db.update(TABLE_FAVORITES, cv, "updateTime = ?", new String[]{String.valueOf(item.getUpdateTime())});
                        db.close();
                        return;
                    }
                }

                item.setConnectState(WebRecord.STATE_CONNECTED);
                String header = UrlUtil.getHead(targetUrl);
                if(header == null || header.equals("")){
                    return ;
                }
                String title = UrlUtil.getTitleFromHeader(header);
                String iconUrl = UrlUtil.getIconUrlString(targetUrl, header);
                cv = new ContentValues();
                cv.put("url", targetUrl);
                if(StringUtil.isNotNull(title)) {
                    cv.put("title", title);
                }
                if(StringUtil.isNotNull(iconUrl)){
                    cv.put("icon", iconUrl);
                }
                cv.put("connect", WebRecord.STATE_CONNECTED);
                db = mDBHelper.getWritableDatabase();
                db.update(TABLE_FAVORITES, cv, "updateTime = ?", new String[]{String.valueOf(item.getUpdateTime())});
                if(StringUtil.isNotNull(title)) {
                    item.setTitle(title);
                }
                if (StringUtil.isNotNull(iconUrl)) {
                    item.setIconUrl(iconUrl);
                }
                db.close();
            }
        });

    }
}
