package com.pluginx.core.component;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.pluginx.core.Constants;
import com.pluginx.core.json.EnumTypeAdapter;
import com.pluginx.core.utils.NotificationCenter;
import com.pluginx.core.utils.ObserverListener;

public class AdsWrapper extends PluginWrapper {
    public enum AdState {
        None, Loading, Loaded, Watched, NotWatchComplete, Error,
    }

    public enum AdType {
        RewardedVideo, RewardedInterstitial, Interstitial, Banner,
    }

    protected final String UNIT_AD_EMPTY = "the unit ad was emptied";

    protected AdState rewardAdState = AdState.None;
    protected AdState interstitialAdState = AdState.None;
    protected AdState rewardInterstitialAdState = AdState.None;
    protected AdState bannerAdState = AdState.None;
    protected ObserverListener mObserverListener = (String eventName, Object... objects) -> {
        Log.d(Constants.TAG, "ads onMessage " + eventName);
        String sdkName = (String) objects[0];
        Log.d(Constants.TAG, "ads onMessage sdkName " + sdkName + " clsName " + getClass().getSimpleName());
        if (!getClass().getSimpleName().toLowerCase().contains(sdkName.toLowerCase())) return;
        switch (eventName) {
            case Constants.SHOW_REWARD_VIDEO_AD:
                Log.d(Constants.TAG, "showRewardedVideoAd");
                showRewardedVideoAd();
                break;
            case Constants.SHOW_REWARD_INTERSTITIAL_AD:
                Log.d(Constants.TAG, "showRewardedInterstitialAd");
                showRewardedInterstitialAd();
                break;
            case Constants.SHOW_INTERSTITIAL_AD:
                Log.d(Constants.TAG, "showInterstitialAd");
                showInterstitialAd();
                break;
            case Constants.BANNER_AD_VISIBLE: {
                boolean visible = (boolean) objects[1];
                Log.d(Constants.TAG, "bannerAd visible " + visible);
                if (visible) showBannerAd();
                else hideBannerAd();
                break;
            }
            case Constants.FLOAT_AD_VISIBLE: {
                boolean visible = (boolean) objects[1];
                Log.d(Constants.TAG, "floatAd visible " + visible);
                if (visible) showFloatAd();
                else hideFloatAd();
                break;
            }
        }
    };

    @Override
    public void onLoad() {
        super.onLoad();
        NotificationCenter.getInstance().registerObserver(Constants.SHOW_REWARD_VIDEO_AD, this.mObserverListener, this);
        NotificationCenter.getInstance().registerObserver(Constants.SHOW_INTERSTITIAL_AD, this.mObserverListener, this);
        NotificationCenter.getInstance().registerObserver(Constants.SHOW_REWARD_INTERSTITIAL_AD, this.mObserverListener, this);
        NotificationCenter.getInstance().registerObserver(Constants.FLOAT_AD_VISIBLE, this.mObserverListener, this);
        NotificationCenter.getInstance().registerObserver(Constants.BANNER_AD_VISIBLE, this.mObserverListener, this);
    }

    protected void preloadRewardedAd() {
    }

    public void showRewardedVideoAd() {
        Log.d(Constants.TAG, "showRewardedVideoAd");
    }

    protected void loadBannerAd(boolean autoShow) {
    }

    public void showBannerAd() {
        Log.d(Constants.TAG, "showBannerAd");
    }

    public void hideBannerAd() {
        Log.d(Constants.TAG, "hideBannerAd");
    }

    protected void preloadRewardedInterstitialAd() {
    }

    public void showRewardedInterstitialAd() {
        Log.d(Constants.TAG, "showRewardedInterstitialAd");
    }

    protected void preloadInterstitialAd() {
    }

    public void showInterstitialAd() {
        Log.d(Constants.TAG, "showInterstitialAd");
    }

    public void showFloatAd() {
        Log.d(Constants.TAG, "showFloatAd");
    }

    public void hideFloatAd() {
        Log.d(Constants.TAG, "hideFloatAd");
    }

    protected void onShowAdSuccess(AdType adType) {
        PluginAdResult result = new PluginAdResult(adType);
        onShowAdResult(PluginStatus.Success.ordinal(), result.toString());
    }

    protected void onShowAdFailed(AdType adType, PluginError pluginError) {
        PluginAdResult result = new PluginAdResult(adType, pluginError);
        onShowAdResult(PluginStatus.Failed.ordinal(), result.toString());
    }

    private static class PluginAdResult extends PluginResult {
        protected AdType adType;

        public PluginAdResult(AdType adType) {
            this.adType = adType;
        }

        public PluginAdResult(AdType adType, PluginError error) {
            super(error);
            this.adType = adType;
        }

        @NonNull
        @Override
        public String toString() {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(AdType.class, new EnumTypeAdapter<>(AdType.class));
            return gsonBuilder.create().toJson(this);
        }
    }

    public native void onShowAdResult(int code, String data);
}
