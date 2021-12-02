package com.game.googlePay;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.game.core.component.IAPWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GooglePay extends IAPWrapper implements BillingClientStateListener, SkuDetailsResponseListener {
    private static final String TAG = "GooglePay";
    private BillingClient billingClient;
    private final Map<String, SkuDetails> skuDetailsMap = new HashMap<>();
    private final Set<Purchase> purchaseConsumptionInProcess = new HashSet<>();
    private boolean billingSetupComplete = false;
    private boolean billingFlowInProcess = false;
    private static final long RECONNECT_TIMER_START_MILLISECONDS = 1000L;
    private static final long RECONNECT_TIMER_MAX_TIME_MILLISECONDS = 1000L * 60L * 15L; // 15 mins
    //    private static final long SKU_DETAILS_REQUERY_TIME = 1000L * 60L * 60L * 4L; // 4 hours
    // how long before the data source tries to reconnect to Google play
    private long reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS;
    // when was the last successful SkuDetailsResponse?
    private final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            Log.d(TAG, "onPurchasesUpdated " + billingResult + " purchases " + (null == purchases));
            if (BillingClient.BillingResponseCode.OK == billingResult.getResponseCode()) {
                if (null != purchases) {
                    processPurchaseList(purchases, null);
                } else {
                    onPaymentResult(PayResult.Fail, billingResult.getDebugMessage());
                    Log.d(TAG, "Null Purchase List Returned from OK response!");
                }
            } else {
                if (BillingClient.BillingResponseCode.USER_CANCELED == billingResult.getResponseCode()) {
                    if (null != purchases)
                        onPaymentResult(PayResult.Cancel, billingResult.getDebugMessage());
                } else {
                    if (null != purchases)
                        onPaymentResult(PayResult.Fail, billingResult.getDebugMessage());
                }
                Log.d(TAG, "BillingResult [" + billingResult.getResponseCode() + "]: "
                        + billingResult.getDebugMessage());
            }
            billingFlowInProcess = false;
        }
    };

    @Override
    protected void initSDK() {
        super.initSDK();
        mActivity.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                // init BillingClient
                billingClient = BillingClient.newBuilder(mActivity).setListener(purchasesUpdatedListener).enablePendingPurchases().build();
                billingClient.startConnection(GooglePay.this);
            }
        });
    }

    /**
     * Retries the billing service connection with exponential backoff, maxing out at the time
     * specified by RECONNECT_TIMER_MAX_TIME_MILLISECONDS.
     */
    private void retryBillingServiceConnectionWithExponentialBackoff() {
        mActivity.runOnMainThread(() ->
                        billingClient.startConnection(this),
                reconnectMilliseconds);
        reconnectMilliseconds = Math.min(reconnectMilliseconds * 2,
                RECONNECT_TIMER_MAX_TIME_MILLISECONDS);
    }

    private void querySkuDetailsAsync() {
        Log.d(TAG, "querySkuDetailsAsync " + inAppSKUs + " " + subscriptionSKUs);
        if (null != inAppSKUs && !inAppSKUs.isEmpty()) {
            billingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder()
                    .setType(BillingClient.SkuType.INAPP)
                    .setSkusList(inAppSKUs)
                    .build(), this);
        }
        if (null != subscriptionSKUs && !subscriptionSKUs.isEmpty()) {
            billingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder()
                    .setType(BillingClient.SkuType.SUBS)
                    .setSkusList(subscriptionSKUs)
                    .build(), this);
        }
    }


    @Override
    public void paymentWithProductId(String productId) {
        super.paymentWithProductId(productId);
        Log.d(TAG, "paymentWithProductId productId: " + productId);
        SkuDetails skuDetails = skuDetailsMap.get(productId);
        if (null != skuDetails) {
            mActivity.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();
                    BillingResult br = billingClient.launchBillingFlow(mActivity, billingFlowParams);
                    if (br.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        billingFlowInProcess = true;
                    } else {
                        Log.e(TAG, "Billing failed: + " + br.getDebugMessage());
                    }
                }
            });
        } else {
            syncSKUs();
            onPaymentResult(PayResult.Fail, "Google Service Timeout!");
            Log.e(TAG, "SkuDetails not found for: " + productId);
        }
    }

    private void processPurchaseList(List<Purchase> purchases, List<String> skusToUpdate) {
        HashSet<String> updatedSkus = new HashSet<>();
        if (null != purchases) {
            for (final Purchase p : purchases) {
                SkuState skuState = SkuState.None;
                for (String sku : p.getSkus()) {
                    skuState = skuStateMap.get(sku);
                    if (null == skuState) {
                        Log.e(TAG, "Unknown SKU " + sku + ". Check to make " +
                                "sure SKU matches SKUS in the Play developer console.");
                        continue;
                    }
                    updatedSkus.add(sku);
                }
                int purchaseState = p.getPurchaseState();
                if (Purchase.PurchaseState.PURCHASED == purchaseState) {
                    if (!isSignatureValid(p)) {
                        Log.e(TAG, "Invalid signature on purchase. Check to make " +
                                "sure your public key is correct.");
                        continue;
                    }
                    setSkuStateFromPurchase(p);
                    boolean isConsumable = false;
                    for (String sku : p.getSkus()) {
                        if (autoConsumeSKUs.contains(sku)) {
                            isConsumable = true;
                        } else {
                            if (isConsumable) {
                                Log.e(TAG, "Purchase cannot contain a mixture of consumable" +
                                        "and non-consumable items: " + p.getSkus().toString());
                                isConsumable = false;
                                break;
                            }
                        }
                    }
                    if (isConsumable) {
                        consumePurchase(p);
                    } else if (!p.isAcknowledged()) {
                        billingClient.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(p.getPurchaseToken())
                                .build(), billingResult -> {
                            if (billingResult.getResponseCode()
                                    == BillingClient.BillingResponseCode.OK) {
                                // purchase acknowledged
                                for (String sku : p.getSkus()) {
                                    setSkuState(sku, SkuState.Purchased);
                                }
                            }
                        });
                    }
                } else {
                    // make sure the state is set
                    setSkuStateFromPurchase(p);
                }
            }
        } else {
            Log.d(TAG, "Empty purchase list.");
        }
        // Clear purchase state of anything that didn't come with this purchase list if this is
        // part of a refresh.
        if (null != skusToUpdate) {
            for (String sku : skusToUpdate) {
                if (!updatedSkus.contains(sku)) {
                    setSkuState(sku, SkuState.None);
                }
            }
        }
    }

    /**
     * Internal call only. Assumes that all signature checks have been completed and the purchase is
     * ready to be consumed. If the sku is already being consumed, does nothing.
     *
     * @param purchase purchase to consume
     */
    private void consumePurchase(@NonNull Purchase purchase) {
        // weak check to make sure we're not already consuming the sku
        if (purchaseConsumptionInProcess.contains(purchase)) {
            // already consuming
            return;
        }
        purchaseConsumptionInProcess.add(purchase);
        billingClient.consumeAsync(ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build(), (billingResult, s) -> {
            // ConsumeResponseListener
            purchaseConsumptionInProcess.remove(purchase);
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Consumption successful. Delivering entitlement.");
                for (String sku : purchase.getSkus()) {
                    // Since we've consumed the purchase
                    setSkuState(sku, SkuState.None);
                    // And this also qualifies as a new purchase
                }
            } else {
                Log.e(TAG, "Error while consuming: " + billingResult.getResponseCode() + " debugMsg " + billingResult.getDebugMessage());
            }
            Log.d(TAG, "End consumption flow.");
        });
    }

    /**
     * Used internally to get purchases from a requested set of SKUs. This is particularly important
     * when changing subscriptions, as onPurchasesUpdated won't update the purchase state of a
     * subscription that has been upgraded from.
     *
     * @param skus    skus to get purchase information for
     * @param skuType sku type, inapp or subscription, to get purchase information for.
     * @return purchases
     */
    private List<Purchase> getPurchases(String[] skus, String skuType) {
        Purchase.PurchasesResult pr = billingClient.queryPurchases(skuType);
        BillingResult br = pr.getBillingResult();
        List<Purchase> returnPurchasesList = new LinkedList<>();
        if (br.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "Problem getting purchases: " + br.getDebugMessage());
        } else {
            List<Purchase> purchasesList = pr.getPurchasesList();
            if (null != purchasesList) {
                for (Purchase purchase : purchasesList) {
                    for (String sku : skus) {
                        for (String purchaseSku : purchase.getSkus()) {
                            if (purchaseSku.equals(sku)) {
                                if (!returnPurchasesList.contains(purchase)) {
                                    returnPurchasesList.add(purchase);
                                }
                            }
                        }
                    }
                }
            }
        }
        return returnPurchasesList;
    }

    /**
     * Consumes an in-app purchase. Interested listeners can watch the purchaseConsumed LiveEvent.
     * To make things easy, you can send in a list of SKUs that are auto-consumed by the
     * BillingDataSource.
     */
    public void consumeInAppPurchase(@NonNull String sku) {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP,
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                        Log.e(TAG, "Problem getting purchases: " +
                                billingResult.getDebugMessage());
                    } else {
                        for (Purchase purchase : list) {
                            // for right now any bundle of SKUs must all be consumable
                            for (String purchaseSku : purchase.getSkus())
                                if (purchaseSku.equals(sku)) {
                                    consumePurchase(purchase);
                                    return;
                                }
                        }
                    }
                    Log.e(TAG, "Unable to consume SKU: " + sku + " Sku not found.");
                });
    }

    private boolean isSignatureValid(Purchase p) {
//        return Security.verifyPurchase(purchase.getOriginalJson(), purchase.getSignature());
        return true;
    }

    /**
     * Calling this means that we have the most up-to-date information for a Sku in a purchase
     * object. This uses the purchase state (Pending, Unspecified, Purchased) along with the
     * acknowledged state.
     *
     * @param purchase an up-to-date object to set the state for the Sku
     */
    private void setSkuStateFromPurchase(@NonNull Purchase purchase) {
        for (String purchaseSku : purchase.getSkus()) {
            SkuState skuState = skuStateMap.get(purchaseSku);
            if (null == skuState) {
                Log.e(TAG, "Unknown SKU " + purchaseSku + ". Check to make " +
                        "sure SKU matches SKUS in the Play developer console.");
            } else {
                switch (purchase.getPurchaseState()) {
                    case Purchase.PurchaseState.PENDING:
                        skuState = SkuState.Pending;
                        break;
                    case Purchase.PurchaseState.UNSPECIFIED_STATE:
                        skuState = SkuState.None;
                        break;
                    case Purchase.PurchaseState.PURCHASED: {
                        if (SkuState.Purchased != skuState) {
                            onPaymentResult(PayResult.Success, billingFlowInProcess ? null : purchase.toString());
                            skuState = SkuState.Purchased;
                        }
                        break;
                    }
                    default:
                        Log.e(TAG, "Purchase in unknown state: " + purchase.getPurchaseState());
                }
                skuStateMap.put(purchaseSku, skuState);
            }
        }
    }

    /**
     * Since we (mostly) are getting sku states when we actually make a purchase or update
     * purchases, we keep some internal state when we do things like acknowledge or consume.
     *
     * @param sku         sku to change the state
     * @param newSkuState the new state of the sku.
     */
    private void setSkuState(@NonNull String sku, SkuState newSkuState) {
        SkuState skuState = skuStateMap.get(sku);
        if (null == skuState) {
            Log.e(TAG, "Unknown SKU " + sku + ". Check to make " +
                    "sure SKU matches SKUS in the Play developer console.");
        } else {
            skuStateMap.put(sku, newSkuState);
        }
    }

    /*
       GPBL v4 now queries purchases asynchronously. This only gets active
       purchases.
    */
    public void refreshPurchasesAsync() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP,
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                        Log.e(TAG, "Problem getting purchases: " +
                                billingResult.getDebugMessage());
                    } else {
                        processPurchaseList(list, inAppSKUs);
                    }
                });
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS,
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                        Log.e(TAG, "Problem getting subscriptions: " +
                                billingResult.getDebugMessage());
                    } else {
                        processPurchaseList(list, subscriptionSKUs);
                    }

                });
        Log.d(TAG, "Refreshing purchases started.");
    }

    /**
     * It's recommended to requery purchases during onResume.
     */
