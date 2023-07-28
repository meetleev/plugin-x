//
// Created by Lee on 2023/7/10.
//

#ifndef PROTOCOL_SHARE_H
#define PROTOCOL_SHARE_H

#include <string>
#include <functional>
#include "PluginMacros.h"

NS_PLUGIN_X_BEGIN

class ProtocolShare {
public:
    enum SharePlatformType {
        FB
    };
    typedef std::function<void(int, const std::string &)> PlatformShareCallBack;

    static void sharePL(SharePlatformType platformType, const std::string &shareJsonContent,
                        const PlatformShareCallBack &callback);
};

NS_PLUGIN_X_END

#endif //PROTOCOL_SHARE_H
