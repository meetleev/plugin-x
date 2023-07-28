package com.game.googlePay;

import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.game.core.component.IAPWrapper;
import com.game.core.component.PluginError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;

import static com.android.billingclient.api.BillingClient.FeatureType.PRODUCT_DETAILS;

public class GooglePay extends IAPWrapper implements BillingClientStateListener, ProductDetailsResponseListener {
    private static final String TAG = "GooglePay";
    private BillingClient billingClient;
    private final Map<String, ProductDetails> productDetailsMap = new HashMap<>();
    private final Set<Purchase> purchaseConsumptionInProcess = new HashSet<>();
    private final List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
    private PluginError connectionError;
    private boolean billingFlowInProcess = false;
    private static final long RECONNECT_TIMER_START_MILLISECONDS = 1000L;
    private static final long RECONNECT_TIMER_MAX_TIME_MILLISECONDS = 1000L * 60L * 15L; // 15 mins
    //    private static final long PRODUCT_DETAILS_REQUERY_TIME = 1000L * 60L * 60L * 4L; // 4 hours
    // how long before the data source tries to reconnect to Google play
    private long reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS;
    private final PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
        // To be implemented in a later section.
        Log.d(TAG, "onPurchasesUpdated " + billingResult + " purchases " + (null == purchases));
        int code = billingResult.getResponseCode();
        if (BillingClient.BillingResponseCode.OK == code) {
            if (null != purchases) {
                processPurchaseList(purchases, null);
            } else {
                onPaymentResult(PayResult.Fail, new PluginError(code, billingResult.getDebugMessage()));
                Log.d(TAG, "Null Purchase List Returned from OK response!");
            }
        } else {
            if (BillingClient.BillingResponseCode.USER_CANCELED == billingResult.getResponseCode()) {
                if (null != purchases)
                    onPaymentResult(PayResult.Cancel, new PluginError(code, billingResult.getDebugMessage()));
            } else {
                if (null != purchases)
                    onPaymentResult(PayResult.Fail, new PluginError(code, billingResult.getDebugMessage()));
            }
            Log.d(TAG, "BillingResult [" + billingResult.getResponseCode() + "]: " + billingResult.getDebugMessage());
        }
        billingFlowInProcess = false;
    };

    @Override
    protected void initSDK() {
        super.initSDK();
        mActivity.runOnMainThread(() -> {
            // init BillingClient
            billingClient = BillingClient.newBuilder(mActivity).setListener(purchasesUpdatedListener).enablePendingPurchases().build();
            billingClient.startConnection(this);
        });
    }

    /**
     * Retries the billing service connection with exponential backoff, maxing out at the time
     * specified by RECONNECT_TIMER_MAX_TIME_MILLISECONDS.
     */
    private void retryBillingServiceConnection() {
        connectionError = null;
        mActivity.runOnMainThread(() -> billingClient.startConnection(this), reconnectMilliseconds);
        reconnectMilliseconds = Math.min(reconnectMilliseconds * 2, RECONNECT_TIMER_MAX_TIME_MILLISECONDS);
    }

    private void queryProductsDetailsAsync() {
        Log.d(TAG, "queryProductsDetailsAsync " + inAppProducts + " " + subscriptionProducts);
        if (BillingClient.BillingResponseCode.OK == billingClient.isFeatureSupported(PRODUCT_DETAILS).getResponseCode()) {
            if (productList.isEmpty()) {
                if (null != inAppProducts && !inAppProducts.isEmpty()) {
                    for (String k : inAppProducts) {
                        QueryProductDetailsParams.Product p = QueryProductDetailsParams.Product.newBuilder().setProductType(BillingClient.ProductType.INAPP).setProductId(k).build();
                        productList.add(p);
                    }
                }
                if (null != subscriptionProducts && !subscriptionProducts.isEmpty()) {
                    for (String k : subscriptionProducts) {
                        QueryProductDetailsParams.Product p = QueryProductDetailsParams.Product.newBuilder().setProductType(BillingClient.ProductType.SUBS).setProductId(k).build();
                        productList.add(p);
                    }
                }
            }
            if (!productList.isEmpty())
                billingClient.queryProductDetailsAsync(QueryProductDetailsParams.newBuilder().setProductList(productList).build(), this);
        } else {
            Log.d(TAG, "Google Play service too low");
        }
    }


    @Override
    public void paymentWithProductId(String productId) {
        super.paymentWithProductId(productId);
        Log.d(TAG, "paymentWithProductId productId: " + productId);
        if (null != connectionError) {
            onPaymentResult(PayResult.Fail, connectionError);
        } else {
            ProductDetails productDetails = productDetailsMap.get(productId);
            if (null != productDetails) {
                mActivity.runOnMainThread(() -> {
                    // Retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                    // Get the offerToken of the selected offer
                    /*String offerToken = productDetails
                        .getSubscriptionOfferDetails()
                        .get(selectedOfferIndex)
                        .getOfferToken();*/
                    List<BillingFlowParams.ProductDetailsParams> productDetailsParams = new ArrayList<>();
                    productDetailsParams.add(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetails).build());
                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParams).setIsOfferPersonalized(true).build();
                    BillingResult br = billingClient.launchBillingFlow(mActivity, billingFlowParams);
                    if (br.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        billingFlowInProcess = true;
                    } else {
                        Log.e(TAG, "Billing failed: + " + br.getDebugMessage());
                    }
                });
            }
        }
    }

    private void processPurchaseList(List<Purchase> purchases, List<String> productsToUpdate) {
        HashSet<String> updatedProducts = new HashSet<>();
        if (null != purchases) {
            for (final Purchase p : purchases) {
                ProductState productState;
                for (String pd : p.getProducts()) {
                    productState = productStateMap.get(pd);
                    if (null == productState) {
                        Log.e(TAG, "Unknown product " + pd + ". Check to make " + "sure product matches products in the Play developer console.");
                        continue;
                    }
                    updatedProducts.add(pd);
                }
                int purchaseState = p.getPurchaseState();
                if (Purchase.PurchaseState.PURCHASED == purchaseState) {
                    if (!isSignatureValid(p)) {
                        Log.e(TAG, "Invalid signature on purchase. Check to make " + "sure your public key is correct.");
                        continue;
                    }
                    setProductStateFromPurchase(p);
                    boolean isConsumable = false;
                    for (String pd : p.getProducts()) {
                        if (autoConsumeProducts.contains(pd)) {
                            isConsumable = true;
                        } else {
                            if (isConsumable) {
                                Log.e(TAG, "Purchase cannot contain a mixture of consumable" + "and non-consumable items: " + p.getProducts());
                                isConsumable = false;
                                break;
                            }
                        }
                    }
                    if (isConsumable) {
                        consumePurchase(p);
                    } else if (!p.isAcknowledged()) {
                        billingClient.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(p.getPurchaseToken()).build(), billingResult -> {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                // purchase acknowledged
                                for (String pd : p.getProducts()) {
                                    setProductState(pd, ProductState.Purchased);
                                }
                            }
                        });
                    }
                } else {
                    // make sure the state is set
                    setProductStateFromPurchase(p);
                }
            }
        } else {
            Log.d(TAG, "Empty purchase list.");
        }
        // Clear purchase state of anything that didn't come with this purchase list if this is
        // part of a refresh.
        if (null != productsToUpdate) {
            for (String pd : productsToUpdate) {
                if (!updatedProducts.contains(pd)) {
                    setProductState(pd, ProductState.None);
                }
            }
        }
    }

    /**
     * Internal call only. Assumes that all signature checks have been completed and the purchase is
     * ready to be consumed. If the product is already being consumed, does nothing.
     *
     * @param purchase purchase to consume
     */
    private void consumePurchase(@NonNull Purchase purchase) {
        // weak check to make sure we're not already consuming the product
        if (purchaseConsumptionInProcess.contains(purchase)) {
            // already consuming
            return;
        }
        purchaseConsumptionInProcess.add(purchase);
        billingClient.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), (billingResult, s) -> {
            // ConsumeResponseListener
            purchaseConsumptionInProcess.remove(purchase);
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Consumption successful. Delivering entitlement.");
                for (String pd : purchase.getProducts()) {
                    // Since we've consumed the purchase
                    setProductState(pd, ProductState.None);
                    // And this also qualifies as a new purchase
                }
            } else {
                Log.e(TAG, "Error while consuming: " + billingResult.getResponseCode() + " debugMsg " + billingResult.getDebugMessage());
            }
            Log.d(TAG, "End consumption flow.");
        });
    }

    /**
     * Consumes an in-app purchase. Interested listeners can watch the purchaseConsumed LiveEvent.
     * To make things easy, you can send in a list of products that are auto-consumed by the
     * BillingDataSource.
     */
    public void consumeInAppPurchase(@NonNull String product) {
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(), (billingResult, list) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Problem getting purchases: " + billingResult.getDebugMessage());
            } else {
                for (Purchase purchase : list) {
                    // for right now any bundle of products must all be consumable
                    for (String purchaseProduct : purchase.getProducts())
                        if (purchaseProduct.equals(product)) {
                            consumePurchase(purchase);
                            return;
                        }
                }
            }
            Log.e(TAG, "Unable to consume product: " + product + " product not found.");
        });
    }

    private boolean isSignatureValid(Purchase p) {
//        return Security.verifyPurchase(purchase.getOriginalJson(), purchase.getSignature());
        return true;
    }

    /**
     * Calling this means that we have the most up-to-date information for a product in a purchase
     * object. This uses the purchase state (Pending, Unspecified, Purchased) along with the
     * acknowledged state.
     *
     * @param purchase an up-to-date object to set the state for the product
     */
    private void setProductStateFromPurchase(@NonNull Purchase purchase) {
        for (String pd : purchase.getProducts()) {
            ProductState productState = productStateMap.get(pd);
            if (null == productState) {
                Log.e(TAG, "Unknown product " + pd + ". Check to make " + "sure product matches products in the Play developer console.");
            } else {
                switch (purchase.getPurchaseState()) {
                    case Purchase.PurchaseState.PENDING:
                        productState = ProductState.Pending;
                        break;
                    case Purchase.PurchaseState.UNSPECIFIED_STATE:
                        productState = ProductState.None;
                        break;
                    case Purchase.PurchaseState.PURCHASED: {
                        if (ProductState.Purchased != productState) {
                            onPaymentResult(PayResult.Success, null);
                            productState = ProductState.Purchased;
                        }
                        break;
                    }
                    default:
                        Log.e(TAG, "Purchase in unknown state: " + purchase.getPurchaseState());
                }
                productStateMap.put(pd, productState);
            }
        }
    }

    /**
     * Since we (mostly) are getting product states when we actually make a purchase or update
     * purchases, we keep some internal state when we do things like acknowledge or consume.
     *
     * @param product  product to change the state
     * @param newState the new state of the product.
     */
    private void setProductState(@NonNull String product, ProductState newState) {
        ProductState productState = productStateMap.get(product);
        if (null == productState) {
            Log.e(TAG, "Unknown product " + product + ". Check to make " + "sure product matches products in the Play developer console.");
        } else {
            productStateMap.put(product, newState);
        }
    }

    /*
       GPBL v4 now queries purchases asynchronously. This only gets active
       purchases.
    */
    public void refreshPurchasesAsync() {
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(), (billingResult, list) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Problem getting purchases: " + billingResult.getDebugMessage());
            } else {
                processPurchaseList(list, inAppProducts);
            }
        });
        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(), (billingResult, list) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Problem getting subscriptions: " + billingResult.getDebugMessage());
            } else {
                processPurchaseList(list, subscriptionProducts);
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
        Log.d(TAG, "ON_RESUME");
        if (null == billingClient) return;
        if (null != connectionError) {
            retryBillingServiceConnection();
        } else {
            // this just avoids an extra purchase refresh after we finish a billing flow
            if (!billingFlowInProcess) {
                Log.d(TAG, "ON_RESUME refreshPurchasesAsync");
                refreshPurchasesAsync();
            }
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        Log.i(TAG, "billingService Disconnected");
        retryBillingServiceConnection();
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        Log.i(TAG, "Billing Setup Finished responseCode " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
        int code = billingResult.getResponseCode();
        if (BillingClient.BillingResponseCode.OK == code) {
            reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS;
            queryProductsDetailsAsync();
            refreshPurchasesAsync();
            connectionError = null;
        } else if (BillingClient.BillingResponseCode.BILLING_UNAVAILABLE == code) {
            /* 1. The Play Store app on the user's device is out of date.
             * 2. The user is in an unsupported country.
             * 3. The user is an enterprise user, and their enterprise admin has disabled users from making purchases.
             * 4. Google Play is unable to charge the user’s payment method. For example, the user's credit card might have expired.
             */
            connectionError = new PluginError(code, billingResult.getDebugMessage());
        } else {
            connectionError = new PluginError(code, billingResult.getDebugMessage());
            retryBillingServiceConnection();
        }
    }

    @Override
    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
        if (BillingClient.BillingResponseCode.OK == billingResult.getResponseCode()) {
            if (!list.isEmpty()) {
                for (ProductDetails pd : list) {
                    if (!productDetailsMap.containsKey(pd.getProductId()))
                        productDetailsMap.put(pd.getProductId(), pd);
                }
                Log.d(TAG, "onProductDetailsResponse ok");
            } else {
                Log.e(TAG, "onProductDetailsResponse: Found null or empty ProductDetails. " + "Check to see if the products you requested are correctly published in the Google Play Console.");
            }
        } else {
            Log.e(TAG, "onProductDetailsResponse err " + billingResult.getResponseCode() + " " + billingResult.getDebugMessage());
        }
    }

    @Override
    protected void onPaymentResult(PayResult payResult, PluginError pluginError) {
        Log.d(TAG, "onPaymentResult " + payResult + " " + pluginError);
        super.onPaymentResult(payResult, pluginError);
    }

    @Override
    public void syncProducts() {
        super.syncProducts();
        queryProductsDetailsAsync();
        refreshPurchasesAsync();
    }
}