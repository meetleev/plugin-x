package com.game.core.component;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.game.core.Constants;
import com.game.core.base.SDKComponent;

import java.io.File;
import java.util.HashMap;

public class NavigateToApp extends Component {
    private DownloadManager mManager;
    private final HashMap<String, Integer> mDownloadManagerStatusMap = new HashMap<>();
    private final HashMap<String, String> mDownloadManagerIdsMap = new HashMap<>();

    public SDKComponent getParent() {
        return (SDKComponent) (parent.get());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        mManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void navigateToApp(String url, String packageName) {
        final String sUrl = url;
        final String sPackageName = packageName;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        SDKComponent root = (SDKComponent) this.getParent();
        Permissions permissionComponent = root.getComponent(Permissions.class);
        if (null != permissionComponent) {
            permissionComponent.hasPermissions(permissionComponent.generateDynamicRequestCode(this), bSuccess -> {
                if (bSuccess) {
                    jumpApk(sUrl, sPackageName);
                } else {
                    root.showToast("需要访问存储空间权限，请授权", Toast.LENGTH_LONG);
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            Log.w(Constants.TAG, "Permission comp is null");
        }
    }

    private void jumpApk(String url, String packageName) {
        Log.d(Constants.TAG, "func-jumpOtherApk-url-package: " + url + " -- " + packageName);
        String apkName = "";
        String _apk = url.substring(url.length() - 4, url.length());
        if (_apk.equals(".apk")) { // 如果连接是带有.apk的
            String[] _str = url.split("/");
            apkName = _str[_str.length - 1];
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (checkPackInfo(packageName)) {
            //存在apk直接打开
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            getActivity().startActivity(intent);
        /*} else if (checkApkIsExists(apkName)) {
            //apk已经下载好PS：未安装
            File apkFile = new File(Environment.getExternalStoragePublicDirectory("/download/"), apkName);
            if (_apk.equals(".apk")) {//如果连接是带有.apk的
                installApk(apkFile);
            }*/
        } else {
            if (_apk.equals(".apk")) {//如果连接是带有.apk的
                boolean bDownload = false;
                if (mDownloadManagerIdsMap.containsKey(apkName)) {
                    String id = mDownloadManagerIdsMap.get(apkName);
                    if (mDownloadManagerStatusMap.containsKey(id)) {
                        if (DownloadManager.STATUS_SUCCESSFUL != mDownloadManagerStatusMap.get(id)) {
                            bDownload = true;
                        }
                    }
                }
                if (!bDownload)
                    backDownload(url, apkName);
                else
                    getParent().showToast("正在下载中...");
            } else {
                CallDefaultBrowser(url);
            }
        }
    }

    /*
     *检查apk是否已经存在文件中并且未安装
     * */
    /*private boolean checkApkIsExists(String apkName) {
        try {
            if (apkName.equals(""))
                return false;
            File apkFile = new File(Environment.getExternalStoragePublicDirectory("/download/"), apkName);
            if (!apkFile.exists())
                return false;
        } catch (Exception e) {
            Log.d(Constants.TAG, "func-checkApkIsExists----e: " + e.toString());
        }
        return true;
    }*/

    /*
     * 检查apk是否已经安装
     * */
    private boolean checkPackInfo(String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(Constants.TAG, "func-checkPackInfo----e: " + e.toString());
        }
        return packageInfo != null;
    }

    /*
     * 后台下载Apk
     * */
    private void backDownload(String url, String apkName) {
        getParent().showToast("开始下载");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 设置可用的网络类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setDestinationInExternalPublicDir("/download/", apkName);
        request.setMimeType("application/vnd.android.package-archive");
        request.setVisibleInDownloadsUi(true);
        // 将下载请求放入队列
        long UCID = mManager.enqueue(request);
        mDownloadManagerIdsMap.put(apkName, UCID + "");
        mDownloadManagerStatusMap.put(UCID + "", DownloadManager.STATUS_PENDING);
        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /*
     * 默认浏览器下载
     * */
    private void CallDefaultBrowser(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        getActivity().startActivity(intent);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            query.setFilterById(id);
            Cursor c = mManager.query(query);
            if (c.moveToFirst()) {
                int temp = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (0 > temp) temp = 0;
                int status = c.getInt(temp);
                mDownloadManagerStatusMap.put(id + "", status);
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                    case DownloadManager.STATUS_PENDING:
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL: {
                        installAPK(getActivity(), mManager.getUriForDownloadedFile(id));
                        mDownloadManagerStatusMap.remove(id + "");
                        break;
                    }
                    case DownloadManager.STATUS_FAILED:
                        // 清除下载并稍后重试
                        mManager.remove(id);
                        break;
                }
            }
        }
    };

    public void installAPK(Context context, Uri uri) {
        if (null == uri) return;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        File file = new File(data);
        if (file.exists()) {
            Intent var2 = new Intent();
            var2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            var2.setAction("android.intent.action.VIEW");
            var2.addCategory("android.intent.category.DEFAULT");
            var2.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uriForFile = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
                var2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                var2.setDataAndType(uriForFile, getActivity().getContentResolver().getType(uriForFile));
            } else {
                var2.setDataAndType(Uri.fromFile(file), getMIMEType(file));
            }
            try {
                getActivity().startActivity(var2);
            } catch (Exception var5) {
                var5.printStackTrace();
                Toast.makeText(getActivity(), "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getMIMEType(File var0) {
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
    }

    /*private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        mActivity.startActivity(intent);
    }*/

    /*public static boolean isHttpUrl(String urls) {
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";

        Pattern pat = Pattern.compile(regex.trim());//对比
        Matcher mat = pat.matcher(urls.trim());
        return mat.matches();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
