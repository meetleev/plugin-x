package com.game.core.utils;

import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;

public class Function {
    private static PackageItemInfo mApplicationInfo;

    public static String getMetaFromApplication(Context ctx, String key) {
        return getMetaFromApplication(ctx, key, null);
    }

    public static String getMetaFromApplication(Context ctx, String key, String defaultValue) {
        if (null == mApplicationInfo) {
            try {
                mApplicationInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return mApplicationInfo.metaData.getString(key, defaultValue);
    }
}
