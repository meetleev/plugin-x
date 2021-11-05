package com.game.core;

import com.game.core.utils.NotificationCenter;

public class ScriptCallJavaBridge {
    public static void showRewardedVideoAd(String sdkName) {
        NotificationCenter.getInstance().postNotification(Constants.SHOW_REWARD_VIDEO_AD, sdkName);
    }

    public static void showInterstitialAd(String sdkName) {
        NotificationCenter.getInstance().postNotification(Constants.SHOW_INTERSTITIAL_AD, sdkName);
    }

    public static void bannerAdVisible(String sdkName, boolean visible) {
        NotificationCenter.getInstance().postNotification(Constants.BANNER_AD_VISIBLE, sdkName, visible);
    }

    public static void floatAdVisible(String sdkName, boolean visible) {
        NotificationCenter.getInstance().postNotification(Constants.FLOAT_AD_VISIBLE, sdkName, visible);
    }

    public static void logEvent(String sdkName, String eventId, String value) {
        NotificationCenter.getInstance().postNotification(Constants.LOG_EVENT, sdkName, eventId, value);
    }

    public static void showToast(String msg) {
        NotificationCenter.getInstance().postNotification(Constants.SHOW_TOAST, msg);
    }
}
