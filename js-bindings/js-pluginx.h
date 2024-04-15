#ifndef PLUGIN_X_H
#define PLUGIN_X_H

#include "cocos/bindings/jswrapper/SeApi.h"
#include "cocos/bindings/manual/jsb_conversions.h"
#include "PluginHelper.h"

JSB_REGISTER_OBJECT_TYPE(pluginx::PluginHelper);
extern se::Object *__jsb_plugin_x_PluginHelper_proto;
extern se::Class * __jsb_plugin_x_PluginHelper_class;

bool register_all_plugin_x(se::Object *obj);

#endif // PLUGIN_X_H