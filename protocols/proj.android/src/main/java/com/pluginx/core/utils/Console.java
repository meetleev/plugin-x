package com.pluginx.core.utils;


import android.util.Log;

import com.pluginx.core.Constants;

public final class Console {

    private static final String DEFAULT_TAG = Constants.TAG;

    public static void log(String msg) {
        Log.i(DEFAULT_TAG, msg);
    }

    public static void log(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void warn(String msg) {
        Log.w(DEFAULT_TAG, msg);
    }

    public static void error(String msg) {
        Log.e(DEFAULT_TAG, msg);
    }

    public static void debug(String msg) {
        Log.d(DEFAULT_TAG, msg);
    }

    public static void json(String json) {
        try {
            String pretty = new org.json.JSONObject(json).toString(2);
            Log.i(DEFAULT_TAG, "\n" + pretty);
        } catch (Exception e) {
            Log.e(DEFAULT_TAG, json);
        }
    }
}
