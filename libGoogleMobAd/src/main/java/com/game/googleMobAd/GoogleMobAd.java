package com.game.googleMobAd;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.game.core.Constants;
import com.game.core.component.AdsWrapper;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import androidx.annotation.NonNull;

public class GoogleMobAd extends AdsWrapper {
    private static final String META_GP_REWARD_VIDEO_AD_ID = "GP_REWARD_VIDEO_AD_ID";
    private static final String META_GP_REWARD_INTERSTITIAL_AD_ID = "GP_REWARD_INTERSTITIAL_AD_ID";
    private static final String META_GP_INTERSTITIAL_AD_ID = "GP_INTERSTITIAL_AD_ID";
    private static final String META_GP_BANNER_AD_ID = "GP_BANNER_AD_ID";
    private static final String TAG = "GoogleMobAd";
    private RewardedAd mRewardedAd;
    private InterstitialAd mInterstitialAd;
    private AdView mBannerAdView;
    private RewardedInterstitialAd mRewardedInterstitialAd;

    @Override
    protected void initSDK() {
        /*List<String> testDeviceIds = Arrays.asList("51F095007FA7FA0F9278044921BAB2EC");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);*/
        super.initSDK();
        mActivity.runOnMainThread(() -> MobileAds.initialize(mActivity, (InitializationStatus initializationStatus) -> {
            Log.d(TAG, "onInitializationComplete");
            preloadRewardedAd();
            preloadInterstitialAd();
            preloadRewardedInterstitialAd();
        }));
    }

