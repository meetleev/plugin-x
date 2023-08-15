package com.pluginx.core.component;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.pluginx.core.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NetworkStatus extends Component {
    public interface NetWorkStateResultListener {
        void onResult(int state);
    }

    private Handler mHandler;
    private NetWorkStateResultListener mNetWorkStateResultListener;
    private int iLastNum = 0;
    private int iCurNum = 0;
    private checkProgressThread checkProgressThread = null;
    private checkNetworkThread checkNetworkThread = null;

    private final int NETWORK_SUCCESS = 10000;//网络连接成功
    private final int NETWORK_FAILED = 10001;//网络连接异常
    private final int PROGRESSBAR_STOP = 10002;//进度条停止

    @Override
    public void onLoad() {
        super.onLoad();
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NETWORK_SUCCESS:
                        if (null != mNetWorkStateResultListener)
                            mNetWorkStateResultListener.onResult(NETWORK_SUCCESS);
                        break;
                    case NETWORK_FAILED:
                        showNetworkState(1);
                        break;
                    case PROGRESSBAR_STOP:
                        showNetworkState(2);
                        break;
                }
                super.handleMessage(msg);
            }
        };
//        this.startThread();
    }

    public int getNetWorkType() {
        boolean bHasNetWork = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            bHasNetWork = isHasNetWork();
        else
            bHasNetWork = isNetworkConnected() ; //|| !ping();
        Log.d(Constants.TAG, "getNetWorkType bHasNetWork...->" + bHasNetWork);
        return bHasNetWork ? 1 : 0;
    }

    private void startThread() {
        checkNetworkThread = new checkNetworkThread();
        checkNetworkThread.start();
    }

    public void addNetWorkStateResultListener(NetWorkStateResultListener pNetWorkStateResultListener) {
        this.mNetWorkStateResultListener = pNetWorkStateResultListener;
    }

    public void checkProgressBar(int num) {
        iCurNum = num;
        if (num == 101) {
            checkProgressThread.tSuspend();
        }
        if (checkProgressThread == null) {
            checkProgressThread = new checkProgressThread();
            checkProgressThread.start();
        }
    }

    class checkNetworkThread extends Thread {
        private final int STOP = -1;
        private final int SUSPEND = 0;
        private final int RUNNING = 1;
        private int status = 1;

        /**
         * 恢复
         */
        public synchronized void tResume() {
            // 修改状态
            status = RUNNING;
            // 唤醒
            notifyAll();
        }

        /**
         * 挂起
         */
        public void tSuspend() {
            // 修改状态
            status = SUSPEND;
//            Log.d(Constants.TAG,"挂起111111...status = "+status);
        }

        /**
         * 停止
         */
        public void tStop() {
            // 修改状态
            status = STOP;
        }

        @Override
        public synchronized void run() {
            // 判断是否停止
            while (status != STOP) {
                // 判断是否挂起
                if (status == SUSPEND) {
                    try {
                        // 若线程挂起则阻塞自己
                        wait();
                    } catch (InterruptedException e) {
                    }
                } else {

                    try {
                        checkNetWork();
                        Thread.sleep(1000L * 10);
                    } catch (InterruptedException e) {
                        Log.d(Constants.TAG, "线程异常终止...");
                    }
                }
            }
        }
    }

    class checkProgressThread extends Thread {
        private final int STOP = -1;
        private final int SUSPEND = 0;
        private final int RUNNING = 1;
        private int status = 1;

        /**
         * 恢复
         */
        public synchronized void tResume() {
            // 修改状态
            status = RUNNING;
            // 唤醒
            notifyAll();
        }

        /**
         * 挂起
         */
        public void tSuspend() {
            // 修改状态
            status = SUSPEND;
//            Log.d(Constants.TAG,"checkProgressThread挂起111111...status = "+status);
        }

        /**
         * 停止
         */
        public void tStop() {
            // 修改状态
            status = STOP;
        }

        @Override
        public synchronized void run() {
            // 判断是否停止
            while (status != STOP) {
                // 判断是否挂起
                if (status == SUSPEND) {
                    try {
                        // 若线程挂起则阻塞自己
                        wait();
                    } catch (InterruptedException e) {
                    }
                } else {

                    try {
                        Thread.sleep(1000L * 5);
                        checkProgress();
                    } catch (InterruptedException e) {
//                        System.out.println("线程异常终止...");
                        Log.d(Constants.TAG, "线程异常终止...");
                    }
                }
            }
        }
    }

    private void checkProgress() {
        if (iCurNum == iLastNum) {
            //长时间进度不变，网络异常，重载webview
//            Log.d(Constants.TAG,"网络连接异常!!!");
            checkProgressThread.tSuspend();
            mHandler.sendEmptyMessage(PROGRESSBAR_STOP);
        }  //
    }


    public int iNetworkState = 0;

    private void checkNetWork() {
        boolean bHasNetWork = !isNetworkConnected() || !isMobileConnected() || !isWifiConnected() || !ping();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            bHasNetWork = isHasNetWork();
        if (!bHasNetWork) {
//            Log.d(Constants.TAG,"网络连接异常,请调整网络！");
            checkNetworkThread.tSuspend();
            iNetworkState = 1;
            mHandler.sendEmptyMessage(NETWORK_FAILED);
        } else {
//            Log.d(Constants.TAG,"网络连接成功！");
            if (iCurNum != 101) {
                if (iNetworkState != 2) {
                    mHandler.sendEmptyMessage(NETWORK_SUCCESS);
                    iNetworkState = 2;
                }
            }

        }
    }

    // 判断是否有网络连接
    private boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mConnectivityManager = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    // 判断WIFI网络是否可用
    private boolean isWifiConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWiFiNetworkInfo != null) {
            return mWiFiNetworkInfo.isAvailable();
        }
        return false;
    }

    // 判断MOBILE网络是否可用
    private boolean isMobileConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobileNetworkInfo = mConnectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobileNetworkInfo != null) {
            return mMobileNetworkInfo.isAvailable();
        }
        return false;
    }

    @TargetApi(23)
    private boolean isHasNetWork() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = mConnectivityManager.getNetworkCapabilities(mConnectivityManager.getActiveNetwork());
        Log.d(Constants.TAG, "networkCapabilities content : " + networkCapabilities);
        if (null != networkCapabilities)
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        return false;
    }

    // 判断是否有外网连接
    /* @author suncat
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     * @return
     */
    private boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuilder stringBuffer = new StringBuilder();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d(Constants.TAG, "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d(Constants.TAG, "result = " + result);
        }
        return false;
    }

    private int iType = 0;

    private void showNetworkState(int n) {
        iType = n;
//        Log.d(Constants.TAG,"显示网络状态提示框!!!n: "+ n);
        new AlertDialog.Builder(getActivity())
                .setTitle("")
                .setCancelable(false)
                .setMessage("当前网络不可以，请尝试重新连接!")
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    if (iType == 1) {
                        checkNetworkThread.tResume();
                    } else {
                        checkProgressThread.tResume();
                    }
                })
                .setNegativeButton("退出", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    getActivity().finish();
                    System.exit(0);
                }).show();
    }

}
