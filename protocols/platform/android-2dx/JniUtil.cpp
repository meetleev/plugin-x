//
// Created by Lee on 2023/7/9.
//

#include "JniUtil.h"
#include "platform/android/jni/JniHelper.h"

using namespace cocos;

NS_PLUGIN_X_BEGIN

jobject JniUtil::createJavaObject(const char *className) {
    JNIEnv *env = JniHelper::getEnv();
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
    JNIEnv *env = JniHelper::getEnv();
    if (env) {
        jclass cls = env->FindClass(className);
        if (cls) {
            jmethodID constructor = env->GetMethodID(cls, "<init>", "(Landroid/app/Activity;)V");
            jobject object = env->NewObject(cls, constructor, JniHelper::getActivity());
            return object;
        }
    }
    return nullptr;
}


bool JniUtil::getGlobalMethodInfo(cocos2d::JniMethodInfo &methodInfo, jobject javaInstance,
                                  const char *methodName, const char *paramCode) {
    jmethodID methodID = nullptr;
    JNIEnv *pEnv = JniHelper::getEnv();
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

NS_PLUGIN_X_END