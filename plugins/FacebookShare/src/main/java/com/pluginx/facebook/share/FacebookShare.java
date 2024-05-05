package com.pluginx.facebook.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.pluginx.core.base.FunctionHelper;
import com.pluginx.core.base.TaskExecutor;
import com.pluginx.core.component.PluginError;
import com.pluginx.core.component.ShareWrapper;

import java.io.InputStream;
import java.util.concurrent.Callable;

public class FacebookShare extends ShareWrapper {
    private static final String TAG = "FacebookShare";
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    @Override
    protected void initSDK() {
        super.initSDK();
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(getActivity());
        shareDialog.registerCallback(callbackManager, new FacebookCallback<>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(TAG, "onSuccess");
                onShareSucceed(result.getPostId());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
                onShareCanceled();
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.d(TAG, "onError: " + e.getMessage());
                onShareFailed(new PluginError(e.getMessage()));
            }
        });
    }

    @Override
    public void share(String value) {
        super.share(value);
        Log.d(TAG, "share: " + value);
        getParent().runOnMainThread(() -> {
            FunctionHelper functionHelper = getParent().getFunctionHelper();
            ShareInfo shareInfo = ShareInfo.formJson(value);
            int contentType = shareInfo.contentType;
            String url = shareInfo.url;
            if (ShareContentType.Link.ordinal() == contentType) {
                if (null == url || url.isEmpty()) {
                    String errMsg = "url not given!";
                    onShareFailed(new PluginError(errMsg));
                    return;
                }
                if (!functionHelper.isHttpUrl(url)) {
                    String errMsg = "url invalid!";
                    onShareFailed(new PluginError(errMsg));
                    return;
                }
                ShareContent.Builder<ShareLinkContent, ShareLinkContent.Builder> contentBuilder = new ShareLinkContent.Builder().setContentUrl(Uri.parse(url));
                shareByDialog(contentBuilder, shareInfo);
            } else if (ShareContentType.Image.ordinal() == contentType) {
                boolean flag = true;
                while (flag) {
                    if (null == url || url.isEmpty()) {
                        String errMsg = "url not given!";
                        onShareFailed(new PluginError(errMsg));
                        break;
                    }
                    if (functionHelper.isAssetsRes(url)) {
                        Bitmap bitmap = functionHelper.loadAssetsImage(url);
                        shareImage(bitmap, shareInfo);
                        break;
                    }
                    if (functionHelper.isFileInAppPrivateStorage(url)) {
                        Bitmap bitmap = BitmapFactory.decodeFile(url);
                        shareImage(bitmap, shareInfo);
                        break;
                    }
                    if (functionHelper.isHttpUrl(url)) {
                        new TaskExecutor().executeAsync(new DownloadImgTask(url), result -> {
                            shareImage(result, shareInfo);
                        });
                        break;
                    }
                    String errMsg = "url invalid!";
                    onShareFailed(new PluginError(errMsg));
                    flag = false;
                }
            } else if (ShareContentType.Video.ordinal() == contentType) {
                if (null == url || url.isEmpty()) {
                    String errMsg = "url not given!";
                    onShareFailed(new PluginError(errMsg));
                    return;
                }
                ShareVideo.Builder builderVideo = new ShareVideo.Builder();
                builderVideo.setLocalUrl(Uri.parse(url));
                ShareVideoContent.Builder content = new ShareVideoContent.Builder().setVideo(builderVideo.build());
                shareByDialog(content, shareInfo);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void shareImage(Bitmap bitmap, ShareInfo shareInfo) {
        ShareContent.Builder<SharePhotoContent, SharePhotoContent.Builder> contentBuilder = new SharePhotoContent.Builder().addPhoto(new SharePhoto.Builder().setBitmap(bitmap).build());
        shareByDialog(contentBuilder, shareInfo);
    }

    private void shareByDialog(ShareContent.Builder<?, ?> contentBuilder, ShareInfo shareInfo) {
        if (null != contentBuilder) {
            if (null != shareInfo.tag && !shareInfo.tag.isEmpty()) {
                contentBuilder.setShareHashtag(new ShareHashtag.Builder().setHashtag(shareInfo.tag).build());
            }
            shareDialog.show(contentBuilder.build());
        } else {
            String errMsg = "share failed";
            Log.d(TAG, errMsg);
            onShareFailed(new PluginError(errMsg));
        }
    }

    private static class DownloadImgTask implements Callable<Bitmap> {
        private final String url;

        public DownloadImgTask(String url) {
            this.url = url;
        }

        @Override
        public Bitmap call() {
            Bitmap bm = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bm = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bm;
        }
    }
}
