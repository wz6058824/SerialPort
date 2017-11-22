package com.shangtongyin.tools.serialport;

import android.util.Log;

/**
 * Created by zf on 2016/7/19.
 */
public class L {
    public static final boolean DEBUG = true;

    public static void d(String tag, String v){
        if(DEBUG) Log.d(tag,v);
    }

    public static void e(String tag, String v){
        if(DEBUG) Log.e(tag,v);
    }
    public static void i(String tag, String v){
        if(DEBUG) Log.i(tag,v);
    }
    public static void w(String tag, String v){
        if(DEBUG) Log.w(tag,v);
    }
}
