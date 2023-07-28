package com.game.plugin.share;

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
import com.game.core.base.TaskExecutor;
import com.game.core.component.PluginError;
import com.game.core.component.ShareWrapper;

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
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
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
            ShareInfo shareInfo = ShareInfo.formJson(value);
            int contentType = shareInfo.contentType;
            String url = shareInfo.url;
            ShareContent.Builder contentBuilder = null;
            if (ShareContentType.Link.ordinal() == contentType) {
                if (null == url || url.isEmpty()) {
                    String errMsg = "url not given!";
                    onShareFailed(new PluginError(errMsg));
                    return;
                }
                contentBuilder = new ShareLinkContent.Builder().setContentUrl(Uri.parse(url));
            } else if (ShareContentType.Image.ordinal() == contentType) {
                SharePhoto.Builder photoBuilder = null;
                boolean canShare = false;
                if (null != url && !url.isEmpty()) {
                    new TaskExecutor().executeAsync(new DownloadImgTask(url), result -> {
                        shareImage(result, shareInfo);
                    });
                    return;
                } else if (null != shareInfo.filePath && !shareInfo.filePath.isEmpty()) {
                    canShare = true;
                    photoBuilder = new SharePhoto.Builder();
                    Bitmap bitmap = BitmapFactory.decodeFile(shareInfo.filePath);
                    photoBuilder.setBitmap(bitmap);
                }
                if (!canShare) {
                    String errMsg = "url and filePath not given!";
                    onShareFailed(new PluginError(errMsg));
                    return;
                }
                contentBuilder = new SharePhotoContent.Builder().addPhoto(photoBuilder.build());

            } else if (ShareContentType.Video.ordinal() == contentType) {
                contentBuilder = new ShareVideoContent.Builder();
                boolean canShare = false;
                if (null != url && !url.isEmpty()) {
                    canShare = true;
                    contentBuilder.setContentUrl(Uri.parse(url));
                }
                ShareVideo.Builder builderVideo;
                if (null != shareInfo.filePath && !shareInfo.filePath.isEmpty()) {
                    canShare = true;
                    builderVideo = new ShareVideo.Builder();
                    builderVideo.setLocalUrl(Uri.parse(shareInfo.filePath));
                }
                if (!canShare) {
                    String errMsg = "url and filePath not given!";
                    onShareFailed(new PluginError(errMsg));
                    return;
                }
            }
            shareByDialog(contentBuilder, shareInfo);
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

    private void shareByDialog(ShareContent.Builder contentBuilder, ShareInfo shareInfo) {
        if (null != contentBuilder) {
            if (null != shareInfo.tag && !shareInfo.tag.isEmpty()) {
                contentBuilder.setShareHashtag(new ShareHashtag.Builder().setHashtag(shareInfo.tag).build());
            }
            shareDialog.show((ShareContent<?, ?>) contentBuilder.build());
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
