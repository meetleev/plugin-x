package com.pluginx.core.component;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class PluginError {
    public int code;
    public String errMsg;

    public PluginError(String errMsg) {
        this.code = -1;
        this.errMsg = errMsg;
    }

    public PluginError(int code) {
        this.code = code;
    }

    public PluginError(int code, String errMsg) {
        this.code = code;
        this.errMsg = errMsg;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
