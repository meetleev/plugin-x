//
// Created by Lee on 2023/7/10.
//

#ifndef PLUGIN_HELPER_H
#define PLUGIN_HELPER_H

#include "PluginMacros.h"
#include "JniUtil.h"

NS_PLUGIN_X_BEGIN

class PluginHelper {
public:
	static jobject addComponent(const char * componentName);
};

NS_PLUGIN_X_END

#endif //PLUGIN_HELPER_H
