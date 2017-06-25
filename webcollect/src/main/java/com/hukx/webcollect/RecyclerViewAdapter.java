package com.hukx.webcollect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.hukx.webcollect.presenter.WebRecord;
import com.hukx.webcollect.utils.DensityUti;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by hkx on 17-6-20.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    public static int TYPE_EMPTY_HEADER = 0;
    public static int TYPE_ITEM_RECORD = 1;

    List<WebRecord> data;
    LayoutInflater inflater;
    Context mContext;

    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;

    public RecyclerViewAdapter(List<WebRecord> data, Context context) {
        this.data = data;
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM_RECORD;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.collect_item, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final WebRecord item = data.get(position);
        holder.showInfo = item;
        item.setViewHolder(holder);

        String title = item.getTitle();
        if (title == null || title.equals("")) {
            holder.titleView.setText(item.getURL());
        } else {
            holder.titleView.setText(title);
        }

        long updateTime = item.getUpdateTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(updateTime);
        String timeString = format.format(date);
        holder.timeView.setText(timeString);

        String note = item.getNote();
        if (note == null || note.equals("")) {
            holder.noteView.setVisibility(View.GONE);
        } else {
            holder.noteView.setVisibility(View.VISIBLE);
            holder.noteView.setText(item.getNote());
        }
        final String iconUrl = item.getIcon();
        final ImageView iconView = holder.iconView;
        iconView.setImageResource(R.mipmap.fail);
        if (iconUrl != null && !iconUrl.equals("")) {
            iconView.setTag(iconUrl);
            ImageRequest imageRequest = new ImageRequest(iconUrl,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            if (iconView.getTag() != null && iconView.getTag().equals(iconUrl)) {
                                iconView.setImageBitmap(response);
                            } else {
                            }
                        }
                    },
                    0, 0, ImageView.ScaleType.CENTER_INSIDE, Bitmap.Config.RGB_565,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            iconView.setImageResource(R.mipmap.fail);
                        }
                    });
            WbApplication.universalRequestueue.add(imageRequest);
        }

        if (position == data.size()) {

            holder.itemView.setPadding(0, 0, 0, DensityUti.dip2px(mContext, 80));

        } else {

            holder.itemView.setPadding(0, 0, 0, 0);

        }

        holder.iconTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("url", item.getURL());
                mContext.startActivity(intent);
            }
        });

        holder.contentPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mItemClickListener) {
                    return;
                } else {
                    mItemClickListener.onItemClick(item);
                }
            }
        });
        holder.contentPanel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null == mItemLongClickListener) {
                    return false;
                } else {
                    return mItemLongClickListener.onItemLongClick(v, item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    public static interface OnItemClickListener{
        public void onItemClick(WebRecord item);
    }

    public static interface OnItemLongClickListener{
        public boolean onItemLongClick(View view, WebRecord item);
    }
}
