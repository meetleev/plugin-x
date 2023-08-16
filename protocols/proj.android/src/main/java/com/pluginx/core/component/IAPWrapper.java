package com.pluginx.core.component;

import android.util.Log;

import com.pluginx.core.Constants;
import com.pluginx.core.utils.NotificationCenter;
import com.pluginx.core.utils.ObserverListener;
import com.pluginx.core.BuildConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class IAPWrapper extends PluginWrapper {
    public enum ProductState {
        None, Pending, Purchased,
    }

    protected final Set<String> autoConsumeProducts = new HashSet<>();
    protected final Map<String, ProductState> productStateMap = new HashMap<>();
    protected List<String> inAppProducts;
    protected List<String> subscriptionProducts;

    private final static String ON_PAYMENT_RESULT = "onPaymentResult";
    private final static String IN_APP_PRODUCTS = "inAppProducts";
    private final static String AUTO_CONSUME_PRODUCTS = "autoConsumeProducts";
    private final static String SUBSCRIPTION_PRODUCTS = "subscriptionProducts";

    protected ObserverListener mObserverListener = (eventName, objects) -> {
        Log.d(Constants.TAG, "onMessage " + eventName);
        String sdkName = (String) objects[0];
        Log.d(Constants.TAG, "onMessage sdkName " + sdkName + " clsName " + getClass().getSimpleName());
        if (!getClass().getSimpleName().toLowerCase().contains(sdkName.toLowerCase())) return;
        if (Constants.PAYMENT_PRODUCT.equals(eventName)) {
            String productId = (String) objects[1];
            Log.d(Constants.TAG, "payment " + productId);
            paymentWithProductId(productId);
        }
    };

    @Override
    public void onLoad() {
        super.onLoad();
        readPayConfig();
        NotificationCenter.getInstance().registerObserver(Constants.PAYMENT_PRODUCT, mObserverListener, this);
    }


    protected void syncProducts() {

    }

    public void paymentWithProductId(String productId) {
    }

    @Override
    public void configDevInfo(Hashtable<String, String> cpInfo) {
        super.configDevInfo(cpInfo);
        if (cpInfo.contains(IN_APP_PRODUCTS)) {
            inAppProducts = Arrays.asList(Objects.requireNonNull(cpInfo.get(IN_APP_PRODUCTS)).split(","));
        }
        if (cpInfo.contains(SUBSCRIPTION_PRODUCTS)) {
            subscriptionProducts = Arrays.asList(Objects.requireNonNull(cpInfo.get(SUBSCRIPTION_PRODUCTS)).split(","));
        }
        if (null != inAppProducts && !inAppProducts.isEmpty() || null != subscriptionProducts && !subscriptionProducts.isEmpty())
            syncProducts();
    }

    public void readPayConfig() {
        String sAutoConsumeProducts = getParent().getStringMetaFromApp(AUTO_CONSUME_PRODUCTS);
        if (null != sAutoConsumeProducts && !sAutoConsumeProducts.isEmpty()) {
            autoConsumeProducts.addAll(Arrays.asList(sAutoConsumeProducts.split(",")));
        }
        String sInAppProducts = getParent().getStringMetaFromApp(IN_APP_PRODUCTS);
        if (null != sInAppProducts && !sInAppProducts.isEmpty()) {
            inAppProducts = Arrays.asList(sInAppProducts.split(","));
            addProductData(inAppProducts);
        }
        String sSubscriptionProducts = getParent().getStringMetaFromApp(SUBSCRIPTION_PRODUCTS);
        if (null != sSubscriptionProducts && !sSubscriptionProducts.isEmpty()) {
            subscriptionProducts = Arrays.asList(sSubscriptionProducts.split(","));
            addProductData(subscriptionProducts);
        }
        Log.d(Constants.TAG, "readPayConfig " + inAppProducts + " " + subscriptionProducts);
    }

    private void addProductData(List<String> skuList) {
        for (String sku : skuList)
            productStateMap.put(sku, ProductState.None);
    }

    protected void onPaymentResult(PluginStatusCodes statusCodes, PluginError pluginError) {
        getParent().nativeCallScript(ON_PAYMENT_RESULT, statusCodes, pluginError);
        if (BuildConfig.USED_NATIVE)
            onNativePaymentResult(statusCodes.ordinal(), null != pluginError ? pluginError.toString() : null);
    }

    public native void onNativePaymentResult(int statusCodes, String pluginError);
}
