package com.game.flurry;

import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;
import com.game.core.Constants;
import com.game.core.component.AppComponent;

public class FlurryApp extends AppComponent {
    private static final String FLURRY_API_KEY = "FLURRY_API_KEY";

    public void onLoad() {
        String appKey = mApp.getMetaFromApplication(FLURRY_API_KEY);
        if (null == appKey || appKey.isEmpty()) {
            Log.e(Constants.TAG, "FlurryApp appKey is null");
            return;
        }
        String channelName = mApp.getMetaFromApplication(Constants.META_ANALYTICS_CHANNEL, "Official");
        Log.d(Constants.TAG, "channel: " + channelName);
        new FlurryAgent.Builder()
                .withDataSaleOptOut(false) // CCPA - the default value is false
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.VERBOSE)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .withListener(() -> FlurryAgent.UserProperties.set(Constants.ANALYTICS_CHANNEL, channelName))
                .build(mApp, appKey);
    }
}
