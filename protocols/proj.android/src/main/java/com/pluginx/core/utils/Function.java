package com.pluginx.core.utils;

import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;

public class Function {
    private static PackageItemInfo mApplicationInfo;

    public static String getStringMetaFromApp(Context ctx, String key) {
        return getStringMetaFromApp(ctx, key, null);
    }

    public static String getStringMetaFromApp(Context ctx, String key, String defaultValue) {
        PackageItemInfo packageItemInfo = getPackageItemInfo(ctx);
        if (null == packageItemInfo) return "";
        return packageItemInfo.metaData.getString(key, defaultValue);
    }

    public static boolean getBooleanMetaFromApp(Context ctx, String key) {
        return getBooleanMetaFromApp(ctx, key, false);
    }

    public static boolean getBooleanMetaFromApp(Context ctx, String key, boolean defaultValue) {
        PackageItemInfo packageItemInfo = getPackageItemInfo(ctx);
        if (null == packageItemInfo) return false;
        return packageItemInfo.metaData.getBoolean(key, defaultValue);
    }

    public static PackageItemInfo getPackageItemInfo(Context ctx) {
        if (null == mApplicationInfo) {
            try {
                mApplicationInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return mApplicationInfo;
    }
}
