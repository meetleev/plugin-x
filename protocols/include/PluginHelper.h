//
// Created by Lee on 2023/7/10.
//

#ifndef PLUGIN_HELPER_H
#define PLUGIN_HELPER_H

#include "PluginMacros.h"
#include <string>
#include <functional>

NS_PLUGIN_X_BEGIN

    typedef std::function<void(int, const std::string &)> PlatformCallBack;

    class PluginHelper {
    public:
        // ui
        static bool showToast(const std::string &msg);
        static bool showToast(const std::string &msg, int duration);
        // ads
        static bool showBannerAd(const std::string &componentName);

        static bool hideBannerAd(const std::string &componentName);

        static bool
        showRewardedVideoAd(const std::string &componentName, const PlatformCallBack &callback);

        static bool showRewardedInterstitialAd(const std::string &componentName,
                                               const PlatformCallBack &callback);

        static bool
        showInterstitialAd(const std::string &componentName, const PlatformCallBack &callback);

        static bool showFloatAd(const std::string &componentName);

        static bool hideFloatAd(const std::string &componentName);

        // user
        static bool signIn(const std::string &componentName, const PlatformCallBack &callback);
        static bool signOut(const std::string &componentName, const PlatformCallBack &callback);

        // share
        static bool share(const std::string &componentName, const std::string &shareJsonContent,
                          const PlatformCallBack &callback);

        // iap
         static void addPaymentResultListener(const PlatformCallBack &callback);
         static bool
        paymentWithProductId(const std::string &componentName, const std::string &productId);
        static bool
        paymentWithProductId(const std::string &componentName, const std::string &productId,
                             const PlatformCallBack &callback);

        // analytics
        static bool logEvent(const std::string &componentName, const std::string &eventId);

        static bool logEvent(const std::string &componentName, const std::string &eventId,
                             const std::string &event);
    };

NS_PLUGIN_X_END

#endif //PLUGIN_HELPER_H
