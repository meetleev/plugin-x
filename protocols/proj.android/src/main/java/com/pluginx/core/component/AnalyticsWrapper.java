package com.pluginx.core.component;

import android.util.Log;

import com.pluginx.core.Constants;
import com.pluginx.core.utils.NotificationCenter;
import com.pluginx.core.utils.ObserverListener;

public class AnalyticsWrapper extends PluginWrapper {
    protected ObserverListener mObserverListener = new ObserverListener() {
        @Override
        public void onMessage(String eventName, Object... objects) {
            Log.d(Constants.TAG, "onMessage " + eventName);
            String sdkName = (String) objects[0];
            Log.d(Constants.TAG, "onMessage sdkName " + sdkName + " clsName " + getClass().getSimpleName());
            if (!getClass().getSimpleName().toLowerCase().contains(sdkName.toLowerCase()))
                return;
            if (Constants.LOG_EVENT.equals(eventName)) {
                String eventId = (String) objects[1];
                String value = null;
                if (2 < objects.length) value = (String) objects[2];
                logEvent(eventId, value);
            }
        }
    };

    @Override
    public void onLoad() {
        super.onLoad();
        NotificationCenter.getInstance().registerObserver(Constants.LOG_EVENT, mObserverListener, this);
    }

    public void logEvent(String eventId) {
        logEvent(eventId, null);
    }

    public void logEvent(String eventId, String value) {
    }
}
