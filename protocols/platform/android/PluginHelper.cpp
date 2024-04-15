//
// Created by Lee on 2023/7/8.
//

#include "PluginHelper.h"
#include "platform/BasePlatform.h"
#include "SDKComponentHelper.h"
#include "SDKEventManager.h"

NS_PLUGIN_X_BEGIN
    bool PluginHelper::showToast(const std::string &msg) {
        return PluginHelper::showToast(msg, -1);
    }

    bool PluginHelper::showToast(const std::string &msg, int duration) {
        return SDKComponentHelper::showToast(msg, duration);
    }

    bool PluginHelper::showBannerAd(const std::string &componentName) {
        if (!componentName.empty()) {
            if (SDKComponentHelper::showBannerAd(componentName)) {
                return true;
            }
        }
        return false;
    }

    bool PluginHelper::hideBannerAd(const std::string &componentName) {
        if (!componentName.empty()) {
            if (SDKComponentHelper::hideBannerAd(componentName)) {
                return true;
            }
        }
        return false;
    }

    bool PluginHelper::showRewardedVideoAd(const std::string &componentName,
                                           const PlatformCallBack &callback) {
        if (!componentName.empty()) {
            SDKEventManager::Instance().addOnceListener(SDKEventType::onShowAd, callback);
            if (SDKComponentHelper::showRewardedVideoAd(componentName)) {
                return true;
            }
            SDKEventManager::Instance().removeListener(SDKEventType::onShowAd);
        }
        return false;
    }

    bool PluginHelper::showRewardedInterstitialAd(const std::string &componentName,
                                                  const PlatformCallBack &callback) {
        if (!componentName.empty()) {
            SDKEventManager::Instance().addOnceListener(SDKEventType::onShowAd, callback);
            if (SDKComponentHelper::showRewardedInterstitialAd(componentName)) {
                return true;
            }
            SDKEventManager::Instance().removeListener(SDKEventType::onShowAd);
        }
        return false;
    }

    bool PluginHelper::showInterstitialAd(const std::string &componentName,
                                          const PlatformCallBack &callback) {
        if (!componentName.empty()) {
            SDKEventManager::Instance().addOnceListener(SDKEventType::onShowAd, callback);
            if (SDKComponentHelper::showInterstitialAd(componentName)) {
                return true;
            }
            SDKEventManager::Instance().removeListener(SDKEventType::onShowAd);
        }
        return false;
    }

    bool PluginHelper::showFloatAd(const std::string &componentName) {
        if (!componentName.empty()) {
            return SDKComponentHelper::showFloatAd(componentName);
        }
        return false;
    }

    bool PluginHelper::hideFloatAd(const std::string &componentName) {
        if (!componentName.empty()) {
            return SDKComponentHelper::hideFloatAd(componentName);
        }
        return false;
    }

    bool PluginHelper::signIn(const std::string &componentName, const PlatformCallBack &callback) {
        if (!componentName.empty()) {
            SDKEventManager::Instance().addOnceListener(SDKEventType::onLogin, callback);
            if (SDKComponentHelper::signIn(componentName)) {
                return true;
            }
            SDKEventManager::Instance().removeListener(SDKEventType::onLogin);
        }
        return false;
    }

    bool PluginHelper::signOut(const std::string &componentName, const PlatformCallBack &callback) {
        if (!componentName.empty()) {
            SDKEventManager::Instance().addOnceListener(SDKEventType::onLogin, callback);
            if (SDKComponentHelper::signOut(componentName)) {
                return true;
            }
            SDKEventManager::Instance().removeListener(SDKEventType::onLogin);
        }
        return false;
    }

    bool PluginHelper::share(const std::string &componentName, const std::string &shareJsonContent,
                             const PlatformCallBack &callback) {
        if (!componentName.empty()) {
            SDKEventManager::Instance().addOnceListener(SDKEventType::onShare, callback);
            if (SDKComponentHelper::share(componentName, shareJsonContent)) {
                return true;
            }
            SDKEventManager::Instance().removeListener(SDKEventType::onShare);
        }
        return false;
    }
    bool PluginHelper::paymentWithProductId(const std::string &componentName,
                                                const std::string &productId) {
        return PluginHelper::paymentWithProductId(componentName, productId, nullptr);
    }

    void PluginHelper::addPaymentResultListener(const pluginx::PlatformCallBack &callback) {
        SDKEventManager::Instance().addListener(SDKEventType::onPayment, callback);
    }

    bool PluginHelper::paymentWithProductId(const std::string &componentName,
                                            const std::string &productId,
                                            const PlatformCallBack &callback) {
        if (!componentName.empty()) {
            SDKEventManager::Instance().addOnceListener(SDKEventType::onPayment, callback);
            if (SDKComponentHelper::paymentWithProductId(componentName, productId)) {
                return true;
            }
            SDKEventManager::Instance().removeListener(SDKEventType::onPayment);
        }
        return false;
    }

    bool PluginHelper::logEvent(const std::string &componentName, const std::string &eventId) {
        return PluginHelper::logEvent(componentName, eventId, "");
    }

    bool PluginHelper::logEvent(const std::string &componentName, const std::string &eventId,
                                const std::string &event) {
        if (!componentName.empty()) {
            return SDKComponentHelper::logEvent(componentName, eventId, event);
        }
        return false;
    }

NS_PLUGIN_X_END