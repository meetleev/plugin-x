package com.pluginx.core.component;

import android.util.Log;

import com.pluginx.core.Constants;
import com.pluginx.core.utils.NotificationCenter;
import com.pluginx.core.utils.ObserverListener;

public class UserWrapper extends PluginWrapper {

    public static class PluginUserInfo extends PluginResult {
        String id;
        String gamerTag;
        String iconImageUrl;
        String email;

        public void setId(String id) {
            this.id = id;
        }

        public void setGamerTag(String gamerTag) {
            this.gamerTag = gamerTag;
        }

        public void setIconImageUrl(String iconImageUrl) {
            this.iconImageUrl = iconImageUrl;
        }

        public void setEmail(String email) {
            this.email = email;
        }


        public PluginUserInfo(String id, String gamerTag, String iconImageUrl) {
            this.id = id;
            this.gamerTag = gamerTag;
            this.iconImageUrl = iconImageUrl;
        }

    }

    protected ObserverListener mObserverListener = (eventName, objects) -> {
        Log.d(Constants.TAG, "onMessage " + eventName);
        String sdkName = (String) objects[0];
        Log.d(Constants.TAG, "onMessage sdkName " + sdkName + " clsName " + getClass().getSimpleName());
        if (!getClass().getSimpleName().toLowerCase().contains(sdkName.toLowerCase())) return;
        if (Constants.SIGN_IN.equals(eventName)) {
            signIn();
        } else if (Constants.SIGN_OUT.equals(eventName)) {
            signOut();
        }
    };

    @Override
    public void onLoad() {
        super.onLoad();
        NotificationCenter.getInstance().registerObserver(Constants.SIGN_IN, mObserverListener, this);
        NotificationCenter.getInstance().registerObserver(Constants.SIGN_OUT, mObserverListener, this);
    }

    public void signIn() {
        Log.d(Constants.TAG, "signIn");
    }

    public void signOut() {
        Log.d(Constants.TAG, "signOut");
    }

    protected void onLoginSucceed(PluginUserInfo userInfo) {
        onLoginResult(PluginStatus.Success.ordinal(), userInfo.toString());
    }

    protected void onLoginFailed(PluginError pluginError) {
        onLoginResult(PluginStatus.Failed.ordinal(), new PluginResult(pluginError).toString());
    }

    protected void onLoginCanceled() {
        onLoginResult(PluginStatus.Canceled.ordinal(), null);
    }

    public native void onLoginResult(int code, String data);
}
