package com.game.core.component;

public class SDKWrapperComp extends Component {
    @Override
    public void onLoad() {
        super.onLoad();
        this.initSDK();
    }

    protected void initSDK(){}
    public void exitSDK(){}
}
