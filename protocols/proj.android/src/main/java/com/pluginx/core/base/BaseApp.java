package com.pluginx.core.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.pluginx.core.Constants;
import com.pluginx.core.component.AppComponent;

import java.util.ArrayList;

public class BaseApp extends Application {
    protected static ArrayList<AppComponent> mComponents;
    private FunctionHelper functionHelper;

    public FunctionHelper getFunctionHelper() {
        return functionHelper;
    }
    public BaseApp() {
        super();
        mComponents = new ArrayList<>();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        functionHelper = new FunctionHelper(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        onLoad();
    }

    protected void onLoad() {
    }

    public <T extends AppComponent> T getComponent(String componentName) {
        for (AppComponent comp : mComponents) {
            if (comp.getClass().getSimpleName().equals(componentName)) return (T) comp;
        }
        return null;
    }

    public <T extends AppComponent> T getComponent(Class componentCls) {
        for (AppComponent comp : mComponents) {
            if (comp.getClass().equals(componentCls)) return (T) comp;
        }
        return null;
    }

    protected <T extends AppComponent> T addComponent(T component) {
        if (!isExistComponent(component)) {
            component.setApp(this);
            component.onLoad();
            mComponents.add(component);
        }
        return component;
    }

    protected <T extends AppComponent> T addComponent(String clsName) {
        try {
            return addComponent(Class.forName(clsName));
        } catch (Exception e) {
            Log.d(Constants.TAG, "BaseApp addComponent string ->" + e);
        }
        return null;
    }

    protected <T extends AppComponent> T addComponent(Class cls) {
        if (null != cls && (cls.getSuperclass().equals(AppComponent.class))) {
            try {
                return addComponent((T) cls.newInstance());
            } catch (Exception e) {
                Log.d(Constants.TAG, "BaseApp addComponent ->" + e);
            }
        }
        return null;
    }

    private boolean isExistComponent(AppComponent component) {
        for (AppComponent comp : mComponents) {
            if (comp.equals(component)) return true;
        }
        return false;
    }

    public String getStringMetaFromApp(String key) {
        return getStringMetaFromApp(key, null);
    }

    public String getStringMetaFromApp(String key, String defaultValue) {
        return functionHelper.getStringMetaFromApp( key, defaultValue);
    }
}
