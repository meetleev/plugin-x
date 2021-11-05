package com.game.core.component;

import android.content.Intent;


import androidx.annotation.NonNull;

import com.game.core.base.BaseActivity;

interface IComponent {
    void onLoad();

    void onDestroy();

    void onPause();

    void onResume();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}

public class Component implements IComponent {
    protected BaseActivity mActivity;
    protected boolean bDebug = false;

    public void setDebug(boolean bDebug) {
        this.bDebug = bDebug;
    }

    public boolean getDebug() {
        return bDebug;
    }

    public Component() {
    }

    public Component(BaseActivity activity) {
        this.mActivity = activity;
    }

    public void setActivity(BaseActivity activity) {
        this.mActivity = activity;
    }

    public BaseActivity getActivity() {
        return this.mActivity;
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
}
