package com.game.core.utils;

public interface ObserverListener {
    void onMessage(Object target, String eventName, Object... objects);
}
