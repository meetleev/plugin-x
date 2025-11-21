//
// Created by Lee on 2023/7/8.
//

#include "SDKComponentHelper.h"
#include "SDKEventManager.h"
//#define USE_REFLECTION  1

NS_PLUGIN_X_BEGIN
    jobject g_plugin = nullptr;

    extern "C" {

    JNIEXPORT void JNICALL
    Java_com_pluginx_core_base_SDKComponent_register(JNIEnv *env, jobject job) {
#ifdef USE_REFLECTION
        g_plugin = env->NewGlobalRef(job);
#endif
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

    bool SDKComponentHelper::showToast(const std::string &msg, int duration) {
#ifdef USE_REFLECTION
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
#else
        if (0 < duration)
            return SDKComponentHelper::nativeCallJava("showToast", msg, duration);
        return SDKComponentHelper::nativeCallJava( "showToast",msg);
#endif
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
            return SDKComponentHelper::nativeCallJava( "showBannerAd", componentName);
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
            return SDKComponentHelper::nativeCallJava("hideBannerAd", componentName);
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
            return SDKComponentHelper::nativeCallJava("showRewardedVideoAd",componentName);
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
            return SDKComponentHelper::nativeCallJava( "showRewardedInterstitialAd", componentName);
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
            return SDKComponentHelper::nativeCallJava( "showInterstitialAd", componentName);
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
            return SDKComponentHelper::nativeCallJava("showFloatAd", componentName);
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
            return SDKComponentHelper::nativeCallJava("hideFloatAd", componentName);
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
            return SDKComponentHelper::nativeCallJava("signIn", componentName);
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
            return SDKComponentHelper::nativeCallJava("signOut", componentName);
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
            return SDKComponentHelper::nativeCallJava("share", componentName, shareJsonContent);
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
            return SDKComponentHelper::nativeCallJava("paymentWithProductId", componentName,
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
                return SDKComponentHelper::nativeCallJava("logEvent", componentName, eventId,
                                                          event);
            return SDKComponentHelper::nativeCallJava("logEvent", componentName, eventId);
#endif
        }
        return false;
    }
NS_PLUGIN_X_END