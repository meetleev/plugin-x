package com.game.core.component;


import android.util.Log;

import com.game.core.Constants;
import com.game.core.utils.NotificationCenter;
import com.game.core.utils.ObserverListener;

public class AdWrapper extends Component {
    public enum AdState {
        None,
        Loading,
        Loaded,
        Watched,
        Error,
    }

    public enum AdType {
        RewardedVideo,
        Interstitial,
        Banner,
    }

    protected final String ON_SHOW_REWARD_VIDEO_AD_RESULT = "onShowRewardVideoAdResult";
    protected final String SPLASH_PLAY_COMPLETE = "SPLASH_PLAY_COMPLETE";

    protected AdState rewardAdState = AdState.None;
    protected AdState interstitialAdState = AdState.None;
    protected ObserverListener mObserverListener = new ObserverListener() {
        @Override
        public void onMessage(Object target, String eventName, Object... objects) {
            Log.d(Constants.TAG, "onMessage " + eventName);
            if (target.getClass().getSuperclass().equals(AdWrapper.class)) {
                String sdkName = (String) objects[0];
                Log.d(Constants.TAG, "onMessage sdkName " + sdkName + " clsName " + target.getClass().getSimpleName());
                if (!target.getClass().getSimpleName().toLowerCase().contains(sdkName.toLowerCase()))
                    return;
                switch (eventName) {
                    case Constants.SHOW_REWARD_VIDEO_AD:
                        Log.d(Constants.TAG, "showRewardedVideoAd");
                        showRewardedVideoAd();
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
            }
        }
    };

    @Override
    public void onLoad() {
        super.onLoad();
        NotificationCenter.getInstance().registerObserver(Constants.SHOW_REWARD_VIDEO_AD, this.mObserverListener, this);
        NotificationCenter.getInstance().registerObserver(Constants.SHOW_INTERSTITIAL_AD, this.mObserverListener, this);
        NotificationCenter.getInstance().registerObserver(Constants.FLOAT_AD_VISIBLE, this.mObserverListener, this);
        NotificationCenter.getInstance().registerObserver(Constants.BANNER_AD_VISIBLE, this.mObserverListener, this);
        this.initSDK();
    }

    protected void initSDK() {
    }

    public void exitSDK() {
    }

    public void preloadRewardedAd() {
    }

    public void showRewardedVideoAd() {
    }

    public void showBannerAd() {
    }

    public void hideBannerAd() {
    }

    public void preloadInterstitialAd() {
    }

    public void showInterstitialAd() {
    }

    public void showFloatAd() {
    }

    public void hideFloatAd() {
    }

}
