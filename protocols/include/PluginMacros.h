//
// Created by Lee on 2023/7/10.
//

#ifndef PLUGIN_MACROS_H
#define PLUGIN_MACROS_H

#ifdef __cplusplus
#define NS_PLUGIN_X_BEGIN                     namespace pluginx {
    #define NS_PLUGIN_X_END                       }
    #define USING_NS_PLUGIN_X                     using namespace pluginx
    #define NS_PLUGIN_X                           ::pluginx
#else
#define NS_PLUGIN_X_BEGIN
#define NS_PLUGIN_X_END
#define USING_NS_PLUGIN_X
#define NS_PLUGIN_X
#endif

#endif //PLUGIN_MACROS_H
