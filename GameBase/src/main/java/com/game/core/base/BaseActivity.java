package com.game.core.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

import com.game.core.Constants;
import com.game.core.component.AdsWrapper;
import com.game.core.component.Component;
import com.game.core.component.PermissionComponent;
import com.game.core.component.SDKWrapperComp;
import com.game.core.utils.NotificationCenter;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;

import java.util.ArrayList;

public class BaseActivity extends Cocos2dxActivity {
    protected static ArrayList<Component> mComponents;
    protected static Handler mMainThreadHandler;

    public BaseActivity() {
        super();
        mMainThreadHandler = new Handler();
        mComponents = new ArrayList<>();
		addComponent(PermissionComponent.class);
    }

    @Override
    public Cocos2dxGLSurfaceView onCreateView() {
        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
        // TestCpp should create stencil buffer
        glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);

        return glSurfaceView;
    }

//    @Override
//    public void finish() {
//        super.finish();
//    }

    public void runOnMainThread(Runnable r) {
        if (null != mMainThreadHandler) {
            mMainThreadHandler.post(r);
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
        if (null != cls && (cls.getSuperclass().equals(Component.class) || cls.getSuperclass().equals(SDKWrapperComp.class)  || cls.getSuperclass().equals(AdsWrapper.class))) {
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
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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


    @Override
    public void runOnGLThread(Runnable runnable) {
        super.runOnGLThread(runnable);
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

    public void nativeCallJS(Object... objects) {
        String call = "AndroidCallJS(";
        for (Object obj : objects) {
            if (obj instanceof String) {
                call += "'" + obj + "',";
            } else if (obj.getClass().isArray()) {
                Log.d(Constants.TAG, "Array not support->" + obj);
            } else {
                call += obj + ",";
            }
        }
        call = call.substring(0, call.length() - 1) + ")";
        Log.d(Constants.TAG, "nativeCallJS->" + call);

        final String call_ = call;
        this.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString(call_);
            }
        });

    }

    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}
