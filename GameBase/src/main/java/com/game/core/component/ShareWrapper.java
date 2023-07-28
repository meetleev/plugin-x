package com.game.core.component;

import android.util.Log;

import com.game.core.Constants;
import com.game.core.utils.NotificationCenter;
import com.game.core.utils.ObserverListener;
import com.google.gson.Gson;

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
            final String value = (String)objects[1];
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
    }

    protected void onShareFailed(PluginError pluginError) {
        getParent().nativeCallScript(ON_SHARE_RESULT, PluginStatusCodes.Failed, pluginError);
    }

    protected void onShareCanceled() {
        getParent().nativeCallScript(ON_SHARE_RESULT, PluginStatusCodes.Canceled);
    }
}
