package com.pluginx.core.component;

import com.pluginx.core.base.BaseApp;

interface IAppComponent {
    void onLoad();
}

public class AppComponent implements IAppComponent {
    protected BaseApp mApp;
    protected boolean bDebug = false;

    public void setDebug(boolean bDebug) {
        this.bDebug = bDebug;
    }

    public boolean getDebug() {
        return bDebug;
    }

    public AppComponent() {
    }

    public AppComponent(BaseApp app) {
        this.mApp = app;
    }

    public BaseApp getApp() {
        return mApp;
    }

    public void setApp(BaseApp mApp) {
        this.mApp = mApp;
    }

    @Override
    public void onLoad() {

    }
}
