package com.game.flurry;

import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.flurry.android.FlurryPerformance;
import com.game.core.Constants;
import com.game.core.component.AppComponent;

public class FlurryApp extends AppComponent {
    private static final String FLURRY_API_KEY = "FLURRY_API_KEY";
    private static final String META_FLURRY_CHANNEL = "FLURRY_CHANNEL";
    private static final String FLURRY_CHANNEL = "Channel";

    public void onLoad() {
        String appKey = mApp.getMetaFromApplication(FLURRY_API_KEY);
        if (null == appKey || appKey.isEmpty()) {
            Log.e(Constants.TAG, "FlurryApp appKey is null");
            return;
        }
        String channelName = mApp.getMetaFromApplication(META_FLURRY_CHANNEL, "Official");
        Log.d(Constants.TAG, "channel: " + channelName);
        new FlurryAgent.Builder()
                .withDataSaleOptOut(false) //CCPA - the default value is false
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.VERBOSE)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .withListener(new FlurryAgentListener() {
                    @Override
                    public void onSessionStarted() {
                        FlurryAgent.UserProperties.set(FLURRY_CHANNEL, channelName);
                    }
                })
                .build(mApp, appKey);
    }
}
