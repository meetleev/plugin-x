package com.game.core.component;

public class AdsWrapper extends Component {
    protected final String VIDEO_PLAY_COMPLETE = "VIDEO_PLAY_COMPLETE";
    protected final String SPLASH_PLAY_COMPLETE = "SPLASH_PLAY_COMPLETE";
    private boolean bDebug = false;

    @Override
    public void onLoad() {
        super.onLoad();
        this.initSDK();
    }

    protected void initSDK() {
    }

    public void exitSDK() {
    }

    public void showRewardedVideoAd() {
    }

    public void showBannerAds() {
    }

    public void hideBannerAds() {
    }

    public void showInterstitialAds() {
    }

    public void setDebug(boolean bDebug) {
        this.bDebug = bDebug;
    }

    public boolean getDebug() {
        return bDebug;
    }
}
