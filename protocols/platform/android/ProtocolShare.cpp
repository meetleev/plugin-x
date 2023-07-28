//
// Created by Lee on 2023/7/10.
//

#include "ProtocolShare.h"
#include "PluginHelper.h"
#include "cocos2d.h"

using namespace cocos2d;

NS_PLUGIN_X_BEGIN

static ProtocolShare::PlatformShareCallBack pPlatformShareCallBack = nullptr;

extern "C" {
    JNIEXPORT void JNICALL Java_com_game_core_component_ShareWrapper_onShareResult(JNIEnv*  env, jobject thiz, jint code, jstring jMsg) {
        std::string msg = JniHelper::jstring2string( jMsg);
        Director::getInstance()->getScheduler()->performFunctionInCocosThread([code, msg]() {
            if (pPlatformShareCallBack) pPlatformShareCallBack(code, msg);
            pPlatformShareCallBack = nullptr;
        });
    }
}


bool share(const char * componentName, const char * shareContentJson) {
    jobject o = PluginHelper::addComponent(componentName);
    if (nullptr != o) {
        cocos2d::JniMethodInfo t;
        if (JniUtil::getGlobalMethodInfo(t, o, "share", "(Ljava/lang/String;)V")) {
            jstring str = t.env->NewStringUTF(shareContentJson);
            t.env->CallVoidMethod(o, t.methodID, str);
            t.env->DeleteLocalRef(o);
            t.env->DeleteLocalRef(str);
            return true;
        }
    }
    return false;
}

void ProtocolShare::sharePL(ProtocolShare::SharePlatformType platformType,
                            const std::string &shareJsonContent,
                            const ProtocolShare::PlatformShareCallBack &callback) {
    std::string className;
    if (SharePlatformType::FB == platformType) {
        className = "com.game.plugin.share.FacebookShare";
    } else {
        callback(-1, "unsupported platform");
        return;
    }
    if (!className.empty()) {
        pPlatformShareCallBack = callback;
        if (!share(className.c_str(), shareJsonContent.c_str())) {
            pPlatformShareCallBack(-1, "");
            pPlatformShareCallBack = nullptr;
        }
    }
}

NS_PLUGIN_X_END
