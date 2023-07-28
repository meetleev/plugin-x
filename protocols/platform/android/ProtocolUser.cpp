//
// Created by Lee on 2023/7/10.
//

#include "ProtocolUser.h"
#include "PluginHelper.h"
#include "cocos2d.h"

using namespace cocos2d;

NS_PLUGIN_X_BEGIN

static ProtocolUser::PlatformLoginCallBack pPlatformLoginCallBack = nullptr;

extern "C" {
    JNIEXPORT void JNICALL Java_com_game_core_component_UserWrapper_onLoginResult(JNIEnv*  env, jobject thiz, jint code, jstring jMsg) {
        std::string msg = JniHelper::jstring2string( jMsg);
        Director::getInstance()->getScheduler()->performFunctionInCocosThread([code, msg]() {
            if (pPlatformLoginCallBack) pPlatformLoginCallBack(code, msg);
            pPlatformLoginCallBack = nullptr;
        });
    }
}

bool logIn(const char * componentName) {
    jobject o = PluginHelper::addComponent(componentName);
    if (nullptr != o) {
        cocos2d::JniMethodInfo t;
        if (JniUtil::getGlobalMethodInfo(t, o, "logIn", "()V")) {
            t.env->CallVoidMethod(o, t.methodID);
            t.env->DeleteLocalRef(o);
            return true;
        }
    }
    return false;
}

void ProtocolUser::loginPL(LoginPlatformType platformType, const PlatformLoginCallBack& callback) {
    std::string className;
    if (LoginPlatformType::GP == platformType) {
        className = "com.game.plugin.user.GoogleLogin";
    } else if (LoginPlatformType::FB == platformType) {
        className = "com.game.plugin.user.FacebookLogin";
    } else {
        callback(-1, "unsupported platform");
        return;
    }
    if (!className.empty()) {
        pPlatformLoginCallBack = callback;
        if (!logIn(className.c_str())) {
            pPlatformLoginCallBack(-1, "");
            pPlatformLoginCallBack = nullptr;
        }
    }
}

NS_PLUGIN_X_END