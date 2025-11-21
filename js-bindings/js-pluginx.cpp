#include "cocos/bindings/jswrapper/SeApi.h"
#include "cocos/bindings/manual/jsb_global.h"
#include "js-pluginx.h"

#ifndef JSB_ALLOC
#define JSB_ALLOC(kls, ...) new (std::nothrow) kls(__VA_ARGS__)
#endif

#ifndef JSB_FREE
#define JSB_FREE(ptr) delete ptr
#endif

se::Class *__jsb_plugin_x_PluginHelper_class = nullptr;
se::Object *__jsb_plugin_x_PluginHelper_proto = nullptr;

SE_DECLARE_FINALIZE_FUNC(js_delete_plugin_x_PluginHelper)

static bool js_plugin_x_PluginHelper_showToast_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc < 1) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    if (1 == argc) {
        pluginx::PluginHelper::showToast(*arg1);
        return true;
    }

    int *arg2 = 0;
    int temp2;
    ok &= sevalue_to_native(args[1], &temp2, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg2 = &temp2;

    pluginx::PluginHelper::showToast(*arg1, *arg2);

    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_showToast_static)

static bool js_plugin_x_PluginHelper_showBannerAd_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc != 1) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    pluginx::PluginHelper::showBannerAd(*arg1);
    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_showBannerAd_static)

static bool js_plugin_x_PluginHelper_hideBannerAd_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc != 1) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    pluginx::PluginHelper::hideBannerAd(*arg1);
    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_hideBannerAd_static)

static bool js_plugin_x_PluginHelper_showRewardedVideoAd_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc != 2) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1 = 0;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    pluginx::PlatformCallBack *arg2 = 0;
    pluginx::PlatformCallBack temp2;
    ok &= sevalue_to_native(args[1], &temp2, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg2 = &temp2;

    pluginx::PluginHelper::showRewardedVideoAd(*arg1, *arg2);

    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_showRewardedVideoAd_static)

static bool js_plugin_x_PluginHelper_showRewardedInterstitialAd_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc != 2) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1 = 0;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    pluginx::PlatformCallBack *arg2 = 0;
    pluginx::PlatformCallBack temp2;
    ok &= sevalue_to_native(args[1], &temp2, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg2 = &temp2;

    pluginx::PluginHelper::showRewardedInterstitialAd(*arg1, *arg2);

    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_showRewardedInterstitialAd_static)

static bool js_plugin_x_PluginHelper_showInterstitialAd_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc != 2) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1 = 0;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    pluginx::PlatformCallBack *arg2 = 0;
    pluginx::PlatformCallBack temp2;
    ok &= sevalue_to_native(args[1], &temp2, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg2 = &temp2;

    pluginx::PluginHelper::showInterstitialAd(*arg1, *arg2);

    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_showInterstitialAd_static)

static bool js_plugin_x_PluginHelper_showFloatAd_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc != 1) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    pluginx::PluginHelper::showFloatAd(*arg1);
    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_showFloatAd_static)

static bool js_plugin_x_PluginHelper_hideFloatAd_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc != 1) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    pluginx::PluginHelper::hideFloatAd(*arg1);
    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_hideFloatAd_static)

static bool js_plugin_x_PluginHelper_signIn_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc != 2) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1 = 0;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    pluginx::PlatformCallBack *arg2 = 0;
    pluginx::PlatformCallBack temp2;
    ok &= sevalue_to_native(args[1], &temp2, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg2 = &temp2;

    pluginx::PluginHelper::signIn(*arg1, *arg2);

    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_signIn_static)

static bool js_plugin_x_PluginHelper_signOut_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc != 2) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1 = 0;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    pluginx::PlatformCallBack *arg2 = 0;
    pluginx::PlatformCallBack temp2;
    ok &= sevalue_to_native(args[1], &temp2, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg2 = &temp2;

    pluginx::PluginHelper::signOut(*arg1, *arg2);

    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_signOut_static)

static bool js_plugin_x_PluginHelper_share_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc != 3) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    std::string *arg2;
    std::string temp2;
    ok &= sevalue_to_native(args[1], &temp2, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg2 = &temp2;

    pluginx::PlatformCallBack *arg3;
    pluginx::PlatformCallBack temp3;
    ok &= sevalue_to_native(args[2], &temp3, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg3 = &temp3;

    pluginx::PluginHelper::share(*arg1, *arg2, *arg3);

    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_share_static)

static bool js_plugin_x_PluginHelper_paymentWithProductId_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (2 > argc) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 2);
        return false;
    }
    std::string *arg1;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    std::string *arg2;
    std::string temp2;
    ok &= sevalue_to_native(args[1], &temp2, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg2 = &temp2;

    if (2 == argc) {
        pluginx::PluginHelper::paymentWithProductId(*arg1, *arg2);
        return true;
    }

    pluginx::PlatformCallBack *arg3;
    pluginx::PlatformCallBack temp3;
    ok &= sevalue_to_native(args[2], &temp3, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg3 = &temp3;

    pluginx::PluginHelper::paymentWithProductId(*arg1, *arg2, *arg3);

    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_paymentWithProductId_static)

static bool js_plugin_x_PluginHelper_addPaymentResultListener_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (1 != argc) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }

    pluginx::PlatformCallBack *arg1;
    pluginx::PlatformCallBack temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    pluginx::PluginHelper::addPaymentResultListener(*arg1);

    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_addPaymentResultListener_static)

