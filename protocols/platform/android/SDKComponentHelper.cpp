//
// Created by Lee on 2023/7/8.
//

#include "SDKComponentHelper.h"
#include "SDKEventManager.h"

//#define USE_REFLECTION  1

NS_PLUGIN_X_BEGIN
    jobject g_plugin = nullptr;

    static const char *SCRIPT_CALL_JAVA_BRIDGE_CLASS = "com/pluginx/core/ScriptCallJavaBridge";
    extern "C" {

    JNIEXPORT void JNICALL
    Java_com_pluginx_core_base_SDKComponent_register(JNIEnv *env, jobject job) {
        g_plugin = env->NewGlobalRef(job);
    }
    JNIEXPORT void JNICALL
    Java_com_pluginx_core_component_AdsWrapper_onShowAdResult(JNIEnv *env, jobject thiz, jint code,
                                                              jstring jMsg) {
        std::string msg = cc::JniHelper::jstring2string(jMsg);
        SDKEventManager::Instance().emit(SDKEventType::onShowAd, code, msg);
    }

    JNIEXPORT void JNICALL
    Java_com_pluginx_core_component_ShareWrapper_onShareResult(JNIEnv *env, jobject thiz, jint code,
                                                               jstring jMsg) {
        std::string msg = cc::JniHelper::jstring2string(jMsg);
        SDKEventManager::Instance().emit(SDKEventType::onShare, code, msg);
    }

    JNIEXPORT void JNICALL
    Java_com_pluginx_core_component_UserWrapper_onLoginResult(JNIEnv *env, jobject thiz, jint code,
                                                              jstring jMsg) {
        std::string msg = cc::JniHelper::jstring2string(jMsg);
        SDKEventManager::Instance().emit(SDKEventType::onLogin, code, msg);
    }

    JNIEXPORT void JNICALL
    Java_com_pluginx_core_component_IAPWrapper_onPaymentResult(JNIEnv *env, jobject thiz, jint code,
                                                               jstring jMsg) {
        std::string msg = cc::JniHelper::jstring2string(jMsg);
        SDKEventManager::Instance().emit(SDKEventType::onPayment, code, msg);
    }
    }

    jobject SDKComponentHelper::addComponent(const char *componentName) {
        cc::JniMethodInfo t;
        if (JniUtil::getGlobalMethodInfo(t, g_plugin, "addComponent",
                                         "(Ljava/lang/String;)Lcom/pluginx/core/component/Component;")) {
            jstring str = t.env->NewStringUTF(componentName);
            jobject o = t.env->CallObjectMethod(g_plugin, t.methodID, str);
            t.env->DeleteLocalRef(str);
            if (nullptr != o) {
                return o;
            }
        }
        return nullptr;
    }

    bool
    SDKComponentHelper::nativeCallJava(const std::string &componentName,
                                       const std::string &method) {
        cc::JniMethodInfo t;
        if (JniUtil::getStaticMethodInfo(t, SCRIPT_CALL_JAVA_BRIDGE_CLASS, method.c_str(),
                                         "(Ljava/lang/String;)V")) {
            jstring str = t.env->NewStringUTF(componentName.c_str());
            t.env->CallStaticVoidMethod(t.classID, t.methodID, str);
            t.env->DeleteLocalRef(t.classID);
            t.env->DeleteLocalRef(str);
            return true;
        }
        return false;
    }

    bool
    SDKComponentHelper::nativeCallJava(const std::string &componentName, const std::string &method,
                                       const std::string &arg1) {
        cc::JniMethodInfo t;
        if (JniUtil::getStaticMethodInfo(t, SCRIPT_CALL_JAVA_BRIDGE_CLASS, method.c_str(),
                                         "(Ljava/lang/String;Ljava/lang/String;)V")) {
            jstring str1 = t.env->NewStringUTF(componentName.c_str());
            jstring str2 = t.env->NewStringUTF(arg1.c_str());
            t.env->CallStaticVoidMethod(t.classID, t.methodID, str1, str2);
            t.env->DeleteLocalRef(t.classID);
            t.env->DeleteLocalRef(str1);
            t.env->DeleteLocalRef(str2);
            return true;
        }
        return false;
    }

    bool
    SDKComponentHelper::nativeCallJava(const std::string &componentName, const std::string &method,
                                       const std::string &arg1, const std::string &arg2) {
        cc::JniMethodInfo t;
        if (JniUtil::getStaticMethodInfo(t, SCRIPT_CALL_JAVA_BRIDGE_CLASS, method.c_str(),
                                         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")) {
            jstring str1 = t.env->NewStringUTF(componentName.c_str());
            jstring str2 = t.env->NewStringUTF(arg1.c_str());
            jstring str3 = t.env->NewStringUTF(arg2.c_str());
            t.env->CallStaticVoidMethod(t.classID, t.methodID, str1, str2, str3);
            t.env->DeleteLocalRef(t.classID);
            t.env->DeleteLocalRef(str1);
            t.env->DeleteLocalRef(str2);
            t.env->DeleteLocalRef(str3);
            return true;
        }
        return false;
    }

    bool SDKComponentHelper::showToast(const std::string &msg, int duration) {
        cc::JniMethodInfo t;
        if (0 < duration) {
            if (JniUtil::getGlobalMethodInfo(t, g_plugin, "showToast",
                                             "(Ljava/lang/String;I)V")) {
                jstring str = t.env->NewStringUTF(msg.c_str());
                 t.env->CallVoidMethod(g_plugin, t.methodID, str, duration);
                t.env->DeleteLocalRef(str);
                return true;
            }
        }
        if (JniUtil::getGlobalMethodInfo(t, g_plugin, "showToast",
                                         "(Ljava/lang/String;)V")) {
            jstring str = t.env->NewStringUTF(msg.c_str());
            t.env->CallVoidMethod(g_plugin, t.methodID, str);
            t.env->DeleteLocalRef(str);
        }
        return false;
    }

    bool SDKComponentHelper::showBannerAd(const std::string &componentName) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "showBannerAd", "()V")) {
                    t.env->CallVoidMethod(o, t.methodID);
                    t.env->DeleteLocalRef(o);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "showBannerAd");
