package com.pluginx.core.component;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;


import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

interface IComponent {
    void onLoad();

    void onDestroy();

    void onPause();

    void onResume();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);


    void onNewIntent(Intent intent);

    void onRestart();

    void onStop();


    boolean onKeyDown(int keyCode, KeyEvent event);

    void onConfigurationChanged(Configuration newConfig);

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onSaveInstanceState(Bundle outState);

    void onStart();

    void onLowMemory();

    void onBackPressed();
}

public class Component implements IComponent {
    protected WeakReference<Component> parent;
    protected WeakReference<Activity> mActivity;
    protected boolean bDebug = false;

    public void setDebug(boolean bDebug) {
        this.bDebug = bDebug;
    }

    public boolean getDebug() {
        return bDebug;
    }

    public Component() {
    }

    public Component(Activity activity) {
        this.mActivity = new WeakReference<>(activity);
    }

    public void setActivity(Activity activity) {
        this.mActivity = new WeakReference<>(activity);
    }

    public Activity getActivity() {
        return this.mActivity.get();
    }

    public Component getParent() {
        return parent.get();
    }

    public void setParent(Component parent) {
        this.parent = new WeakReference<>(parent);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onBackPressed() {

    }
}
