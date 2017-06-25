package com.hukx.webcollect.utils;

/**
 * Created by hkx on 17-6-18.
 */

public class StringUtil {

    public static boolean isNotNull(String s){
        if(s == null || s.equals(""))
            return false;
        return true;
    }

}