//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Override
    public void onResume() {
//        Log.d(TAG, "ON_RESUME");
        // this just avoids an extra purchase refresh after we finish a billing flow
        if (billingSetupComplete && !billingFlowInProcess) {
            Log.d(TAG, "ON_RESUME refreshPurchasesAsync");
            refreshPurchasesAsync();
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        Log.i(TAG, "billingService Disconnected");
        billingSetupComplete = false;
        retryBillingServiceConnectionWithExponentialBackoff();
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        Log.i(TAG, "Billing Setup Finished responseCode " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
        if (BillingClient.BillingResponseCode.OK == billingResult.getResponseCode()) {
            if (!billingSetupComplete) {
                reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS;
                billingSetupComplete = true;
                querySkuDetailsAsync();
                refreshPurchasesAsync();
            } else {
                Log.d(TAG, "billing already Setup Complete");
            }
        } else {
            retryBillingServiceConnectionWithExponentialBackoff();
        }
    }

    @Override
    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> skuDetailsList) {
        // Process the result.
        if (BillingClient.BillingResponseCode.OK == billingResult.getResponseCode()) {
            if (null != skuDetailsList && !skuDetailsList.isEmpty()) {
                for (SkuDetails sd : skuDetailsList) {
                    if (!skuDetailsMap.containsKey(sd.getSku()))
                        skuDetailsMap.put(sd.getSku(), sd);
                }
                Log.d(TAG, "onSkuDetailsResponse ok");
            } else {
                Log.e(TAG, "onSkuDetailsResponse: Found null or empty SkuDetails. " +
                        "Check to see if the SKUs you requested are correctly published in the Google Play Console.");
            }
        } else {
            Log.e(TAG, "onSkuDetailsResponse err " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
        }
    }

    @Override
    protected void onPaymentResult(PayResult payResult, String msg) {
        Log.d(TAG, "onPaymentResult " + payResult + " " + msg);
        super.onPaymentResult(payResult, msg);
    }

    @Override
    public void syncSKUs() {
        super.syncSKUs();
        querySkuDetailsAsync();
        refreshPurchasesAsync();
    }
}