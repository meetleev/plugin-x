//
// Created by Lee on 2023/7/10.
//

#ifndef SDK_COMPONENT_HELPER_H
#define SDK_COMPONENT_HELPER_H

#include "PluginMacros.h"
#include "JniUtil.h"

NS_PLUGIN_X_BEGIN

class SDKComponentHelper {
public:
    static bool showToast(const std::string &msg, int duration);
    // ads
    static bool showBannerAd(const std::string& componentName);
    static bool hideBannerAd(const std::string& componentName);
    static bool showRewardedVideoAd(const std::string& componentName);
    static bool showRewardedInterstitialAd(const std::string& componentName);
    static bool showInterstitialAd(const std::string& componentName);
    static bool showFloatAd(const std::string& componentName);
    static bool hideFloatAd(const std::string& componentName);
    // user
    static bool signIn(const std::string& componentName);
    static bool signOut(const std::string& componentName);
    // share
    static bool share(const std::string& componentName,  const std::string &shareJsonContent);
    // iap
    static bool paymentWithProductId(const std::string &componentName, const std::string &productId);
    // analytics
    static bool logEvent(const std::string &componentName, const std::string &eventId,const std::string &event);
protected:
    SDKComponentHelper() {}
    static jobject addComponent(const char * componentName);
    static bool nativeCallJava(const std::string& componentName, const std::string& method);
    static bool nativeCallJava(const std::string& componentName, const std::string& method, const std::string& arg1);
    static bool nativeCallJava(const std::string& componentName, const std::string& method, const std::string& arg1, const std::string& arg2);
};

NS_PLUGIN_X_END

#endif //SDK_COMPONENT_HELPER_H
