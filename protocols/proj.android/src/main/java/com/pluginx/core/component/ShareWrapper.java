package com.pluginx.core.component;

import android.util.Log;

import com.google.gson.Gson;
import com.pluginx.core.Constants;
import com.pluginx.core.utils.NotificationCenter;
import com.pluginx.core.utils.ObserverListener;

public class ShareWrapper extends PluginWrapper {
    public enum ShareContentType {
        Link, Image, Video
    }

    protected static class ShareInfo {
        public int contentType;
        public String url;
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
        Log.d(Constants.TAG, "share message " + value);
    }

    protected void onShareSucceed(String postId) {
        onShareResult(PluginStatus.Success.ordinal(), new PluginShareResult(postId).toString());
    }

    protected void onShareFailed(PluginError pluginError) {
        onShareResult(PluginStatus.Failed.ordinal(), new PluginResult(pluginError).toString());
    }

    protected void onShareCanceled() {
        onShareResult(PluginStatus.Canceled.ordinal(), null);
    }

    private static class PluginShareResult extends PluginResult {
        protected String postId;

        public PluginShareResult(String postId) {
            this.postId = postId;
        }
    }

    public native void onShareResult(int code, String data);
}
