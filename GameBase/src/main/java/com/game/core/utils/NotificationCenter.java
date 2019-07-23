package com.game.core.utils;

import java.util.ArrayList;
import java.util.HashMap;

// @author LeeWei 消息中心
// ----------------------------------------NotificationObserver-----------------------------
class Observer {
    private Object mTarget;
    public static final String ObserverName = "Observer";
    private HashMap<String, ObserverListener> mObserverListenerMap;
    private static final String EVENT_NAME_PREFIX = "$";

    public Observer(Object target, ObserverListener listener, String name) {
        name = (null == name || name.isEmpty()) ? "" : name;
        this.mTarget = target;
        if (null == this.mObserverListenerMap)
            this.mObserverListenerMap = new HashMap<>();
        this.addListener(name, listener);
    }

    public Object getTarget() {
        return this.mTarget;
    }

    public HashMap<String, ObserverListener> getListeners() {
        return this.mObserverListenerMap;
    }

    public ObserverListener getListener(String eventName) {
        if (null != eventName && this.mObserverListenerMap.containsKey(EVENT_NAME_PREFIX + eventName))
            return this.mObserverListenerMap.get(EVENT_NAME_PREFIX + eventName);
        return null;
    }

    public void addListener(String eventName, ObserverListener listener) {
        if (null != eventName && null == this.getListener(eventName))
            this.mObserverListenerMap.put(EVENT_NAME_PREFIX + eventName, listener);
    }

    public void removeListener(String eventName) {
        if (null != this.getListener(eventName))
            this.mObserverListenerMap.remove(EVENT_NAME_PREFIX + eventName);
    }
}

//  ---------------------------------------NotificationCenter---------------------------------------
public class NotificationCenter {
    private ArrayList<Observer> mObservers;
    private static final NotificationCenter mNotificationCenter = new NotificationCenter();

    public NotificationCenter() {
        this.mObservers = new ArrayList<>();
    }

    public static NotificationCenter getInstance() {
        return mNotificationCenter;
    }

    //  注册事件
    public void registerObserver(Object target, ObserverListener listener, String eventName) {
        Observer observer = this.observerExisted(target);
        if (null != observer && null != observer.getListener(eventName)) return;
        if (null == observer) {
            observer = new Observer(target, listener, eventName);
            this.mObservers.add(observer);
        } else
            observer.addListener(eventName, listener);
    }

    public void registerObserver(Object target, ObserverListener listener) {
        this.registerObserver(target, listener, Observer.ObserverName);
    }

    private Observer observerExisted(Object target) {
        if (null == target) return null;
        for (Observer observer : this.mObservers) {
            if (target == observer.getTarget())
                return observer;
        }
        return null;
    }

    //  通知事件
    public void postNotification(String eventName, Object... objects) {
        for (Observer observer : this.mObservers) {
            ObserverListener listener = observer.getListener(eventName);
            if (null != listener)
                listener.onMessage(observer.getTarget(), eventName, objects);
            else {
                listener = observer.getListener(Observer.ObserverName);
                if (null != listener)
                    listener.onMessage(observer.getTarget(), eventName, objects);
            }
        }
    }

    // 移除单一事件
    public void unregisterObserver(Object target, String eventName) {
        if (null == target) return;
        Observer observer = this.observerExisted(target);
        if (null != observer)
            observer.removeListener(eventName);
    }

    // 移除事件
    public void unregisterAllObserver(Object target) {
        if (null == target) return;
        for (int i = 0; i < this.mObservers.size(); i++) {
            Observer observer = this.mObservers.get(i);
            if (target == observer.getTarget())
                this.mObservers.remove(i);
        }
    }
}