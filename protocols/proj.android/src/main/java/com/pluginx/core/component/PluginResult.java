package com.pluginx.core.component;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class PluginResult {

    public PluginError error;

    public PluginResult() {
    }

    public PluginResult(PluginError error) {
        this.error = error;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
