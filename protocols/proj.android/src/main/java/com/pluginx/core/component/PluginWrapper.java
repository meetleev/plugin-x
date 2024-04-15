package com.pluginx.core.component;


import com.pluginx.core.base.SDKComponent;
import com.pluginx.core.utils.NotificationCenter;

import java.util.Hashtable;

public class PluginWrapper extends Component {
    public enum PluginStatus {
        Success, Failed, Canceled,
    }
    public SDKComponent getParent() {
        return (SDKComponent) (root.get());
    }

    public void runOnMainThread(Runnable r) {
        getParent().runOnMainThread(r);
    }

    public void runOnMainThread(Runnable r, long delayMillis) {
        getParent().runOnMainThread(r, delayMillis);
    }

    public void runOnGLThread(Runnable runnable) {
        getParent().runOnGLThread(runnable);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.initSDK();
    }

    public void configDevInfo(Hashtable<String, String> cpInfo) {
    }


    protected void initSDK() {
    }

    public void exitSDK() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance().unregisterAllObserver(this);
    }
}
