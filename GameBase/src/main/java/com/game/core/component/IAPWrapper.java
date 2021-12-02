package com.game.core.component;

import android.util.Log;

import com.game.core.Constants;
import com.game.core.utils.NotificationCenter;
import com.game.core.utils.ObserverListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IAPWrapper extends PluginWrapper {
    public enum SkuState {
        None,
        Pending,
        Purchased,
    }

    public enum PayResult {
        Success,
        Fail,
        Cancel,
    }

    protected final Set<String> autoConsumeSKUs = new HashSet<>();
    protected final Map<String, SkuState> skuStateMap = new HashMap<>();
    protected List<String> inAppSKUs;
    protected List<String> subscriptionSKUs;

    private final static String ON_PAYMENT_RESULT = "onPaymentResult";
    private final static String IN_APP_SKUS = "inAppSKUs";
    private final static String AUTO_CONSUME_SKUS = "autoConsumeSKUs";
    private final static String SUBSCRIPTION_SKUS = "subscriptionSKUs";

    protected ObserverListener mObserverListener = new ObserverListener() {
        @Override
        public void onMessage(Object target, String eventName, Object... objects) {
            Log.d(Constants.TAG, "onMessage " + eventName);
            if (target.getClass().getSuperclass().equals(IAPWrapper.class)) {
                String sdkName = (String) objects[0];
                Log.d(Constants.TAG, "onMessage sdkName " + sdkName + " clsName " + target.getClass().getSimpleName());
                if (!target.getClass().getSimpleName().toLowerCase().contains(sdkName.toLowerCase()))
                    return;
                if (Constants.PAYMENT_PRODUCT.equals(eventName)) {
                    String productId = (String) objects[1];
                    Log.d(Constants.TAG, "payment " + productId);
                    paymentWithProductId(productId);
                }
            }
        }
    };

    @Override
    public void onLoad() {
        super.onLoad();
        readPayConfig();
        ;
        NotificationCenter.getInstance().registerObserver(Constants.PAYMENT_PRODUCT, mObserverListener, this);
    }


    protected void syncSKUs() {

    }

    public void paymentWithProductId(String productId) {
    }

    @Override
    public void configDevInfo(Hashtable<String, String> cpInfo) {
        super.configDevInfo(cpInfo);
        if (cpInfo.contains(IN_APP_SKUS)) {
            inAppSKUs = Arrays.asList(cpInfo.get(IN_APP_SKUS).split(","));
        }
        if (cpInfo.contains(SUBSCRIPTION_SKUS)) {
            subscriptionSKUs = Arrays.asList(cpInfo.get(SUBSCRIPTION_SKUS).split(","));
        }
        if (null != inAppSKUs && !inAppSKUs.isEmpty() || null != subscriptionSKUs && !subscriptionSKUs.isEmpty())
            syncSKUs();
    }

    public void readPayConfig() {
        String sAutoConsumeSKUS = mActivity.getMetaFromApplication(AUTO_CONSUME_SKUS);
        if (null != sAutoConsumeSKUS && !sAutoConsumeSKUS.isEmpty()) {
            autoConsumeSKUs.addAll(Arrays.asList(sAutoConsumeSKUS.split(",")));
        }
        String sInAppSKUs = mActivity.getMetaFromApplication(IN_APP_SKUS);
        if (null != sInAppSKUs && !sInAppSKUs.isEmpty()) {
            inAppSKUs = Arrays.asList(sInAppSKUs.split(","));
            addSkuData(inAppSKUs);
        }
        String sSubscriptionSKUs = mActivity.getMetaFromApplication(SUBSCRIPTION_SKUS);
        if (null != sSubscriptionSKUs && !sSubscriptionSKUs.isEmpty()) {
            subscriptionSKUs = Arrays.asList(sSubscriptionSKUs.split(","));
            addSkuData(subscriptionSKUs);
        }
        Log.d(Constants.TAG, "readPayConfig " + inAppSKUs + " " + subscriptionSKUs);
    }

    private void addSkuData(List<String> skuList) {
        for (String sku : skuList)
            skuStateMap.put(sku, SkuState.None);
    }

    protected void onPaymentResult(PayResult payResult, String msg) {
        mActivity.nativeCallScript(ON_PAYMENT_RESULT, payResult.ordinal(), msg);
    }
}
