package com.pluginx.google.login;


import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.games.Player;
import com.pluginx.core.base.FunctionHelper;
import com.pluginx.core.component.PluginError;
import com.pluginx.core.component.UserWrapper;


public class GoogleLogin extends UserWrapper {
    //    private static final String META_GP_AUTHENTICATION_SERVER_ID = "GP_AUTHENTICATION_SERVER_ID";
    private static final String TAG = "GoogleLogin";
    private GamesSignInClient gamesSignInClient;

    @Override
    protected void initSDK() {
        super.initSDK();
        PlayGamesSdk.initialize(getActivity());
        gamesSignInClient = PlayGames.getGamesSignInClient(getActivity());
        /*runOnMainThread(() -> gamesSignInClient.isAuthenticated().addOnCompleteListener(task -> {
           AuthenticationResult r = task.getResult();
            boolean isAuthenticated = (task.isSuccessful() && task.getResult().isAuthenticated());
            if (isAuthenticated) {
                // Continue with Play Games Services
                fetchPlayerInfo();
            }
        }));*/
    }

    @Override
    public void signIn() {
        super.signIn();
        Log.d(TAG, "signIn");
        runOnMainThread(() -> gamesSignInClient.isAuthenticated().addOnCompleteListener(task -> {
            boolean isAuthenticated = (task.isSuccessful() && task.getResult().isAuthenticated());

            if (isAuthenticated) {
                // Continue with Play Games Services
                fetchPlayerInfo();
            } else {
                // Disable your integration with Play Games Services or show a
                // login button to ask  players to sign-in. Clicking it should
                gamesSignInClient.signIn().addOnCompleteListener(task1 -> {
                    boolean isAuthenticated2 = (task1.isSuccessful() && task1.getResult().isAuthenticated());
                    if (isAuthenticated2) fetchPlayerInfo();
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "signIn: " + e.getLocalizedMessage() + "---" + e.getMessage());
                    onLoginFailed(new PluginError(e.getMessage()));
                });
            }
        }));
    }

    private void fetchPlayerInfo() {
        PlayGames.getPlayersClient(getActivity()).getCurrentPlayer().addOnCompleteListener(mTask -> {
            // Get PlayerID with mTask.getResult().getPlayerId()
            Player p = mTask.getResult();
            Log.d(TAG, "getPlayerId--" + p);
            Uri iconImageUrl = p.getIconImageUri();
            if (null != iconImageUrl) {
                ImageManager manager = ImageManager.create(getActivity());
                manager.loadImage((uri, drawable, b) -> {
                    FunctionHelper functionHelper = getParent().getFunctionHelper();
                    Bitmap bitmap = functionHelper.drawableToBitmap(drawable);
                    String filePath = functionHelper.saveBitmapToFile(bitmap, p.getPlayerId() + ".jpg");
                    onLoginSucceed(new PluginUserInfo(p.getPlayerId(), p.getDisplayName(), "file://" + filePath));
                }, iconImageUrl);
            } else {
                onLoginSucceed(new PluginUserInfo(p.getPlayerId(), p.getDisplayName(), null));
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "fetchPlayerInfo: " + e.getLocalizedMessage() + "---" + e.getMessage());
            onLoginFailed(new PluginError(e.getMessage()));
        });
    }
}
