//
// Created by Lee on 2023/7/9.
//

#include "JniUtil.h"

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

NS_PLUGIN_X_END