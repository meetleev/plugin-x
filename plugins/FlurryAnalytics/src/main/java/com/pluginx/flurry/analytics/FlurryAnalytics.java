package com.pluginx.flurry.analytics;

import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.pluginx.core.component.AnalyticsWrapper;

import java.util.HashMap;

public class FlurryAnalytics extends AnalyticsWrapper {
    private static final String Tag = "FlurryAnalytics";

    @Override
    public void logEvent(String eventId, String parameters) {
        Log.d(Tag, "logEvent eventId: " + eventId + " parameters: " + parameters);
        if (null != parameters && !parameters.isEmpty()) {
            String[] values = parameters.split(";");
            HashMap<String, String> params = new HashMap<>();
            for (String value : values) {
                String[] kv = value.split(",");
                params.put(kv[0], kv[1]);
            }
            FlurryAgent.logEvent(eventId, params);
        } else FlurryAgent.logEvent(eventId);
    }
}
