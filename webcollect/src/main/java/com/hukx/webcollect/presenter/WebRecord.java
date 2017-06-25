package com.hukx.webcollect.presenter;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.hukx.webcollect.RecyclerViewHolder;
import com.hukx.webcollect.WbApplication;

import java.io.Serializable;

/**
 * Created by hkx on 17-3-18.
 */

public class WebRecord implements Serializable{

    public static int STATE_CONNECTING = 0;
    public static int STATE_CONNECTED = 1;
    public static int STATE_CONNECT_FAILED = 2;
    public static int STATE_CONNECT_UNDETERMINED = 3;

    private String URL;
    private String title;
    private String note;
    private long updateTime;
    private String iconUrl;

    private boolean isUrlComplete = true;
    private int connectState;

    private RecyclerViewHolder viewHolder;

    private EditText currEditText;

    public  WebRecord(String url, String title, String note, String iconUrl, long updateTime, int connectState){

        this.updateTime = updateTime;
        this.title = title;
        this.URL = url;
        this.note = note;
        this.iconUrl = iconUrl;
        this.connectState = connectState;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setTitle(final String newTitle) {
        if(newTitle == null){
            return;
        }
        this.title = newTitle;
        if(viewHolder == null) {
            return;
        }
        final TextView titleTv = viewHolder.titleView;
        if(titleTv == null){
            return ;
        }
        WbApplication.mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                titleTv.setText(newTitle);
            }
        });
    }

    public void setNote(final String note) {
        if(this.note != null && this.note.equals(note)){
            return ;
        }
        this.note = note;
        if(viewHolder == null) {
            return;
        }
        final TextView noteTv = viewHolder.noteView;
        if(noteTv == null){
            return ;
        }
        WbApplication.mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                noteTv.setText(note);
            }
        });
    }

    public void setIconUrl(final String newIconUrl) {
        if(newIconUrl == null){
            return;
        }
        this.iconUrl = newIconUrl;
        if(viewHolder == null) {
            return;
        }
        final  ImageView iconView = viewHolder.iconView;
        if(iconView == null){
            return ;
        }
        iconView.setTag(iconUrl);
        ImageRequest imageRequest = new ImageRequest(iconUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        if(iconView.getTag() != null && iconView.getTag().equals(iconUrl)) {
                            iconView.setImageBitmap(response);
                        } else {
                        }
                    }
                },
                0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        WbApplication.universalRequestueue.add(imageRequest);
    }

    public void setViewHolder(RecyclerViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public void setUpdateTime(long createTime) {
        this.updateTime = createTime;
        if(viewHolder == null)
            return;
        viewHolder.timeView.setText(String.valueOf(this.updateTime));
    }

    public void setConnectState(int connectState) {
        this.connectState = connectState;
        if(viewHolder == null)
            return;
        ImageView iconView = viewHolder.iconView;
        if(iconView.getTag() != null && iconView.getTag().equals(iconUrl)) {
            switch (connectState) {
                case 0:
                    iconView.setImageBitmap(null);
                    return;
                case 1:
                    return;
                case 2:
                    return;
            }
        } else {
        }
    }
    public void setUrlComplete(boolean urlComplete) {
        isUrlComplete = urlComplete;
        Log.i("123","currEditText = " + currEditText);
        if(currEditText != null) {
            WbApplication.mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    currEditText.setText(URL);
                    currEditText.setEnabled(isUrlComplete);
                    currEditText.invalidate();
                }
            });
        }
    }

    public EditText getCurrEditText() {
        return currEditText;
    }

    public void setCurrEditText(EditText currEditText) {
        this.currEditText = currEditText;
    }

    public boolean isUrlComplete() {
        return isUrlComplete;
    }


    public int getConnectState() {
        return connectState;
    }

    public String getURL() {
        return URL;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public String getIcon() {
        return iconUrl;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public RecyclerViewHolder getViewHolder() {
        return viewHolder;
    }
}
