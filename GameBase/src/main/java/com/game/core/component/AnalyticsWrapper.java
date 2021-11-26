package com.game.core.component;

import android.util.Log;

import com.game.core.Constants;
import com.game.core.utils.NotificationCenter;
import com.game.core.utils.ObserverListener;

public class AnalyticsWrapper extends PluginWrapper {
    protected ObserverListener mObserverListener = new ObserverListener() {
        @Override
        public void onMessage(Object target, String eventName, Object... objects) {
            Log.d(Constants.TAG, "onMessage " + eventName);
            if (target.getClass().getSuperclass().equals(AnalyticsWrapper.class)) {
                String sdkName = (String) objects[0];
                Log.d(Constants.TAG, "onMessage sdkName " + sdkName + " clsName " + target.getClass().getSimpleName());
                if (!target.getClass().getSimpleName().toLowerCase().contains(sdkName.toLowerCase()))
                    return;
                if (Constants.LOG_EVENT.equals(eventName)) {
                    String eventId = (String) objects[1];
                    String value = null;
                    if (2 < objects.length) value = (String) objects[2];
                    logEvent(eventId, value);
                }
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
