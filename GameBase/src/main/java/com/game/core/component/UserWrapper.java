package com.game.core.component;

import android.util.Log;

import androidx.annotation.NonNull;

import com.game.core.Constants;
import com.game.core.utils.NotificationCenter;
import com.game.core.utils.ObserverListener;
import com.google.gson.Gson;

public class UserWrapper extends PluginWrapper {
    private final static String ON_LOGIN_RESULT = "onLoginResult";

    public static class UserInfo {
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

        public UserInfo() {
        }

        public UserInfo(String id, String gamerTag, String iconImageUrl) {
            this.id = id;
            this.gamerTag = gamerTag;
            this.iconImageUrl = iconImageUrl;
        }

        @NonNull
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    public void logIn() {
    }

    public void logOut() {
    }

    protected void onLoginSucceed(UserInfo userInfo) {
        getParent().nativeCallScript(ON_LOGIN_RESULT, PluginStatusCodes.Succeed.ordinal(), userInfo);
    }

    protected void onLoginFailed(PluginError pluginError) {
        getParent().nativeCallScript(ON_LOGIN_RESULT, PluginStatusCodes.Failed, pluginError);
    }

    protected void onLoginCanceled() {
        getParent().nativeCallScript(ON_LOGIN_RESULT, PluginStatusCodes.Canceled);
    }

    protected ObserverListener mObserverListener = (eventName, objects) -> {
        Log.d(Constants.TAG, "onMessage " + eventName);
        String sdkName = (String) objects[0];
        Log.d(Constants.TAG, "onMessage sdkName " + sdkName + " clsName " + getClass().getSimpleName());
        if (!getClass().getSimpleName().toLowerCase().contains(sdkName.toLowerCase())) return;
        if (Constants.LOG_IN.equals(eventName)) {
            logIn();
        } else if (Constants.LOG_OUT.equals(eventName)) {
            logOut();
        }
    };

    @Override
    public void onLoad() {
        super.onLoad();
        NotificationCenter.getInstance().registerObserver(Constants.LOG_IN, mObserverListener, this);
        NotificationCenter.getInstance().registerObserver(Constants.LOG_OUT, mObserverListener, this);
    }
}
