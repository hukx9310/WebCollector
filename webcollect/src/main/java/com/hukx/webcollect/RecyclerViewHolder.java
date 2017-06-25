package com.hukx.webcollect;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hukx.webcollect.presenter.WebRecord;

/**
 * Created by hkx on 17-6-20.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public View itemView;
    public View iconTitleLayout;
    public View contentPanel;
    public ImageView iconView = null;
    public TextView noteView = null;
    public TextView titleView = null;
    public TextView timeView = null;
    public WebRecord showInfo = null;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        contentPanel = itemView.findViewById(R.id.contentPanel);
        iconTitleLayout = itemView.findViewById(R.id.iconAndTitle);
        iconView = (ImageView) itemView.findViewById(R.id.icon_img);
        noteView = (TextView) itemView.findViewById(R.id.note);
        titleView = (TextView) itemView.findViewById(R.id.title);
        timeView = (TextView) itemView.findViewById(R.id.time);
    }


}
