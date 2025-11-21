//
// Created by Lee on 2023/7/9.
//

#ifndef  JNI_UTIL_H
#define  JNI_UTIL_H

#include <string>
#include "PluginMacros.h"
#include <jni.h>
#include "platform/java/jni/JniHelper.h"

NS_PLUGIN_X_BEGIN

using JValue = std::variant<
        std::string,
        int,
        bool,
        float,
        double
>;

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

    template<typename... Args>
    static bool callJavaStatic(const std::string& className, const std::string& method, Args&&... args)
    {
        std::vector<JValue> values;
        (values.emplace_back(std::forward<Args>(args)), ...);
        return callJavaStaticWithList(className, method, values);
    }
private:
    JniUtil(){}
    static bool callJavaStaticWithList(const std::string& className, const std::string& method, const std::vector<JValue>& args);
};

NS_PLUGIN_X_END

#endif // JNI_UTIL_H
