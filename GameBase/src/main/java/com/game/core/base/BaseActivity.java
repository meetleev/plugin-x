package com.game.core.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cocos.lib.CocosActivity;
import com.cocos.lib.CocosHelper;
import com.cocos.lib.CocosJavascriptJavaBridge;
import com.game.core.Constants;
import com.game.core.component.Component;
import com.game.core.component.Permission;
import com.game.core.component.PluginWrapper;
import com.game.core.utils.Function;
import com.game.core.utils.NotificationCenter;
import com.game.core.utils.ObserverListener;

import java.util.ArrayList;
import java.util.Objects;

public class BaseActivity extends CocosActivity {
    protected ArrayList<Component> mComponents;
    protected Handler mMainThreadHandler;

    protected ObserverListener mObserverListener = (target, eventName, objects) -> {
        Log.d(Constants.TAG, "onMessage " + eventName);
        if (Objects.equals(target.getClass().getSuperclass(), BaseActivity.class)) {
            if (eventName.equals(Constants.SHOW_TOAST)) {
                Log.d(Constants.TAG, "showToast");
                final String msg = (String) objects[0];
                final int duration = 1 < objects.length ? (int) objects[1] : Toast.LENGTH_SHORT;
                showToast(msg, duration);
            }
        }
    };


    public BaseActivity() {
        super();
        mMainThreadHandler = new Handler();
        mComponents = new ArrayList<>();
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

    protected <T extends Component> T addComponent(T component) {
        if (!isExistComponent(component)) {
            component.setActivity(this);
            component.onLoad();
            mComponents.add(component);
        }
        return component;
    }

    protected <T extends Component> T addComponent(String clsName) {
        try {
            return addComponent(Class.forName(clsName));
        } catch (Exception e) {
            System.out.println("addComponent string ->" + e);
        }
        return null;
    }

    protected <T extends Component> T addComponent(Class cls) {
        if (null != cls && (Objects.equals(cls.getSuperclass(), Component.class)
                || Objects.equals(cls.getSuperclass().getSuperclass(), Component.class))
                || Objects.equals(cls.getSuperclass().getSuperclass(), PluginWrapper.class)) {
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
    protected void onResume() {
        super.onResume();
        for (Component component : mComponents)
            component.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (Component component : mComponents)
            component.onPause();
    }

    @Override
    protected void onDestroy() {
        NotificationCenter.getInstance().unregisterAllObserver(this);
        for (Component component : mComponents)
            component.onDestroy();
        mComponents.clear();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Component component : mComponents)
            component.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (Component component : mComponents)
            component.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Workaround in https://stackoverflow.com/questions/16283079/re-launch-of-activity-on-home-button-but-only-the-first-time/16447508
        if (!isTaskRoot()) {
            // Android launched another instance of the root activity into an existing task
            //  so just quietly finish and go away, dropping the user back into the activity
            //  at the top of the stack (ie: the last state of this task)
            // Don't need to finish it again since it's finished in super.onCreate .
            return;
        }
        onLoad();
        // DO OTHER INITIALIZATION BELOW
    }

    protected void onLoad() {
        NotificationCenter.getInstance().registerObserver(Constants.SHOW_TOAST, this.mObserverListener, this);
        addComponent(Permission.class);
//        addComponent(NetworkStatus.class);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


    public void runOnGLThread(Runnable runnable) {
        CocosHelper.runOnGameThread(runnable);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void nativeCallScript(Object... objects) {
        StringBuilder call = new StringBuilder("ccx.eventManager.emit(");
        for (Object obj : objects) {
            if (null == obj) continue;
            if (obj instanceof String) {
                call.append("'").append(obj).append("',");
            } else if (obj.getClass().isArray()) {
                Log.d(Constants.TAG, "Array not support->" + obj);
            } else {
                call.append(obj).append(",");
            }
        }
        call = new StringBuilder(call.substring(0, call.length() - 1) + ")");
        Log.d(Constants.TAG, "nativeCallScript ->[ " + call + " ]");

        final String call_ = call.toString();
        this.runOnGLThread(() -> CocosJavascriptJavaBridge.evalString(call_));
    }

    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public void showToast(String msg) {
        final String msgF = msg;
        runOnMainThread(() -> Toast.makeText(getApplicationContext(), msgF, Toast.LENGTH_SHORT).show());
    }

    public void showToast(String msg, int duration) {
        final String msgF = msg;
        final int durationF = duration;
        runOnMainThread(() -> Toast.makeText(getApplicationContext(), msgF, durationF).show());
    }

    public String getMetaFromApplication(String key) {
        return getMetaFromApplication(key, null);
    }

    public String getMetaFromApplication(String key, String defaultValue) {
        return Function.getMetaFromApplication(this, key, defaultValue);
    }
}
