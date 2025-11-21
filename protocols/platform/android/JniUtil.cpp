//
// Created by Lee on 2023/7/9.
//

#include "JniUtil.h"
#include "PluginConsole.h"

NS_PLUGIN_X_BEGIN

    jobject JniUtil::createJavaObject(const char *className) {
        JNIEnv *env = cc::JniHelper::getEnv();
        if (env) {
            jclass cls = env->FindClass(className);
            if (cls) {
                jmethodID constructor = env->GetMethodID(cls, "<init>", "()V");
                jobject object = env->NewObject(cls, constructor);
                return object;
            }
        }
        return nullptr;
    }

    jobject JniUtil::createJavaObjectWithActivity(const char *className) {
        JNIEnv *env = cc::JniHelper::getEnv();
        if (env) {
            jclass cls = env->FindClass(className);
            if (cls) {
                jmethodID constructor = env->GetMethodID(cls, "<init>",
                                                         "(Landroid/app/Activity;)V");
                jobject object = env->NewObject(cls, constructor, cc::JniHelper::getActivity());
                return object;
            }
        }
        return nullptr;
    }


    bool JniUtil::getGlobalMethodInfo(cc::JniMethodInfo &methodInfo, jobject javaInstance,
                                      const char *methodName, const char *paramCode) {
        jmethodID methodID;
        JNIEnv *pEnv = cc::JniHelper::getEnv();
        bool bRet = false;
        do {
            if (!pEnv) {
                break;
            }
            if (!javaInstance) {
                LOGD("javaInstance null given!");
                break;
            }
            jclass classID = pEnv->GetObjectClass(javaInstance);
            if (!classID) {
                LOGD("getClassID classID null");
                break;
            }
            methodID = pEnv->GetMethodID(classID, methodName, paramCode);
            if (!methodID) {
                pEnv->DeleteLocalRef(classID);
                LOGD("Failed to find method id of %s", methodName);
                break;
            }

            methodInfo.classID = classID;
            methodInfo.env = pEnv;
            methodInfo.methodID = methodID;

            bRet = true;
        } while (false);
        return bRet;
    }

    bool JniUtil::getStaticMethodInfo(cc::JniMethodInfo &methodInfo,
                                      const char *className,
                                      const char *methodName,
                                      const char *paramCode) {
        if ((nullptr == className) ||
            (nullptr == methodName) ||
            (nullptr == paramCode)) {
            return false;
        }
        return cc::JniHelper::getStaticMethodInfo(methodInfo, className, methodName, paramCode);
    }


    static std::string getJNISign(const std::vector<JValue>& args)
    {
        std::string sig = "(";

        for (auto& v : args)
        {
            if (std::holds_alternative<std::string>(v)) sig += "Ljava/lang/String;";
            else if (std::holds_alternative<int>(v))     sig += "I";
            else if (std::holds_alternative<bool>(v))    sig += "Z";
            else if (std::holds_alternative<float>(v))   sig += "F";
            else if (std::holds_alternative<double>(v))  sig += "D";
        }

        sig += ")V";
        return sig;
    }

    bool JniUtil::callJavaStaticWithList(const std::string& className,
                                       const std::string& method,
                                       const std::vector<JValue>& args)
    {
        cc::JniMethodInfo t;

        std::string sig = getJNISign(args);

        if (!JniUtil::getStaticMethodInfo(t,
                                          className.c_str(),
                                          method.c_str(),
                                          sig.c_str()))
        {
            LOGE("Method not found: %s %s", method.c_str(), sig.c_str());
            return false;
        }

        std::vector<jvalue> jniArgs;
        std::vector<jobject> localRefs;  // 用于释放局部引用

        for (auto& v : args)
        {
            jvalue val;

            if (std::holds_alternative<std::string>(v))
            {
                auto str = std::get<std::string>(v);
                jstring jstr = t.env->NewStringUTF(str.c_str());
                val.l = jstr;
                localRefs.push_back(jstr);
            }
            else if (std::holds_alternative<int>(v))
                val.i = std::get<int>(v);

            else if (std::holds_alternative<bool>(v))
                val.z = std::get<bool>(v);

            else if (std::holds_alternative<float>(v))
                val.f = std::get<float>(v);

            else if (std::holds_alternative<double>(v))
                val.d = std::get<double>(v);

            jniArgs.push_back(val);
        }

        t.env->CallStaticVoidMethodA(
                t.classID,
                t.methodID,
                jniArgs.empty() ? nullptr : jniArgs.data()
        );

        // 清理局部引用
        for (auto obj : localRefs)
            t.env->DeleteLocalRef(obj);

        t.env->DeleteLocalRef(t.classID);

        return true;
    }
NS_PLUGIN_X_END