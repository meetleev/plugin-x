cmake_minimum_required(VERSION 3.6)

set(CMAKE_CXX_STANDARD 17)

option(CCX_USE_CREATOR "Enable Cocos Creator" ON)
option(CCX_USE_LUA  "Enable LUA" OFF)

if (CCX_USE_CREATOR)
	set(CCX_USE_LUA, OFF)
endif()

set(lib_name plugin_x)

project(${lib_name} VERSION 1.0.0 LANGUAGES CXX C)

set(plugin_x_sources)

set(plugin_x_headers ${CMAKE_CURRENT_LIST_DIR}/protocols/include)

set(cc_external_header)

if (DEFINED CMAKE_SYSTEM_NAME)
	if (CMAKE_SYSTEM_NAME MATCHES "iOS")
	    set(cc_external_header ${COCOS_X_PATH}/external/iOS/include/v8)
		# if (DEFINED CMAKE_OSX_ARCHITECTURES)
		# 	string(REPLACE ";" "-" archs "${CMAKE_OSX_ARCHITECTURES}")
		# 	set(library_name "${library_name}-${archs}")
		# endif()
		# set(IOS_FORCE_LOADS
		# 	${CMAKE_CURRENT_SOURCE_DIR}/iOS/sdk/libBuglyAgent.a
		# 	${CMAKE_CURRENT_SOURCE_DIR}/iOS/sdk/Bugly.framework/Bugly)
	elseif (CMAKE_SYSTEM_NAME MATCHES "Android")
	    set(cc_external_header ${COCOS_X_PATH}/external/android/${ANDROID_ABI}/include/v8)
		if (CCX_USE_CREATOR)
			set(android_source
				${CMAKE_CURRENT_LIST_DIR}/protocols/platform/android/EventManager.h
				${CMAKE_CURRENT_LIST_DIR}/protocols/platform/android/JniUtil.h
				${CMAKE_CURRENT_LIST_DIR}/protocols/platform/android/JniUtil.cpp
				${CMAKE_CURRENT_LIST_DIR}/protocols/platform/android/SDKComponentHelper.h
				${CMAKE_CURRENT_LIST_DIR}/protocols/platform/android/SDKComponentHelper.cpp
				${CMAKE_CURRENT_LIST_DIR}/protocols/platform/android/PluginHelper.cpp
				${CMAKE_CURRENT_LIST_DIR}/protocols/platform/android/SDKEventManager.h)
		else()
			file(GLOB android_source ${CMAKE_CURRENT_LIST_DIR}/protocols/platform/android-2dx/*.cpp)
		endif()
		list(APPEND plugin_x_sources ${android_source})
		# set(LIB_SHARED_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/android/prebuilt/${CMAKE_ANDROID_ARCH_ABI})
	endif()
endif()

if (CCX_USE_CREATOR)
	set(js_binding_sources
		${CMAKE_CURRENT_LIST_DIR}/js-bindings/js-pluginx.h
		${CMAKE_CURRENT_LIST_DIR}/js-bindings/js-pluginx.cpp)
	list(APPEND plugin_x_sources ${js_binding_sources})
	list(APPEND plugin_x_headers ${CMAKE_CURRENT_LIST_DIR}/protocols/js-bindings)
else()
	if (CCX_USE_LUA)
	    list(APPEND plugin_x_headers ${CMAKE_CURRENT_SOURCE_DIR}/luabindings)
	    file(GLOB lua_source luabindings/*.cpp)
	    list(APPEND plugin_x_sources ${lua_source})
	else()
	#    list(APPEND plugin_x_headers ${CMAKE_CURRENT_SOURCE_DIR}/jsbindings)
	#    file(GLOB js_source jsbindings/*.cpp)
	#    list(APPEND plugin_x_sources ${js_source})
	endif()
endif()

add_library(${lib_name} STATIC ${plugin_x_sources})

# if (DEFINED IOS_FORCE_LOADS)
# 	set_property(TARGET ${lib_name} APPEND PROPERTY BUGLY_IOS_FORCE_LOADS "${IOS_FORCE_LOADS}")
# endif()

# if (DEFINED LIB_SHARED_DIRECTORY)
# 	file(COPY "${LIB_SHARED_DIRECTORY}/libBugly.so" DESTINATION ${CMAKE_LIBRARY_OUTPUT_DIRECTORY})
# endif()


if (CCX_USE_CREATOR)
	if (NOT DEFINED COCOS_X_PATH)
	    message(FATAL_ERROR "COCOS_X_PATH is not set!")
	endif()
	target_include_directories(${lib_name}
		PUBLIC ${plugin_x_headers}
		PRIVATE ${COCOS_X_PATH}
		PRIVATE ${COCOS_X_PATH}/cocos
		# removed in the future
		PRIVATE ${COCOS_X_PATH}/cocos/renderer
		PRIVATE ${COCOS_X_PATH}/cocos/bindings/jswrapper
		PRIVATE ${cc_external_header}
		)
else()
	if (CCX_USE_LUA)
		if (NOT DEFINED COCOS2DX_ROOT_PATH)
		    message(FATAL_ERROR "COCOS2DX_ROOT_PATH is not set!")
		endif()

		target_include_directories(${lib_name}
			PUBLIC ${plugin_x_headers}
			PRIVATE ${COCOS2DX_ROOT_PATH}/cocos
		    PRIVATE ${COCOS2DX_ROOT_PATH}/external
			PRIVATE ${COCOS2DX_ROOT_PATH}/external/lua
			PRIVATE ${COCOS2DX_ROOT_PATH}/external/lua/tolua
			PRIVATE ${COCOS2DX_ROOT_PATH}/external/lua/luajit/include
			)
	endif()
endif()

set_target_properties(${lib_name}
    PROPERTIES
    ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
    FOLDER "Plugins"
)