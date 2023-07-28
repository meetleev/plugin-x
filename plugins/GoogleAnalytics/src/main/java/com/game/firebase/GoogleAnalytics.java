package com.game.firebase;

import android.os.Bundle;
import android.util.Log;

import com.game.core.Constants;
import com.game.core.component.AnalyticsWrapper;
import com.google.firebase.analytics.FirebaseAnalytics;

public class GoogleAnalytics extends AnalyticsWrapper {
    private static final String Tag = "GoogleAnalytics";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onLoad() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mActivity);
        String channelName = mActivity.getMetaFromApplication(Constants.META_ANALYTICS_CHANNEL, "Official");
        Log.d(Constants.TAG, "channel: " + channelName);
        mFirebaseAnalytics.setUserProperty(Constants.ANALYTICS_CHANNEL, channelName);
        super.onLoad();
    }

    @Override
    public void logEvent(String eventId, String parameters) {
        Log.d(Tag, "logEvent eventId: " + eventId + " parameters: " + parameters);
        Bundle params = new Bundle();
        if (null != parameters && !parameters.isEmpty()) {
            String[] values = parameters.split(";");
            for (String value : values) {
                String[] kv = value.split(",");
                params.putString(kv[0], kv[1]);
            }
        }
        mFirebaseAnalytics.logEvent(eventId, params);
    }
}
