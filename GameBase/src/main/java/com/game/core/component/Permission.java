package com.game.core.component;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class Permission extends Component {
    private SparseArray<RequestPermissionCallback> permissionCallbackArray = new SparseArray<>();

    /**
     * 申请成功的回调接口
     */
    public interface RequestPermissionCallback {
        void onRequestCallback(boolean bSuccess);
    }

    public int generateDynamicRequestCode(Object object) {
        return object.getClass().hashCode() >> 1;
    }

    private String[] findDeniedPermissions(String... permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this.getActivity(), perm) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionList.add(perm);
            }
        }
        return permissionList.toArray(new String[permissionList.size()]);
    }

    public void hasPermissions(int requestCode, RequestPermissionCallback callback, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.permissionCallbackArray.put(requestCode, callback);
            String[] deniedPermissions = findDeniedPermissions(permissions);
            if (deniedPermissions.length > 0) {
                ActivityCompat.requestPermissions(this.getActivity(), permissions, requestCode);
            } else {
                if (null != callback)
                    callback.onRequestCallback(true);
            }
        } else {
            if (null != callback)
                callback.onRequestCallback(true);
        }
    }

    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults)
            if (PackageManager.PERMISSION_GRANTED != result)
                return false;
        return true;
    }

    /**
     * 当被用户拒绝授权并且出现不再提示时
     * shouldShowRequestPermissionRationale也会返回false，若实在必须申请权限时可以使用方法检测，
     *
     * @param permissions
     * @return
     */
    protected boolean verifyShouldShowRequestPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), permission)) {
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean grandResult = verifyPermissions(grantResults);
        RequestPermissionCallback callback = this.permissionCallbackArray.get(requestCode);
        if (null != callback) {
            if (grandResult) {
                callback.onRequestCallback(true);
            } else {
                if (this.verifyShouldShowRequestPermissions(permissions)) {
                    callback.onRequestCallback(false);
                } else {
                    callback.onRequestCallback(false);
                }
            }
        }
    }
}
