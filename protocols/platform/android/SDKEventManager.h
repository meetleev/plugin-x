//
// Created by Lee on 2024/4/3.
//

#ifndef SDK_EVENT_MANAGER_H
#define SDK_EVENT_MANAGER_H

#include "EventManager.h"
#include "PluginMacros.h"

NS_PLUGIN_X_BEGIN

    using SDKEventManager = EventManager<int, int, const std::string>;

    enum SDKEventType {
        onLogin, onShare, onShowAd, onPayment,
    };

NS_PLUGIN_X_END

#endif //SDK_EVENT_MANAGER_H
