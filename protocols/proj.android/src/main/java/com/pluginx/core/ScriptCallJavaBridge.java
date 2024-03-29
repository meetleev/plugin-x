package com.pluginx.core;


import com.pluginx.core.utils.NotificationCenter;

public class ScriptCallJavaBridge {
    public static void showRewardedVideoAd(String sdkName) {
        NotificationCenter.getInstance().postNotification(Constants.SHOW_REWARD_VIDEO_AD, sdkName);
    }

    public static void showRewardedInterstitialAd(String sdkName) {
        NotificationCenter.getInstance().postNotification(Constants.SHOW_REWARD_INTERSTITIAL_AD, sdkName);
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

    public static void paymentWithProductId(String sdkName, String productId) {
        ScriptCallJavaBridge.paymentWithProductId(sdkName, productId, true);
    }

    public static void logIn(String sdkName) {
        NotificationCenter.getInstance().postNotification(Constants.LOG_IN, sdkName);
    }

    public static void logOut(String sdkName) {
        NotificationCenter.getInstance().postNotification(Constants.LOG_OUT, sdkName);
    }

    public static void share(String sdkName, String content) {
        NotificationCenter.getInstance().postNotification(Constants.SHARE, sdkName, content);
    }

    public static void paymentWithProductId(String sdkName, String productId, boolean bAutoConsume) {
        NotificationCenter.getInstance().postNotification(Constants.PAYMENT_PRODUCT, sdkName, productId, bAutoConsume);
    }
}
