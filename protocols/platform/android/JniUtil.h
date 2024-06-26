//
// Created by Lee on 2023/7/9.
//

#ifndef  JNI_UTIL_H
#define  JNI_UTIL_H

#include <string>
#include "PluginMacros.h"
#include <jni.h>
#include "platform/java/jni/JniHelper.h"
#include <android/log.h>

#define  LOG_TAG    "Plugin Debug"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

NS_PLUGIN_X_BEGIN

class JniUtil {
public:
    static jobject createJavaObject(const char *classname);

    static jobject createJavaObjectWithActivity(const char *classname);

    static bool getGlobalMethodInfo(cc::JniMethodInfo &methodInfo, jobject javaInstance,
                             const char *methodName, const char *paramCode);
    static bool getStaticMethodInfo(cc::JniMethodInfo &methodInfo,
                                             const char *className,
                                             const char *methodName,
                                             const char *paramCode);
private:
    JniUtil(){}
};

NS_PLUGIN_X_END

#endif // JNI_UTIL_H
