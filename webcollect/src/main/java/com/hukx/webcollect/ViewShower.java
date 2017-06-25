package com.hukx.webcollect;

import com.hukx.webcollect.presenter.WebRecord;

import java.util.List;

/**
 * Created by hkx on 17-3-18.
 */

public interface ViewShower {
    void showLoading();
    void hideLoading();
    void loadContent(List<WebRecord> result);
    void notifyItemRangeChangedFromTo(int dataIndex, int count);
    void onInsertItem(int dataIndex);
    void onDeleteItem(int dataIndex);
}
