package com.pluginx.core.component;

import android.util.Log;

import com.pluginx.core.BuildConfig;
import com.google.gson.Gson;
import com.pluginx.core.Constants;
import com.pluginx.core.utils.NotificationCenter;
import com.pluginx.core.utils.ObserverListener;

public class ShareWrapper extends PluginWrapper {
    private final static String ON_SHARE_RESULT = "onShareResult";

    public enum ShareContentType {
        Link, Image, Video
    }

    public static class ShareInfo {
        public int contentType;
        public String url;
        public String filePath;
        public String tag;

        public static ShareInfo formJson(String json) {
            return new Gson().fromJson(json, ShareInfo.class);
        }
    }

    protected ObserverListener mObserverListener = (eventName, objects) -> {
        Log.d(Constants.TAG, "onMessage " + eventName);
        String sdkName = (String) objects[0];
        Log.d(Constants.TAG, "onMessage sdkName " + sdkName + " clsName " + getClass().getSimpleName());
        if (!getClass().getSimpleName().toLowerCase().contains(sdkName.toLowerCase())) return;
        if (Constants.SHARE.equals(eventName)) {
            final String value = (String) objects[1];
            share(value);
        }
    };

    @Override
    public void onLoad() {
        super.onLoad();
        NotificationCenter.getInstance().registerObserver(Constants.SHARE, mObserverListener, this);
    }

    public void share(String value) {

    }

    protected void onShareSucceed(String postId) {
        getParent().nativeCallScript(ON_SHARE_RESULT, PluginStatusCodes.Succeed.ordinal(), postId);
        if (BuildConfig.USED_NATIVE)
            onShareResult(PluginStatusCodes.Succeed.ordinal(), postId);
    }

    protected void onShareFailed(PluginError pluginError) {
        getParent().nativeCallScript(ON_SHARE_RESULT, PluginStatusCodes.Failed, pluginError);
        if (BuildConfig.USED_NATIVE)
            onShareResult(PluginStatusCodes.Failed.ordinal(), pluginError.toString());
    }

    protected void onShareCanceled() {
        getParent().nativeCallScript(ON_SHARE_RESULT, PluginStatusCodes.Canceled);
        if (BuildConfig.USED_NATIVE)
            onShareResult(PluginStatusCodes.Canceled.ordinal(), null);
    }

    public native void onShareResult(int code, String data);
}
