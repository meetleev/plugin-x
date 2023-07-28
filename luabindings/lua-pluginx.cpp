//
// Created by Lee on 2023/7/8.
//

#include "lua-pluginx.h"
#include "PluginMgr.h"
#include "ProtocolUser.h"
#include "ProtocolShare.h"
#include "scripting/lua-bindings/manual/CCLuaValue.h"
#include "scripting/lua-bindings/manual/CCLuaEngine.h"
#include "scripting/lua-bindings/manual/tolua_fix.h"
#include "scripting/lua-bindings/manual/LuaBasicConversions.h"

using namespace cocos2d;

USING_NS_PLUGIN_X;

static int lua_PluginMgr_loginPL(lua_State *tolua_S) {
    if (nullptr == tolua_S)
        return 0;

    int argc = 0;
#if COCOS2D_DEBUG >= 1
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PluginMgr", 0, &tolua_err)) goto tolua_lerror;
#endif

    argc = lua_gettop(tolua_S) - 1;
    if (argc == 2) {
#if COCOS2D_DEBUG >= 1
        if (!tolua_isnumber(tolua_S, 2, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 3,"LUA_FUNCTION",0,&tolua_err))
            goto tolua_err;
        else
#endif
        ProtocolUser::LoginPlatformType arg0;
        luaval_to_int32(tolua_S, 2, (int *) &arg0, "pluginx.PluginMgr:loginPL");

        LUA_FUNCTION handler = toluafix_ref_function(tolua_S, 3, 0);
        PluginMgr::loginPL(arg0, [=](int code, const std::string &msg) {
            LuaStack *stack = LuaEngine::getInstance()->getLuaStack();
            stack->pushInt(code);
            stack->pushString(msg.c_str());
            stack->executeFunctionByHandler(handler, 2);
            stack->clean();
        });

        return 1;
    }

    luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n",
               "pluginx.PluginMgr:loginPL", argc, 2);
    return 0;

#if COCOS2D_DEBUG >= 1
    tolua_lerror:
    tolua_error(tolua_S, "#ferror in function 'lua_PluginMgr_loginPL'.\n", &tolua_err);
#endif
    return 0;
}

static int lua_PluginMgr_sharePL(lua_State *tolua_S) {
    if (nullptr == tolua_S)
        return 0;

    int argc = 0;
#if COCOS2D_DEBUG >= 1
    tolua_Error tolua_err;
    if (!tolua_isusertable(tolua_S, 1, "PluginMgr", 0, &tolua_err)) goto tolua_lerror;
#endif

    argc = lua_gettop(tolua_S) - 1;
    if (argc == 3) {
#if COCOS2D_DEBUG >= 1
        if (!tolua_isnumber(tolua_S, 2, 0, &tolua_err) ||
        !tolua_isstring(tolua_S, 3, 0, &tolua_err) ||
        !toluafix_isfunction(tolua_S, 4,"LUA_FUNCTION",0,&tolua_err))
            goto tolua_err;
        else
#endif
        ProtocolShare::SharePlatformType arg0;
        std::string arg1;
        luaval_to_int32(tolua_S, 2, (int *) &arg0, "pluginx.PluginMgr:sharePL");
        luaval_to_std_string(tolua_S, 3, &arg1, "pluginx.PluginMgr:sharePL");

        LUA_FUNCTION handler = toluafix_ref_function(tolua_S, 4, 0);
        PluginMgr::sharePL(arg0, arg1, [=](int code, const std::string &msg) {
            LuaStack *stack = LuaEngine::getInstance()->getLuaStack();
            stack->pushInt(code);
            stack->pushString(msg.c_str());
            stack->executeFunctionByHandler(handler, 2);
            stack->clean();
        });

        return 1;
    }

    luaL_error(tolua_S, "%s has wrong number of arguments: %d, was expecting %d\n",
               "pluginx.PluginMgr:sharePL", argc, 3);
    return 0;

#if COCOS2D_DEBUG >= 1
    tolua_lerror:
    tolua_error(tolua_S, "#ferror in function 'lua_PluginMgr_loginPL'.\n", &tolua_err);
#endif
    return 0;
}

TOLUA_API int lua_register_plugin_x_PluginMgr(lua_State *L) {

    tolua_usertype(L, "pluginx.PluginMgr");
    tolua_cclass(L, "PluginMgr", "pluginx.PluginMgr", "", nullptr);

    tolua_beginmodule(L, "PluginMgr");

    tolua_function(L, "loginPL", lua_PluginMgr_loginPL);
    tolua_function(L, "sharePL", lua_PluginMgr_sharePL);

    tolua_endmodule(L);
    std::string typeName = typeid(PluginMgr).name();
    g_luaType[typeName] = "PluginMgr";
    g_typeCast["PluginMgr"] = "PluginMgr";

    return 1;
}


TOLUA_API int register_all_plugin_x(lua_State *L) {
    lua_getglobal(L, "_G");
    if (lua_istable(L, -1))//stack:...,_G,
    {
        tolua_open(L);

        tolua_module(L, "pluginx", 0);
        tolua_beginmodule(L, "pluginx");

        lua_register_plugin_x_PluginMgr(L);

        tolua_endmodule(L);
    }
    lua_pop(L, 1);
    return 1;
}