static bool js_plugin_x_PluginHelper_logEvent_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    size_t argc = args.size();

    if (argc < 2) {
        SE_REPORT_ERROR("wrong number of arguments: %d, was expecting %d", (int) argc, 1);
        return false;
    }
    std::string *arg1;
    std::string temp1;
    ok &= sevalue_to_native(args[0], &temp1, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg1 = &temp1;

    std::string *arg2;
    std::string temp2;
    ok &= sevalue_to_native(args[1], &temp2, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg2 = &temp2;

    if (2 == argc) {
        pluginx::PluginHelper::logEvent(*arg1, *arg2);
        return true;
    }

    std::string *arg3;
    std::string temp3;
    ok &= sevalue_to_native(args[2], &temp3, s.thisObject());
    SE_PRECONDITION2(ok, false, "Error processing arguments");
    arg3 = &temp3;

    pluginx::PluginHelper::logEvent(*arg1, *arg2, *arg3);

    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginHelper_logEvent_static)

static bool js_new_PluginHelper(se::State &s) // NOLINT(readability-identifier-naming)
{
    pluginx::PluginHelper *result;
    result = (pluginx::PluginHelper *) new pluginx::PluginHelper();

    auto *ptr = JSB_MAKE_PRIVATE_OBJECT_WITH_INSTANCE(result);
    s.thisObject()->setPrivateObject(ptr);
    return true;
}

SE_BIND_CTOR(js_new_PluginHelper, __jsb_plugin_x_PluginHelper_class,
             js_delete_plugin_x_PluginHelper)

static bool js_delete_plugin_x_PluginHelper(se::State &s) {
    return true;
}

SE_BIND_FINALIZE_FUNC(js_delete_plugin_x_PluginHelper)

bool js_register_plugin_x_plugin_helper(se::Object *obj) {
    auto *cls = se::Class::create("PluginHelper", obj, nullptr, _SE(js_new_PluginHelper));

    cls->defineStaticProperty("__isJSB", se::Value(true),
                              se::PropertyAttribute::READ_ONLY | se::PropertyAttribute::DONT_ENUM |
                              se::PropertyAttribute::DONT_DELETE);
    cls->defineStaticFunction("showToast", _SE(js_plugin_x_PluginHelper_showToast_static));
    cls->defineStaticFunction("showBannerAd", _SE(js_plugin_x_PluginHelper_showBannerAd_static));
    cls->defineStaticFunction("hideBannerAd", _SE(js_plugin_x_PluginHelper_hideBannerAd_static));
    cls->defineStaticFunction("showRewardedVideoAd",
                              _SE(js_plugin_x_PluginHelper_showRewardedVideoAd_static));
    cls->defineStaticFunction("showRewardedInterstitialAd",
                              _SE(js_plugin_x_PluginHelper_showRewardedInterstitialAd_static));
    cls->defineStaticFunction("showInterstitialAd",
                              _SE(js_plugin_x_PluginHelper_showInterstitialAd_static));
    cls->defineStaticFunction("showFloatAd", _SE(js_plugin_x_PluginHelper_showFloatAd_static));
    cls->defineStaticFunction("hideFloatAd", _SE(js_plugin_x_PluginHelper_hideFloatAd_static));
    cls->defineStaticFunction("signIn", _SE(js_plugin_x_PluginHelper_signIn_static));
    cls->defineStaticFunction("signOut", _SE(js_plugin_x_PluginHelper_signOut_static));
    cls->defineStaticFunction("share", _SE(js_plugin_x_PluginHelper_share_static));
    cls->defineStaticFunction("paymentWithProductId",
                              _SE(js_plugin_x_PluginHelper_paymentWithProductId_static));
    cls->defineStaticFunction("addPaymentResultListener",
                              _SE(js_plugin_x_PluginHelper_addPaymentResultListener_static));
    cls->defineStaticFunction("logEvent", _SE(js_plugin_x_PluginHelper_logEvent_static));
    cls->defineFinalizeFunction(_SE(js_delete_plugin_x_PluginHelper));

    cls->install();
    JSBClassType::registerClass<pluginx::PluginHelper>(cls);
    __jsb_plugin_x_PluginHelper_proto = cls->getProto();
    __jsb_plugin_x_PluginHelper_class = cls;
    se::ScriptEngine::getInstance()->clearException();
    return true;
}

se::Class *__jsb_plugin_x_Console_class = nullptr;
se::Object *__jsb_plugin_x_Console_proto = nullptr;

SE_DECLARE_FINALIZE_FUNC(js_delete_plugin_x_PluginConsole)

static std::string formatArgs(const std::vector<se::Value> &args) {

    std::ostringstream oss;

    for (size_t i = 0; i < args.size(); ++i) {
        const se::Value &v = args[i];

        if (v.isString()) {
            oss << v.toString();
        } else if (v.isBoolean()) {
            oss << (v.toBoolean() ? "true" : "false");
        } else if (v.isNumber() || v.isBigInt()) {
            oss << v.toDouble();
        } else if (v.isObject()) {
            se::Object *obj = v.toObject();
            oss << "[Object " << obj << "]";
        } else if (v.isNull()) {
            oss << "null";
        } else if (v.isUndefined()) {
            oss << "undefined";
        } else {
            oss << "[Unknown]";
        }
    }
    return oss.str();
}

static bool js_plugin_x_PluginConsole_log_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    pluginx::Console::log(formatArgs(args));
    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginConsole_log_static)


