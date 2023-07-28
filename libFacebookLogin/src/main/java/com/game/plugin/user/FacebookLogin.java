package com.game.plugin.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.game.core.component.PluginError;
import com.game.core.component.UserWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class FacebookLogin extends UserWrapper {
    private static final String TAG = "FaceBookLogin";
    private CallbackManager callbackManager;

    @Override
    protected void initSDK() {
        super.initSDK();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess");
                fetchPlayerInfo(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
                onLoginCanceled();
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.e(TAG, "onError: " + e.getMessage());
                onLoginFailed(new PluginError(e.getLocalizedMessage()));
            }
        });
    }

    @Override
    public void logIn() {
        super.logIn();
        Log.d(TAG, "logIn");
        getParent().runOnMainThread(() -> {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (null != accessToken && !accessToken.isExpired()) {
                Log.d(TAG, "fetchPlayerInfo form cache token");
                fetchPlayerInfo(accessToken);
            } else {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "email"));
            }
        });
    }

    @Override
    public void logOut() {
        super.logOut();
        getParent().runOnMainThread(() -> LoginManager.getInstance().logOut());
    }

    private void fetchPlayerInfo(AccessToken token) {
        Bundle params = new Bundle();
        params.putString("fields", "picture,name,id,email");
        new GraphRequest(token, "me", params, HttpMethod.GET, graphResponse -> {
            JSONObject jsonObject = graphResponse.getJSONObject();
            Log.d(TAG, "fetchPlayerInfo: " + jsonObject);
            if (null == jsonObject) {
                onLoginFailed(new PluginError("invalid graphResponse"));
                return;
            }
            UserInfo userInfo = new UserInfo();
            if (jsonObject.has("picture")) {
                try {
                    JSONObject o = jsonObject.getJSONObject("picture");
                    JSONObject o2 = o.getJSONObject("data");
                    userInfo.setIconImageUrl(o2.getString("url"));
                } catch (JSONException e) {
                    Log.d(TAG, "got picture err: " + e.getMessage());
                }
            }
            if (jsonObject.has("name")) {
                try {
                    userInfo.setGamerTag(jsonObject.getString("name"));
                } catch (JSONException e) {
                    Log.d(TAG, "got name err: " + e.getMessage());
                }
            }

            if (jsonObject.has("id")) {
                try {
                    userInfo.setId(jsonObject.getString("id"));
                } catch (JSONException e) {
                    Log.d(TAG, "got id err: " + e.getMessage());
                }
            }
            if (jsonObject.has("email")) {
                try {
                    userInfo.setEmail(jsonObject.getString("email"));
                } catch (JSONException e) {
                    Log.d(TAG, "got email err: " + e.getMessage());
                }
            }
            onLoginSucceed(userInfo);
        }).executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
