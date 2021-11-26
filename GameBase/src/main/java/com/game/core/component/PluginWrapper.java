package com.game.core.component;

import com.game.core.utils.NotificationCenter;

import java.util.Hashtable;

public class PluginWrapper extends Component {
    @Override
    public void onLoad() {
        super.onLoad();
        this.initSDK();
    }

    public void configDevInfo(Hashtable<String, String> cpInfo) {
    }


    protected void initSDK() {
    }

    public void exitSDK() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationCenter.getInstance().unregisterAllObserver(this);
    }
}
