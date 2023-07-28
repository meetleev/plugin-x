package com.game.core.utils;

public interface ObserverListener {
    void onMessage( String eventName, Object... objects);
}