#endif
        }
        return false;
    }

    bool SDKComponentHelper::hideBannerAd(const std::string &componentName) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "hideBannerAd", "()V")) {
                    t.env->CallVoidMethod(o, t.methodID);
                    t.env->DeleteLocalRef(o);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "hideBannerAd");
#endif
        }
        return false;
    }

    bool SDKComponentHelper::showRewardedVideoAd(const std::string &componentName) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "showRewardedVideoAd", "()V")) {
                    t.env->CallVoidMethod(o, t.methodID);
                    t.env->DeleteLocalRef(o);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "showRewardedVideoAd");
#endif
        }
        return false;
    }

    bool SDKComponentHelper::showRewardedInterstitialAd(
            const std::string &componentName) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "showRewardedInterstitialAd", "()V")) {
                    t.env->CallVoidMethod(o, t.methodID);
                    t.env->DeleteLocalRef(o);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "showRewardedInterstitialAd");
#endif
        }
        return false;
    }

    bool SDKComponentHelper::showInterstitialAd(const std::string &componentName) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "showInterstitialAd", "()V")) {
                    t.env->CallVoidMethod(o, t.methodID);
                    t.env->DeleteLocalRef(o);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "showInterstitialAd");
#endif
        }
        return false;
    }

    bool SDKComponentHelper::showFloatAd(const std::string &componentName) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "showFloatAd", "()V")) {
                    t.env->CallVoidMethod(o, t.methodID);
                    t.env->DeleteLocalRef(o);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "showFloatAd");
#endif
        }
        return false;
    }

    bool SDKComponentHelper::hideFloatAd(const std::string &componentName) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "hideFloatAd", "()V")) {
                    t.env->CallVoidMethod(o, t.methodID);
                    t.env->DeleteLocalRef(o);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "hideFloatAd");
#endif
        }
        return false;
    }

    bool SDKComponentHelper::signIn(const std::string &componentName) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "signIn", "()V")) {
                    t.env->CallVoidMethod(o, t.methodID);
                    t.env->DeleteLocalRef(o);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "signIn");
#endif
        }
        return false;
    }

    bool SDKComponentHelper::signOut(const std::string &componentName) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "signOut", "()V")) {
                    t.env->CallVoidMethod(o, t.methodID);
                    t.env->DeleteLocalRef(o);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "signOut");
#endif
        }
        return false;
    }

    bool SDKComponentHelper::share(const std::string &componentName,
                                   const std::string &shareJsonContent) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "share", "(Ljava/lang/String;)V")) {
                    jstring str = t.env->NewStringUTF(shareJsonContent.c_str());
                    t.env->CallVoidMethod(o, t.methodID, str);
                    t.env->DeleteLocalRef(o);
                    t.env->DeleteLocalRef(str);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "share", shareJsonContent);
#endif
        }
        return false;
    }

    bool SDKComponentHelper::paymentWithProductId(const std::string &componentName,
                                                  const std::string &productId) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (JniUtil::getGlobalMethodInfo(t, o, "paymentWithProductId",
                                                 "(Ljava/lang/String;)V")) {
                    jstring str = t.env->NewStringUTF(productId.c_str());
                    t.env->CallVoidMethod(o, t.methodID, str);
                    t.env->DeleteLocalRef(o);
                    t.env->DeleteLocalRef(str);
                    return true;
                }
            }
#else
            return SDKComponentHelper::nativeCallJava(componentName, "paymentWithProductId",
                                                      productId);
#endif
        }
        return false;
    }

    // analytics
    bool SDKComponentHelper::logEvent(const std::string &componentName, const std::string &eventId,
                                      const std::string &event) {
        if (!componentName.empty()) {
#ifdef USE_REFLECTION
            jobject o = SDKComponentHelper::addComponent(componentName.c_str());
            if (nullptr != o) {
                cc::JniMethodInfo t;
                if (!event.empty()) {
                    if (JniUtil::getGlobalMethodInfo(t, o, "logEvent",
                                                     "(Ljava/lang/String;Ljava/lang/String;)V")) {
                        jstring eventIdStr = t.env->NewStringUTF(eventId.c_str());
                        jstring eventStr = t.env->NewStringUTF(event.c_str());
                        t.env->CallVoidMethod(o, t.methodID, eventIdStr, eventStr);
                        t.env->DeleteLocalRef(o);
                        t.env->DeleteLocalRef(eventIdStr);
                        t.env->DeleteLocalRef(eventStr);
                        return true;
                    }
                }

                if (JniUtil::getGlobalMethodInfo(t, o, "logEvent", "(Ljava/lang/String;)V")) {
                    jstring str = t.env->NewStringUTF(eventId.c_str());
                    t.env->CallVoidMethod(o, t.methodID, str);
                    t.env->DeleteLocalRef(o);
                    t.env->DeleteLocalRef(str);
                    return true;
                }
            }
#else
            if (!event.empty())
                return SDKComponentHelper::nativeCallJava(componentName, "logEvent", eventId,
                                                          event);
            return SDKComponentHelper::nativeCallJava(componentName, "logEvent", eventId);
#endif
        }
        return false;
    }
NS_PLUGIN_X_END