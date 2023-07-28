package com.game.core.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.game.core.Constants;
import com.game.core.component.Component;
import com.game.core.component.Permissions;
import com.game.core.component.PluginWrapper;
import com.game.core.utils.Function;
import com.game.core.utils.NotificationCenter;
import com.game.core.utils.ObserverListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class SDKComponent extends Component {
    public interface IGameThreadCallBack {
        void run(Runnable runnable);
    }

    public interface IJavaCallScriptCallBack {
        void run(String msg);
    }

    private ArrayList<Component> mComponents;
    private Handler mMainThreadHandler;
    private WeakReference<Activity> mActivity = null;
    private IGameThreadCallBack mGameThreadCallBack;
    private IJavaCallScriptCallBack mJavaCallScriptCallBack;

    public Activity getActivity() {
        return this.mActivity.get();
    }
    protected ObserverListener mObserverListener = (eventName, objects) -> {
        Log.d(Constants.TAG, "onMessage " + eventName);
        if (eventName.equals(Constants.SHOW_TOAST)) {
            Log.d(Constants.TAG, "showToast");
            final String msg = (String) objects[0];
            final int duration = 1 < objects.length ? (int) objects[1] : Toast.LENGTH_SHORT;
            showToast(msg, duration);
        }
    };

    public SDKComponent() {
        mMainThreadHandler = new Handler();
        mComponents = new ArrayList<>();
    }

    public void init(Activity activity) {
        mActivity = new WeakReference<>(activity);
    }

    public void init(Activity activity, IGameThreadCallBack gameThreadCallBack) {
        mActivity = new WeakReference<>(activity);
        mGameThreadCallBack = gameThreadCallBack;
    }

    public void init(Activity activity, IGameThreadCallBack gameThreadCallBack, IJavaCallScriptCallBack javaCallScriptCallBack) {
        mActivity = new WeakReference<>(activity);
        mGameThreadCallBack = gameThreadCallBack;
        mJavaCallScriptCallBack = javaCallScriptCallBack;
    }


    public void runOnMainThread(Runnable r) {
        if (null != mMainThreadHandler) {
            mMainThreadHandler.post(r);
        }
    }

    public void runOnMainThread(Runnable r, long delayMillis) {
        if (null != mMainThreadHandler) {
            mMainThreadHandler.postDelayed(r, delayMillis);
        }
    }

    public <T extends Component> T getComponent(String componentName) {
        for (Component comp : mComponents) {
            if (comp.getClass().getSimpleName().equals(componentName))
                return (T) comp;
        }
        return null;
    }

    public <T extends Component> T getComponent(Class componentCls) {
        for (Component comp : mComponents) {
            if (comp.getClass().equals(componentCls))
                return (T) comp;
        }
        return null;
    }

    public <T extends Component> T addComponent(T component) {
        if (!isExistComponent(component)) {
            component.setActivity(getActivity());
            component.setParent(this);
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

    public  <T extends Component> T addComponent(@NonNull Class cls) {
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
            if (comp.equals(component))
                return true;
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
        NotificationCenter.getInstance().registerObserver(Constants.SHOW_TOAST, this.mObserverListener, this);
        addComponent(Permissions.class);
//        addComponent(NetworkStatus.class);
    }

    public void runOnGLThread(Runnable runnable) {
        if (null != mGameThreadCallBack)
            mGameThreadCallBack.run(runnable);
    }

    public void nativeCallScript(Object... objects) {
        StringBuilder call = new StringBuilder("ccx.eventManager.emit(");
        for (Object obj : objects) {
            if (null == obj) continue;
            call.append(obj).append(",");
        }
        call = new StringBuilder(call.substring(0, call.length() - 1) + ")");
        Log.d(Constants.TAG, "nativeCallScript ->[ " + call + " ]");

        final String call_ = call.toString();
        this.runOnGLThread(() -> {
            if (null != mJavaCallScriptCallBack) mJavaCallScriptCallBack.run(call_);
        });
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
}
