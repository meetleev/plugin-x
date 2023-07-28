//
// Created by Lee on 2023/7/8.
//

#ifndef LUA_PLUGIN_MGR_H
#define LUA_PLUGIN_MGR_H

#ifdef __cplusplus
extern "C" {
#endif
#include "tolua++.h"
#ifdef __cplusplus
}
#endif

TOLUA_API int register_all_plugin_x(lua_State* L);

#endif //LUA_PLUGIN_MGR_H
