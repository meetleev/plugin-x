//
// Created by Lee on 2023/7/10.
//

#ifndef PROTOCOL_USER_H
#define PROTOCOL_USER_H

#include <string>
#include <functional>
#include "PluginMacros.h"

NS_PLUGIN_X_BEGIN

class ProtocolUser {
public:
    enum LoginPlatformType {
        GP, FB
    };
    typedef std::function<void(int, const std::string &)> PlatformLoginCallBack;
    static void loginPL(LoginPlatformType platformType, const PlatformLoginCallBack &callback);
};

NS_PLUGIN_X_END

#endif //PROTOCOL_USER_H
