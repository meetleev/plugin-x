package com.game.core.component;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.game.core.Constants;

import java.io.File;

public class JumpOtherApk extends Component {

    private DownloadManager mManager;
    private String apkName = "";

    public void jumpApk(String url, String packageName) {
        Log.d(Constants.TAG, "func-jumpOtherApk-url-package: " + url + " -- " + packageName);

        String _apk = url.substring(url.length() - 4, url.length());
        if (_apk.equals(".apk")) { // 如果连接是带有.apk的
            String[] _str = url.split("/");
            apkName = _str[_str.length - 1];
        }

        PackageManager packageManager = mActivity.getPackageManager();
        if (checkPackInfo(packageName)) {
            //存在apk直接打开
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            mActivity.startActivity(intent);
        } else if (checkApkIsExists()) {
            //apk已经下载好PS：未安装
            File apkFile = new File(Environment.getExternalStoragePublicDirectory("/download/"), apkName);
            if (_apk.equals(".apk")) {//如果连接是带有.apk的
                installApk(apkFile);
            }
        } else {
            if (_apk.equals(".apk")) {//如果连接是带有.apk的
                backDownload(url);
            } else {
                CallDefaultBrowser(url);
            }
        }
    }

    /*
     *检查apk是否已经存在文件中并且未安装
     * */
    private boolean checkApkIsExists() {
        try {
            if(apkName.equals(""))
                return false;
            File apkFile = new File(Environment.getExternalStoragePublicDirectory("/download/"), apkName);
            if (!apkFile.exists())
                return false;
        } catch (Exception e) {
            Log.d(Constants.TAG, "func-checkApkIsExists----e: " + e.toString());
        }
        return true;
    }

    /*
     * 检查apk是否已经安装
     * */
    private boolean checkPackInfo(String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mActivity.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /*
     * 后台下载Apk
     * */
    private void backDownload(String url) {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        mManager = (DownloadManager) mActivity.getSystemService(mActivity.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //设置可用的网络类型
        request.setAllowedNetworkTypes(request.NETWORK_MOBILE | request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setDestinationInExternalPublicDir("/download/", apkName);
        //将下载请求放入队列
        long UCID = mManager.enqueue(request);
        //注册监听
        mActivity.registerReceiver(receiver, new IntentFilter(mManager.ACTION_DOWNLOAD_COMPLETE));
        /*} else {
            CallDefaultBrowser(url);
        }*/
    }

    /*
     * 默认浏览器下载
     * */
    private void CallDefaultBrowser(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        //包名、要打开的activity
        //	intent.setClassName("com.pfu.candy", "com.pfu.candy.CandyCrash");
        mActivity.startActivity(intent);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(mManager.ACTION_DOWNLOAD_COMPLETE)) {
                mActivity.unregisterReceiver(receiver);
                File apkFile = new File(Environment.getExternalStoragePublicDirectory("/download/"), apkName);
                String _apk = apkName.substring(apkName.length() - 4, apkName.length());
                if (_apk.equals(".apk")) {//如果连接是带有.apk的
                    installApk(apkFile);
                }
            }
        }
    };

    private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        mActivity.startActivity(intent);
    }
}
