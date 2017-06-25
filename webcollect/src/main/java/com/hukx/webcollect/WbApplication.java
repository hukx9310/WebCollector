package com.hukx.webcollect;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Printer;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hkx on 17-6-10.
 */

public class WbApplication extends Application {


    public static Handler mMainHandler;
    public static ExecutorService universalExecutor;
    public static RequestQueue universalRequestueue;

    private static String SP_FILE_NAME = "preferences";
    private static String SP_LUNCH_TIMES = "lunchTime";

    @Override
    public void onCreate() {
        super.onCreate();

        mMainHandler = new Handler(this.getMainLooper());
        universalExecutor = Executors.newFixedThreadPool(1);
        universalRequestueue = Volley.newRequestQueue(this);

        Looper.getMainLooper().setMessageLogging(new Printer() {
            @Override
            public void println(String x) {
//                LogUtil.i(x);
            }
        });

    }

    public boolean isFirstTimeLaunch(){

        SharedPreferences sp = this.getSharedPreferences(SP_FILE_NAME, MODE_PRIVATE);
        if(sp.getInt(SP_LUNCH_TIMES, 0) == 0){
            SharedPreferences.Editor spEditor = sp.edit();
            spEditor.putInt(SP_LUNCH_TIMES, 1);
            spEditor.commit();
            return true;
        }
        return false;
    }

}
