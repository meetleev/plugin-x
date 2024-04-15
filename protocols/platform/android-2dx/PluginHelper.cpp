//
// Created by Lee on 2023/7/8.
//

#include "PluginMgr.h"
#include "PluginHelper.h"
#include "cocos2d.h"
#include "JniUtil.h"

using namespace cocos2d;

NS_PLUGIN_X_BEGIN

jobject g_plugin = nullptr;

extern "C" {

    JNIEXPORT void JNICALL Java_com_game_core_base_SDKComponent_register(JNIEnv *env, jobject job)
    {
        g_plugin = env->NewGlobalRef(job);
    }
}

jobject PluginHelper::addComponent(const char * componentName)
{
    cocos2d::JniMethodInfo t;
    if (JniUtil::getGlobalMethodInfo(t, g_plugin, "addComponent", "(Ljava/lang/String;)Lcom/game/core/component/Component;")) {
        jstring str = t.env->NewStringUTF(componentName);
        jobject o = t.env->CallObjectMethod(g_plugin, t.methodID, str);
        t.env->DeleteLocalRef(str);
        if (nullptr != o) {
            return o;
        }
    }
    return nullptr;
}

NS_PLUGIN_X_END