package com.game.core.component;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.game.core.base.BaseActivity;


interface ComponentInterface {
    void onLoad();

    void onDestroy();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}

public class Component implements ComponentInterface {
    protected BaseActivity mActivity;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }
}
