package com.pluginx.vungle.ads;

import android.content.res.Configuration;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.pluginx.core.Constants;
import com.pluginx.core.component.AdsWrapper;
import com.pluginx.core.component.PluginError;
import com.vungle.warren.AdConfig;
import com.vungle.warren.BannerAdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

public class VungleAd extends AdsWrapper {
    private static final String META_VUNGLE_APP_ID = "VUNGLE_APP_ID";
    private static final String META_VUNGLE_REWARD_VIDEO_AD_ID = "VUNGLE_REWARD_VIDEO_AD_ID";
    private static final String META_VUNGLE_REWARD_INTERSTITIAL_AD_ID = "VUNGLE_REWARD_INTERSTITIAL_AD_ID";
    private static final String META_VUNGLE_INTERSTITIAL_AD_ID = "VUNGLE_INTERSTITIAL_AD_ID";
    private static final String META_VUNGLE_BANNER_AD_ID = "VUNGLE_BANNER_AD_ID";
    private static final String TAG = "VungleAd";
    private VungleBanner mBannerAdView;
    private LinearLayout bannerLayout;
    private BannerAdConfig bannerAdConfig;

    @Override
    protected void initSDK() {
        super.initSDK();
        String appId = getParent().getStringMetaFromApp(META_VUNGLE_APP_ID);
        Log.d(TAG, "appId" + appId);
        if (null != appId && !appId.isEmpty()) {
            runOnMainThread(() -> Vungle.init(appId, getActivity().getApplicationContext(), new InitCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onInitializationComplete");
                    preloadRewardedAd();
                    loadBannerAd(true);
                }

                @Override
                public void onError(VungleException e) {
                    Log.e(TAG, "SDK Init Error : " + e.getLocalizedMessage());
                }

                @Override
                public void onAutoCacheAdAvailable(String placementId) {
                    Log.d(TAG, "Auto Cache Ad Available For Placement : " + placementId);
                }
            }));
        } else {
            Log.e(TAG, "appId nun given!");
        }
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
            onShowAdFailed(AdType.RewardedVideo, new PluginError(AdState.Error.ordinal(), UNIT_AD_EMPTY));
            return;
        }
        rewardAdState = AdState.Loading;
        Vungle.loadAd(adId, new LoadAdCallback() {
            @Override
            public void onAdLoad(String placementId) {
                rewardAdState = AdState.Loaded;
                Log.d(TAG, "RewardedVideoAd:{ " + placementId + " } was loaded.");
            }

            @Override
            public void onError(String placementId, VungleException exception) {
                Log.d(TAG, "RewardedVideoAd:{ " + placementId + " }  error:  " + exception.getMessage());
                rewardAdState = AdState.None;
            }
        });
    }

    @Override
    public void showRewardedVideoAd() {
        String adId = getAdUnitId(AdType.RewardedVideo);
        if (null == adId) {
            onShowAdFailed(AdType.RewardedVideo, new PluginError(AdState.Error.ordinal(), UNIT_AD_EMPTY));
            return;
        }
        if (AdState.Loaded == rewardAdState) {
            runOnMainThread(() -> {
                if (Vungle.canPlayAd(adId)) {
                    Vungle.playAd(adId, null, new PlayAdCallback() {
                        @Override
                        public void creativeId(String creativeId) {
                            Log.d(TAG, "RewardedVideoAd:{ " + creativeId + " } was created.");
                        }

                        @Override
                        public void onAdStart(String placementId) {
                            Log.d(TAG, "RewardedVideoAd:{ " + placementId + " } was onAdStart.");
                        }

                        @Override
                        public void onAdEnd(String placementId, boolean completed, boolean isCTAClicked) {

                        }

                        @Override
                        public void onAdEnd(String placementId) {
                            Log.d(TAG, "RewardedVideoAd:{ " + placementId + " } was onAdEnd.");
                            if (AdState.Watched == rewardAdState)
                                onShowAdSuccess(AdType.RewardedVideo);
                            else
                                onShowAdFailed(AdType.RewardedVideo, new PluginError(AdState.NotWatchComplete.ordinal()));
                            rewardAdState = AdState.None;
                            preloadRewardedAd();
                        }

                        @Override
                        public void onAdClick(String placementId) {
                            Log.d(TAG, "RewardedVideoAd:{ " + placementId + " } was onAdClick.");
                        }

                        @Override
                        public void onAdRewarded(String placementId) {
                            Log.d(TAG, "RewardedVideoAd:{ " + placementId + " } was onAdRewarded.");
                            rewardAdState = AdState.Watched;
                        }

                        @Override
                        public void onAdLeftApplication(String placementId) {
                            Log.d(TAG, "RewardedVideoAd:{ " + placementId + " } was onAdLeftApplication.");
                        }

                        @Override
                        public void onError(String placementId, VungleException exception) {
                            Log.d(TAG, "RewardedVideoAd:{ " + placementId + " } was onError." + exception.getMessage());
                        }

                        @Override
                        public void onAdViewed(String placementId) {
                            Log.d(TAG, "RewardedVideoAd:{ " + placementId + " } was onAdViewed.");
                        }
                    });
                } else {
                    preloadRewardedAd();
                }
            });
        } else {
            onShowAdFailed(AdType.RewardedVideo, new PluginError(rewardAdState.ordinal()));
            runOnMainThread(this::preloadRewardedAd);
        }
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
            onShowAdFailed(AdType.Interstitial, new PluginError(AdState.Error.ordinal(), UNIT_AD_EMPTY));
            return;
        }
        interstitialAdState = AdState.Loading;
        Vungle.loadAd(adId, null, new LoadAdCallback() {
            @Override
            public void onAdLoad(String placementId) {
                interstitialAdState = AdState.Loaded;
                Log.d(TAG, "InterstitialAd:{ " + placementId + " } was loaded.");
            }

            @Override
            public void onError(String placementId, VungleException exception) {
                Log.d(TAG, "InterstitialAd:{ " + placementId + " } error:" + exception.getMessage());
                interstitialAdState = AdState.None;
            }
        });
    }

    @Override
    public void showInterstitialAd() {
        String adId = getAdUnitId(AdType.Interstitial);
        if (null == adId) {
            onShowAdFailed(AdType.Interstitial, new PluginError(AdState.Error.ordinal(), UNIT_AD_EMPTY));
            return;
        }
        if (AdState.Loaded == interstitialAdState) {
            runOnMainThread(() -> {
                if (Vungle.canPlayAd(adId)) {
                    Vungle.playAd(adId, null, new PlayAdCallback() {
                        @Override
                        public void creativeId(String creativeId) {
                            Log.d(TAG, "InterstitialAd:{ " + creativeId + " } was created.");
                        }

                        @Override
                        public void onAdStart(String placementId) {
                            Log.d(TAG, "InterstitialAd:{ " + placementId + " } was onAdStart.");
                        }

                        @Override
                        public void onAdEnd(String placementId, boolean completed, boolean isCTAClicked) {
                        }

                        @Override
                        public void onAdEnd(String placementId) {
                            Log.d(TAG, "InterstitialAd:{ " + placementId + " } was onAdEnd.");
                            onShowAdSuccess(AdType.Interstitial);
                            interstitialAdState = AdState.None;
                            preloadInterstitialAd();
                        }

                        @Override
                        public void onAdClick(String placementId) {
                            Log.d(TAG, "InterstitialAd:{ " + placementId + " } was onAdClick.");
                        }

                        @Override
                        public void onAdRewarded(String placementId) {
                            Log.d(TAG, "InterstitialAd:{ " + placementId + " } was onAdRewarded.");
                            interstitialAdState = AdState.Watched;
                        }

                        @Override
                        public void onAdLeftApplication(String placementId) {
                            Log.d(TAG, "InterstitialAd:{ " + placementId + " } was onAdLeftApplication.");
                        }

                        @Override
                        public void onError(String placementId, VungleException exception) {
                            Log.d(TAG, "InterstitialAd:{ " + placementId + " } was onError." + exception.getMessage());
                        }

                        @Override
                        public void onAdViewed(String placementId) {
                            Log.d(TAG, "InterstitialAd:{ " + placementId + " } was onAdViewed.");
                        }
                    });
                } else {
                    preloadInterstitialAd();
                }
            });
        } else {
            onShowAdFailed(AdType.Interstitial, new PluginError(interstitialAdState.ordinal()));
            runOnMainThread(this::preloadInterstitialAd);
        }
    }

    @Override
    protected void loadBannerAd(boolean autoShow) {
        super.loadBannerAd(autoShow);
        runOnMainThread(() -> {
            if (AdState.Loading == bannerAdState || AdState.Loaded == bannerAdState) {
                Log.d(TAG, "Banner loading or loaded");
                return;
            }
            String adId = getAdUnitId(AdType.Banner);
            if (null == adId) {
                return;
            }
            bannerAdState = AdState.Loading;
            makeBannerAdConfigNonNull();
            Banners.loadBanner(adId, bannerAdConfig, new LoadAdCallback() {
                @Override
                public void onAdLoad(String placementId) {
                    Log.d(TAG, "BannerAd was adLoaded.");
                    bannerAdState = AdState.Loaded;
                    if (autoShow) {
                        _showBannerAd(adId);
                    }
                }

                @Override
                public void onError(String placementId, VungleException exception) {
                    Log.d(TAG, "BannerAd was adFailedToLoad. err:" + exception.getMessage());
                    bannerAdState = AdState.None;
                }
            });
        });
    }

    private void makeBannerAdConfigNonNull() {
        if (null == bannerAdConfig) {
            bannerAdConfig = new BannerAdConfig();
            bannerAdConfig.setAdSize(isTabletDevice() ? AdConfig.AdSize.BANNER_LEADERBOARD : AdConfig.AdSize.BANNER);
        }
    }

    private void makeBannerAdContainerNonNull() {
        if (null == bannerLayout) {
            bannerLayout = new LinearLayout(getActivity());
            bannerLayout.setGravity(LinearLayout.VERTICAL);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            getActivity().addContentView(bannerLayout, params);
        }
    }


    private boolean isTabletDevice() {
        return (getActivity().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void showBannerAd() {
        super.showBannerAd();
        String adId = getAdUnitId(AdType.Banner);
        if (null == adId) {
            return;
        }
        if (AdState.Loaded == bannerAdState) {
            if (null != mBannerAdView) {
                runOnMainThread(() -> {
                    ViewGroup mViewGroup = (ViewGroup) mBannerAdView.getParent();
                    if (null != mViewGroup)
                        mViewGroup.setVisibility(View.VISIBLE);
                });
            } else {
                runOnMainThread(() -> {
                    makeBannerAdConfigNonNull();
                    if (Banners.canPlayAd(adId, bannerAdConfig.getAdSize())) {
                        _showBannerAd(adId);
                    } else {
                        bannerAdState = AdState.None;
                    }
                });
            }
        } else {
            runOnMainThread(() -> {
                if (null != mBannerAdView)
                    mBannerAdView.destroyAd();
                mBannerAdView = null;
                loadBannerAd(true);
            });
        }
    }

    private void _showBannerAd(String adId) {
        makeBannerAdContainerNonNull();
        mBannerAdView = Banners.getBanner(adId, bannerAdConfig, new PlayAdCallback() {
            @Override
            public void creativeId(String creativeId) {
                Log.d(TAG, "BannerAd was creativeId." + creativeId);
            }

            @Override
            public void onAdStart(String placementId) {
                Log.d(TAG, "BannerAd was onAdStart." + placementId);
            }

            @Override
            public void onAdEnd(String placementId, boolean completed, boolean isCTAClicked) {
                Log.d(TAG, "BannerAd was onAdEnd2." + placementId);
            }

            @Override
            public void onAdEnd(String placementId) {
                Log.d(TAG, "BannerAd was onAdEnd." + placementId);
            }

            @Override
            public void onAdClick(String placementId) {
                Log.d(TAG, "BannerAd was onAdClick." + placementId);
            }

            @Override
            public void onAdRewarded(String placementId) {
                Log.d(TAG, "BannerAd was onAdRewarded." + placementId);
            }

            @Override
            public void onAdLeftApplication(String placementId) {
                Log.d(TAG, "BannerAd was onAdLeftApplication." + placementId);
            }

            @Override
            public void onError(String placementId, VungleException exception) {
                Log.d(TAG, "BannerAd was onAdLeftApplication." + placementId + " e " + exception.getMessage());
            }

            @Override
            public void onAdViewed(String placementId) {
                Log.d(TAG, "BannerAd was onAdViewed." + placementId);
            }
        });
        bannerLayout.addView(mBannerAdView);
    }

    @Override
    public void hideBannerAd() {
        super.hideBannerAd();
        if (null != mBannerAdView) {
            runOnMainThread(() -> {
                ViewGroup mViewGroup = (ViewGroup) mBannerAdView.getParent();
                if (null != mViewGroup)
                    mViewGroup.setVisibility(View.INVISIBLE);
            });
        }
    }

    private String getAdUnitId(AdType adType) {
        String metaKey = null;
        if (AdType.RewardedVideo == adType) {
            metaKey = META_VUNGLE_REWARD_VIDEO_AD_ID;
        } else if (AdType.Interstitial == adType) {
            metaKey = META_VUNGLE_INTERSTITIAL_AD_ID;
        } else if (AdType.Banner == adType) {
            metaKey = META_VUNGLE_BANNER_AD_ID;
        } else if (AdType.RewardedInterstitial == adType) {
            metaKey = META_VUNGLE_REWARD_INTERSTITIAL_AD_ID;
        }
        String adId = getParent().getStringMetaFromApp(metaKey);
        if (null == adId || adId.isEmpty()) {
            Log.d(Constants.TAG, "adId is null <adType=> " + adType);
            return null;
        }
        return adId;
    }
}
