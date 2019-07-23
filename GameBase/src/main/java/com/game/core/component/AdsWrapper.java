package com.game.core.component;

public class AdsWrapper extends Component {
    protected final String VIDEO_PLAY_COMPLETE = "VIDEO_PLAY_COMPLETE";
    protected final String SPLASH_PLAY_COMPLETE = "SPLASH_PLAY_COMPLETE";
    @Override
    public void onLoad() {
        super.onLoad();
        this.initSDK();
    }

    protected void initSDK() {
    }

    public void exitSDK() {
    }

    public void showRewardedVideoAd(){}

    public void showBannerAds(){}
    public void hideBannerAds(){}
    public void showInterstitialAds(){}
}
