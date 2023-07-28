//
// Created by Lee on 2023/7/8.
//

#ifndef PLUGIN_MGR_H
#define PLUGIN_MGR_H

#include <string>
#include <functional>
#include "PluginMacros.h"
#include "ProtocolUser.h"
#include "ProtocolShare.h"

NS_PLUGIN_X_BEGIN

class PluginMgr {
public:
    static void loginPL(ProtocolUser::LoginPlatformType platformType, const ProtocolUser::PlatformLoginCallBack &callback) {
        return ProtocolUser::loginPL(platformType, callback);
    }
    static void sharePL(ProtocolShare::SharePlatformType platformType, const std::string& shareJsonContent, const ProtocolShare::PlatformShareCallBack &callback){
        return ProtocolShare::sharePL(platformType, shareJsonContent, callback);
    }
};

NS_PLUGIN_X_END

#endif //PLUGIN_MGR_H