    @Override
    protected void preloadRewardedAd() {
        super.preloadRewardedAd();
        if (AdState.Loading == rewardAdState || AdState.Loaded == rewardAdState) {
            Log.d(TAG, "RewardedAd loading or loaded");
            return;
        }
        String adId = getAdUnitId(AdType.RewardedVideo);
        if (null == adId) {
            onShowAdFailed(AdType.RewardedVideo, AdState.Error.ordinal());
            return;
        }
        rewardAdState = AdState.Loading;
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(mActivity, adId,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, "RewardedVideoAd " + loadAdError.getMessage());
                        mRewardedAd = null;
                        rewardAdState = AdState.None;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        rewardAdState = AdState.Loaded;
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "RewardedVideoAd was loaded.");
                    }
                });
    }

    @Override
    public void showRewardedVideoAd() {
        if (AdState.Loaded == rewardAdState) {
            if (null != mRewardedAd) {
                mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        Log.d(TAG, "RewardedVideoAd failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        Log.d(TAG, "RewardedVideoAd was shown.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Log.d(TAG, "RewardedVideoAd was dismissed.");
                        mRewardedAd = null;
                        if (AdState.Watched == rewardAdState)
                            onShowAdSuccess(AdType.RewardedVideo);
                        else
                            onShowAdFailed(AdType.RewardedVideo, AdState.NotWatchComplete.ordinal());
                        rewardAdState = AdState.None;
                        preloadRewardedAd();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        Log.d(TAG, "RewardedVideoAd was impression.");
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.d(TAG, "RewardedVideoAd was clicked.");
                    }
                });
                mActivity.runOnMainThread(() -> mRewardedAd.show(mActivity, rewardItem -> {
                    Log.d(TAG, "RewardedVideoAd onUserEarnedReward.");
                    rewardAdState = AdState.Watched;
                }));
            }
        } else {
            onShowAdFailed(AdType.RewardedVideo, rewardAdState.ordinal());
            mActivity.runOnMainThread(this::preloadRewardedAd);
        }
    }

    @Override
    protected void onShowAdSuccess(AdType adType) {
        mActivity.nativeCallScript(ON_SHOW_AD_SUCCESS, adType.ordinal());
    }

    @Override
    protected void onShowAdFailed(AdType adType, int errCode) {
        mActivity.nativeCallScript(ON_SHOW_AD_FAILED, adType.ordinal(), errCode);
    }

    @Override
    protected void preloadInterstitialAd() {
        super.preloadInterstitialAd();
        if (AdState.Loading == interstitialAdState || AdState.Loaded == interstitialAdState) {
            Log.d(TAG, "Interstitial loading or loaded");
            return;
        }
        String adId = getAdUnitId(AdType.Interstitial);
        if (null == adId) {
            onShowAdFailed(AdType.Interstitial, AdState.Error.ordinal());
            return;
        }
        interstitialAdState = AdState.Loading;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(mActivity, adId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        interstitialAdState = AdState.Loaded;
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "InterstitialAd onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, "InterstitialAd " + loadAdError.getMessage());
                        mInterstitialAd = null;
                        interstitialAdState = AdState.None;
                    }
                });
    }

    @Override
    public void showInterstitialAd() {
        if (AdState.Loaded == interstitialAdState) {
            if (null != mInterstitialAd) {
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        Log.d(TAG, "InterstitialAd failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        Log.d(TAG, "InterstitialAd was shown.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Log.d(TAG, "InterstitialAd was dismissed.");
                        mInterstitialAd = null;
                        if (AdState.Watched == interstitialAdState) {
                            onShowAdSuccess(AdType.Interstitial);
                        } else {
                            onShowAdFailed(AdType.Interstitial, AdState.NotWatchComplete.ordinal());
                        }
                        interstitialAdState = AdState.None;
                        preloadInterstitialAd();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        Log.d(TAG, "InterstitialAd was impression.");
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.d(TAG, "InterstitialAd was clicked.");
                    }
                });
                mActivity.runOnMainThread(() -> {
                    interstitialAdState = AdState.Watched;
                    mInterstitialAd.show(mActivity);
                });
            }
        } else {
            onShowAdFailed(AdType.Interstitial, interstitialAdState.ordinal());
            mActivity.runOnMainThread(this::preloadInterstitialAd);
        }
    }

    @Override
    protected void preloadRewardedInterstitialAd() {
        super.preloadRewardedInterstitialAd();
        if (AdState.Loading == rewardInterstitialAdState || AdState.Loaded == rewardInterstitialAdState) {
            Log.d(TAG, "RewardInterstitialAd loading or loaded");
            return;
        }
        String adId = getAdUnitId(AdType.RewardedInterstitial);
        if (null == adId) {
            onShowAdFailed(AdType.RewardedInterstitial, AdState.Error.ordinal());
            return;
        }
        rewardInterstitialAdState = AdState.Loading;
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedInterstitialAd.load(mActivity, adId, adRequest,
                new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd rewardedInterstitialAd) {
                        // The mRewardedInterstitialAd reference will be null until
                        // an ad is loaded.
                        rewardInterstitialAdState = AdState.Loaded;
                        mRewardedInterstitialAd = rewardedInterstitialAd;
                        Log.i(TAG, "RewardInterstitialAd onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, "RewardInterstitialAd " + loadAdError.getMessage());
                        mRewardedInterstitialAd = null;
                        rewardInterstitialAdState = AdState.None;
                    }
                });
    }

    @Override
    public void showRewardedInterstitialAd() {
        if (AdState.Loaded == rewardInterstitialAdState) {
            if (null != mRewardedInterstitialAd) {
                mRewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        Log.d(TAG, "RewardedInterstitialAd failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        Log.d(TAG, "RewardedInterstitialAd was shown.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Log.d(TAG, "RewardedInterstitialAd was dismissed.");
                        mRewardedInterstitialAd = null;
                        if (AdState.Watched == rewardInterstitialAdState) {
                            onShowAdSuccess(AdType.RewardedInterstitial);
                        } else {
                            onShowAdFailed(AdType.RewardedInterstitial, AdState.NotWatchComplete.ordinal());
                        }
                        rewardInterstitialAdState = AdState.None;
                        preloadRewardedInterstitialAd();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        Log.d(TAG, "RewardedInterstitialAd was impression.");
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.d(TAG, "RewardedInterstitialAd was clicked.");
                    }
                });
                mActivity.runOnMainThread(() -> mRewardedInterstitialAd.show(mActivity, (@NonNull RewardItem rewardItem) -> rewardInterstitialAdState = AdState.Watched));
            }
        } else {
            onShowAdFailed(AdType.RewardedInterstitial, rewardInterstitialAdState.ordinal());
            mActivity.runOnMainThread(this::preloadRewardedInterstitialAd);
        }
    }

    @Override
    protected void loadBannerAd() {
        super.loadBannerAd();
        mActivity.runOnMainThread(() -> {
            if (AdState.Loading == bannerAdState || AdState.Loaded == bannerAdState) {
                Log.d(TAG, "Banner loading or loaded");
                return;
            }
            String adId = getAdUnitId(AdType.Banner);
            if (null == adId) {
                return;
            }
            bannerAdState = AdState.Loading;

            if (null == mBannerAdView) {
                LinearLayout layout = new LinearLayout(mActivity);
                layout.setGravity(LinearLayout.VERTICAL);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.BOTTOM;
                mActivity.addContentView(layout, params);

                mBannerAdView = new AdView(mActivity);
                mBannerAdView.setAdUnitId(adId);
                mBannerAdView.setAdSize(getAdSize());

                layout.addView(mBannerAdView);


                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mBannerAdView.loadAd(adRequest);

                mBannerAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Log.d(TAG, "BannerAd was closed.");
                        bannerAdState = AdState.None;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d(TAG, "BannerAd was adFailedToLoad.");
                        bannerAdState = AdState.None;
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        Log.d(TAG, "BannerAd was adOpened.");
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.d(TAG, "BannerAd was adLoaded.");
                        bannerAdState = AdState.Loaded;
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.d(TAG, "BannerAd was adClicked.");
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        Log.d(TAG, "BannerAd was adImpression.");
                    }
                });
            }
        });
    }

    private AdSize getAdSize() {
        DisplayMetrics displayMetrics = mActivity.getResources().getDisplayMetrics();
        float widthPixels = displayMetrics.widthPixels;
        float density = displayMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth);
    }

    @Override
    public void showBannerAd() {
        super.showBannerAd();
        if (AdState.Loaded == bannerAdState) {
            if (null != mBannerAdView) {
                mActivity.runOnMainThread(() -> {
                    ViewGroup mViewGroup = (ViewGroup) mBannerAdView.getParent();
                    if (null != mViewGroup)
                        mViewGroup.setVisibility(View.VISIBLE);

                });
            } else {
                loadBannerAd();
            }
        } else {
            if (null == mBannerAdView) {
                loadBannerAd();
            } else {
                mActivity.runOnMainThread(() -> {
                    AdRequest adRequest = new AdRequest.Builder()
                            .build();
                    mBannerAdView.loadAd(adRequest);
                });
            }
        }
    }

    @Override
    public void hideBannerAd() {
        super.hideBannerAd();
        if (null != mBannerAdView) {
            mActivity.runOnMainThread(() -> {
                ViewGroup mViewGroup = (ViewGroup) mBannerAdView.getParent();
                if (null != mViewGroup)
                    mViewGroup.setVisibility(View.INVISIBLE);
            });
        }
    }

    private String getAdUnitId(AdType adType) {
        String metaKey = null;
        if (AdType.RewardedVideo == adType) {
            metaKey = META_GP_REWARD_VIDEO_AD_ID;
        } else if (AdType.Interstitial == adType) {
            metaKey = META_GP_INTERSTITIAL_AD_ID;
        } else if (AdType.Banner == adType) {
            metaKey = META_GP_BANNER_AD_ID;
        } else if (AdType.RewardedInterstitial == adType) {
            metaKey = META_GP_REWARD_INTERSTITIAL_AD_ID;
        }
        String adId = mActivity.getMetaFromApplication(metaKey);
        if (null == adId || adId.isEmpty()) {
            Log.d(Constants.TAG, "adId is null <adType=> " + adType);
            return null;
        }
        return adId;
    }
}
