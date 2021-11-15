package com.game.googleMobAd;

import android.util.Log;

import androidx.annotation.NonNull;

import com.game.core.Constants;
import com.game.core.component.AdWrapper;
import com.game.core.component.InterfaceAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class GoogleMobAd extends AdWrapper implements InterfaceAd {
    private static final String META_GP_REWARD_VIDEO_AD_ID = "GP_REWARD_VIDEO_AD_ID";
    private static final String META_GP_INTERSTITIAL_AD_ID = "GP_INTERSTITIAL_AD_ID";
    private static final String TAG = "GoogleMobAd";
    private RewardedAd mRewardedAd;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void initSDK() {
        super.initSDK();
        mActivity.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                MobileAds.initialize(mActivity, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                        Log.d(TAG, "onInitializationComplete");
                        preloadRewardedAd();
                        preloadInterstitialAd();
                    }
                });
            }
        });
    }

    @Override
    public void preloadRewardedAd() {
        super.preloadRewardedAd();
        if (AdState.Loading == rewardAdState || AdState.Loaded == rewardAdState) {
            Log.d(TAG, "RewardedAd loading or loaded");
            return;
        }
        String adId = getAdUnitId(AdType.RewardedVideo);
        if (null == adId) {
            onShowRewardVideoAdResult(false, AdState.Error.ordinal());
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
                        onShowRewardVideoAdResult(AdState.Watched == rewardAdState, -1);
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
                mActivity.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mRewardedAd.show(mActivity, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                Log.d(TAG, "RewardedVideoAd onUserEarnedReward.");
                                rewardAdState = AdState.Watched;
                            }
                        });
                    }
                });
            }
        } else {
            onShowRewardVideoAdResult(false, rewardAdState.ordinal());
            mActivity.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    preloadRewardedAd();
                }
            });
        }
    }

    @Override
    public void onShowRewardVideoAdResult(boolean bSuccess, int errCode) {
        mActivity.nativeCallScript(ON_SHOW_REWARD_VIDEO_AD_RESULT, bSuccess, errCode);
    }

    @Override
    public void onShowInterstitialAdResult(boolean bSuccess, int errCode) {

    }

    @Override
    public void preloadInterstitialAd() {
        super.preloadInterstitialAd();
        if (AdState.Loading == interstitialAdState || AdState.Loaded == interstitialAdState) {
            Log.d(TAG, "Interstitial loading or loaded");
            return;
        }
        String adId = getAdUnitId(AdType.Interstitial);
        if (null == adId) {
            onShowInterstitialAdResult(false, AdState.Error.ordinal());
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
                        onShowInterstitialAdResult(AdState.Watched == interstitialAdState, -1);
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
                mActivity.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        interstitialAdState = AdState.Watched;
                        mInterstitialAd.show(mActivity);
                    }
                });
            }
        } else {
            onShowInterstitialAdResult(false, interstitialAdState.ordinal());
            mActivity.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    preloadInterstitialAd();
                }
            });
        }
    }

    private String getAdUnitId(AdType adType) {
        String metaKey = null;
        if (AdType.RewardedVideo == adType) {
            metaKey = META_GP_REWARD_VIDEO_AD_ID;
        } else if (AdType.Interstitial == adType) {
            metaKey = META_GP_INTERSTITIAL_AD_ID;
        }
        String adId = mActivity.getMetaFromApplication(metaKey);
        if (null == adId || adId.isEmpty()) {
            Log.d(Constants.TAG, "adId is null <adType=> " + adType);
            return null;
        }
        return adId;
    }
}