static bool js_plugin_x_PluginConsole_debug_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    pluginx::Console::debug(formatArgs(args));
    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginConsole_debug_static)



static bool js_plugin_x_PluginConsole_info_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    pluginx::Console::info(formatArgs(args));
    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginConsole_info_static)



static bool js_plugin_x_PluginConsole_warn_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    pluginx::Console::warn(formatArgs(args));
    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginConsole_warn_static)



static bool js_plugin_x_PluginConsole_error_static(se::State &s) {
    CC_UNUSED bool ok = true;
    const auto &args = s.args();
    pluginx::Console::error(formatArgs(args));
    return true;
}

SE_BIND_FUNC(js_plugin_x_PluginConsole_error_static)

static bool js_new_PluginConsole(se::State &s) // NOLINT(readability-identifier-naming)
{
    pluginx::Console *result;
    result = (pluginx::Console *) new pluginx::Console();

    auto *ptr = JSB_MAKE_PRIVATE_OBJECT_WITH_INSTANCE(result);
    s.thisObject()->setPrivateObject(ptr);
    return true;
}

SE_BIND_CTOR(js_new_PluginConsole, __jsb_plugin_x_Console_class,
             js_delete_plugin_x_PluginConsole)

static bool js_delete_plugin_x_PluginConsole(se::State &s) {
    return true;
}

SE_BIND_FINALIZE_FUNC(js_delete_plugin_x_PluginConsole)

bool js_register_plugin_x_plugin_console(se::Object *obj) {
    auto *cls = se::Class::create("Console", obj, nullptr, _SE(js_new_PluginConsole));

    cls->defineStaticProperty("__isJSB", se::Value(true),
                              se::PropertyAttribute::READ_ONLY | se::PropertyAttribute::DONT_ENUM |
                              se::PropertyAttribute::DONT_DELETE);
    cls->defineStaticFunction("error", _SE(js_plugin_x_PluginConsole_error_static));
    cls->defineStaticFunction("warn", _SE(js_plugin_x_PluginConsole_warn_static));
    cls->defineStaticFunction("debug",
                              _SE(js_plugin_x_PluginConsole_debug_static));
    cls->defineStaticFunction("info",
                              _SE(js_plugin_x_PluginConsole_info_static));
    cls->defineStaticFunction("log", _SE(js_plugin_x_PluginConsole_log_static));
    cls->defineFinalizeFunction(_SE(js_delete_plugin_x_PluginConsole));

    cls->install();
    JSBClassType::registerClass<pluginx::Console>(cls);
    __jsb_plugin_x_Console_proto = cls->getProto();
    __jsb_plugin_x_Console_class = cls;
    se::ScriptEngine::getInstance()->clearException();
    return true;
}

bool register_all_plugin_x(se::Object *obj) // NOLINT(readability-identifier-naming)
{
    // Get the ns
    se::Value nsVal;
    if (!obj->getProperty("pluginx", &nsVal)) {
        se::HandleObject jsobj(se::Object::createPlainObject());
        nsVal.setObject(jsobj);
        obj->setProperty("pluginx", nsVal);
    }
    se::Object *ns = nsVal.toObject();
    js_register_plugin_x_plugin_helper(ns);
    js_register_plugin_x_plugin_console(ns);

    return true;
}
