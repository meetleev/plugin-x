package com.pluginx.core.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.pluginx.core.component.Component;
import com.pluginx.core.component.Permissions;
import com.pluginx.core.component.PluginWrapper;
import com.pluginx.core.utils.Function;
import com.pluginx.core.utils.NotificationCenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class SDKComponent extends Component {
    public interface IGameThreadCallBack {
        void run(Runnable runnable);
    }

    private final ArrayList<Component> mComponents;
    private final Handler mMainThreadHandler;
    private WeakReference<Activity> mActivity = null;
    private IGameThreadCallBack mGameThreadCallBack;

    public Activity getActivity() {
        return this.mActivity.get();
    }

    public SDKComponent() {
        mMainThreadHandler = new Handler();
        mComponents = new ArrayList<>();
    }

    public void init(Activity activity) {
        mActivity = new WeakReference<>(activity);
        register();
        onLoad();
    }

    public void init(Activity activity, IGameThreadCallBack gameThreadCallBack) {
        mActivity = new WeakReference<>(activity);
        mGameThreadCallBack = gameThreadCallBack;
        register();
        onLoad();
    }


    public void runOnMainThread(Runnable r) {
        mMainThreadHandler.post(r);
    }

    public void runOnMainThread(Runnable r, long delayMillis) {
        mMainThreadHandler.postDelayed(r, delayMillis);
    }

    public void runOnGLThread(Runnable runnable) {
        if (null != mGameThreadCallBack) mGameThreadCallBack.run(runnable);
    }

    public <T extends Component> T getComponent(String componentName) {
        for (Component comp : mComponents) {
            if (comp.getClass().getSimpleName().equals(componentName)) return (T) comp;
        }
        return null;
    }

    public <T extends Component> T getComponent(Class componentCls) {
        for (Component comp : mComponents) {
            if (comp.getClass().equals(componentCls)) return (T) comp;
        }
        return null;
    }

    public <T extends Component> T addComponent(T component) {
        if (!isExistComponent(component)) {
            component.setActivity(getActivity());
            component.setRoot(this);
            component.onLoad();
            mComponents.add(component);
        }
        return component;
    }

    public <T extends Component> T addComponent(String clsName) {
        try {
            return addComponent(Class.forName(clsName));
        } catch (Exception e) {
            System.out.println("addComponent string ->" + e);
        }
        return null;
    }

    public <T extends Component> T addComponent(@NonNull Class cls) {
        if (Objects.equals(cls.getSuperclass(), Component.class) || Objects.equals(cls.getSuperclass().getSuperclass(), Component.class) || Objects.equals(cls.getSuperclass().getSuperclass(), PluginWrapper.class)) {
            try {
                return addComponent((T) cls.newInstance());
            } catch (Exception e) {
                System.out.println("addComponent ->" + e);
            }
        }
        return null;
    }

    private boolean isExistComponent(Component component) {
        for (Component comp : mComponents) {
            if (comp.equals(component)) return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        for (Component component : mComponents)
            component.onResume();
    }

    @Override
    public void onPause() {
        for (Component component : mComponents)
            component.onPause();
    }

    @Override
    public void onDestroy() {
        NotificationCenter.getInstance().unregisterAllObserver(this);
        for (Component component : mComponents)
            component.onDestroy();
        mComponents.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Component component : mComponents)
            component.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (Component component : mComponents)
            component.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLoad() {
        addComponent(Permissions.class);
//        addComponent(NetworkStatus.class);
    }

    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public void showToast(String msg) {
        final String msgF = msg;
        runOnMainThread(() -> Toast.makeText(getActivity().getApplicationContext(), msgF, Toast.LENGTH_SHORT).show());
    }

    public void showToast(String msg, int duration) {
        final String msgF = msg;
        final int durationF = duration;
        runOnMainThread(() -> Toast.makeText(getActivity().getApplicationContext(), msgF, durationF).show());
    }

    public String getStringMetaFromApp(String key) {
        return getStringMetaFromApp(key, null);
    }

    public String getStringMetaFromApp(String key, String defaultValue) {
        return Function.getStringMetaFromApp(getActivity(), key, defaultValue);
    }

    public boolean getBooleanMetaFromApp(String key) {
        return getBooleanMetaFromApp(key, false);
    }

    public boolean getBooleanMetaFromApp(String key, boolean defaultValue) {
        return Function.getBooleanMetaFromApp(getActivity(), key, defaultValue);
    }

    public native void register();
}
