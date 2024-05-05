package com.pluginx.core.base;

import android.content.Context;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionHelper {
    private PackageItemInfo mApplicationInfo;
    private WeakReference<Context> mContext = null;

    public Context getContext() {
        return mContext.get();
    }

    private FunctionHelper() {
    }

    public FunctionHelper(Context context) {
        mContext = new WeakReference<>(context);
    }

    public String getStringMetaFromApp(String key) {
        return getStringMetaFromApp(key, null);
    }

    public String getStringMetaFromApp(String key, String defaultValue) {
        PackageItemInfo packageItemInfo = getPackageItemInfo();
        if (null == packageItemInfo) return "";
        return packageItemInfo.metaData.getString(key, defaultValue);
    }

    public boolean getBooleanMetaFromApp(Context ctx, String key) {
        return getBooleanMetaFromApp(key, false);
    }

    public boolean getBooleanMetaFromApp(String key, boolean defaultValue) {
        PackageItemInfo packageItemInfo = getPackageItemInfo();
        if (null == packageItemInfo) return false;
        return packageItemInfo.metaData.getBoolean(key, defaultValue);
    }

    public PackageItemInfo getPackageItemInfo() {
        Context ctx = getContext();
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

    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public String saveBitmapToFile(Bitmap bitmap, String fileName) {
        File filesDir = getContext().getFilesDir();
        File file = new File(filesDir.getPath(), fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isHttpUrl(String url) {
        // 使用正则表达式匹配 HTTP URL
        String httpPattern = "^(http://|https://).*";
        Pattern pattern = Pattern.compile(httpPattern);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    public boolean isAssetsRes(String url) {
        return url.startsWith("assets");
    }


    public Bitmap loadAssetsImage(String url) {
        if (isAssetsRes(url)) {
            AssetManager assetManager = getContext().getAssets();
            try {
                InputStream inputStream = assetManager.open(url);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 判断文件是否位于应用的私有存储路径下
    public boolean isFileInAppPrivateStorage(String filePath) {
        // 获取应用的私有存储路径
        String appPrivateStoragePath = getContext().getFilesDir().getAbsolutePath();
        // 判断文件路径是否以应用的私有存储路径开头
        return filePath.startsWith(appPrivateStoragePath);
    }
}
