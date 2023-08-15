package com.pluginx.core.component;

import android.util.Log;

import com.pluginx.core.BuildConfig;
import com.pluginx.core.Constants;
import com.pluginx.core.utils.NotificationCenter;
import com.pluginx.core.utils.ObserverListener;


public class AdsWrapper extends PluginWrapper {
    public enum AdState {
        None, Loading, Loaded, Watched, NotWatchComplete, Error,
    }

    public enum AdType {
        RewardedVideo, RewardedInterstitial, Interstitial, Banner,
    }

    protected final String ON_SHOW_AD_RESULT = "onShowAdResult";
    protected final String UNIT_AD_EMPTY = "the unit ad was emptied";
    protected final String SPLASH_PLAY_COMPLETE = "SPLASH_PLAY_COMPLETE";

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
    }

    protected void loadBannerAd(boolean autoShow) {
    }

    public void showBannerAd() {
    }

    public void hideBannerAd() {
    }

    protected void preloadRewardedInterstitialAd() {
    }

    public void showRewardedInterstitialAd() {
        onShowAdFailed(AdType.RewardedInterstitial, new PluginError(AdState.Error.ordinal(), UNIT_AD_EMPTY));
    }

    protected void preloadInterstitialAd() {
    }

    public void showInterstitialAd() {
    }

    public void showFloatAd() {
    }

    public void hideFloatAd() {
    }

    protected void onShowAdSuccess(AdType adType) {
        getParent().nativeCallScript(ON_SHOW_AD_RESULT, adType, PluginStatusCodes.Succeed);
        if (BuildConfig.USED_NATIVE)
            onShowAdResult(adType.ordinal(), PluginStatusCodes.Succeed.ordinal(), null);
    }

    protected void onShowAdFailed(AdType adType, PluginError pluginError) {
        getParent().nativeCallScript(ON_SHOW_AD_RESULT, adType, PluginStatusCodes.Failed, pluginError.toString());
        if (BuildConfig.USED_NATIVE)
            onShowAdResult(adType.ordinal(), PluginStatusCodes.Failed.ordinal(), pluginError.toString());
    }

    public native void onShowAdResult(int adType, int code, String data);
}
