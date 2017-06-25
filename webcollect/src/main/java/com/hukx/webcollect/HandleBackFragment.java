package com.hukx.webcollect;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by hkx on 17-6-14.
 */

public abstract class HandleBackFragment extends Fragment {


     abstract public boolean onBackPressed();
    private boolean ifInterceptBackKey = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ifInterceptBackKey = false;
    }

    public boolean ifInterceptBack(){
        return ifInterceptBackKey;
    }
}
