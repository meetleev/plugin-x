cmake_minimum_required(VERSION 3.6)

set(lib_name SdkBox)

project(${lib_name} VERSION 1.0.0 LANGUAGES CXX C)

set(sdkBox_sources)

set(sdkBox_headers ${CMAKE_CURRENT_SOURCE_DIR}/protocols/include)

if (DEFINED CMAKE_SYSTEM_NAME)
	if (CMAKE_SYSTEM_NAME MATCHES "iOS")
		# if (DEFINED CMAKE_OSX_ARCHITECTURES)
		# 	string(REPLACE ";" "-" archs "${CMAKE_OSX_ARCHITECTURES}")
		# 	set(library_name "${library_name}-${archs}")
		# endif()
		# set(IOS_FORCE_LOADS
		# 	${CMAKE_CURRENT_SOURCE_DIR}/iOS/sdk/libBuglyAgent.a
		# 	${CMAKE_CURRENT_SOURCE_DIR}/iOS/sdk/Bugly.framework/Bugly)
	elseif (CMAKE_SYSTEM_NAME MATCHES "Android")
		file(GLOB android_source protocols/platform/android/*.cpp)
		list(APPEND sdkBox_sources ${android_source})
		# set(LIB_SHARED_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/android/prebuilt/${CMAKE_ANDROID_ARCH_ABI})
	endif()
endif()

list(APPEND sdkBox_headers ${CMAKE_CURRENT_SOURCE_DIR}/luabindings)

file(GLOB lua_source luabindings/*.cpp)
list(APPEND sdkBox_sources ${lua_source})

add_library(${lib_name} STATIC ${sdkBox_sources})

# if (DEFINED IOS_FORCE_LOADS)
# 	set_property(TARGET ${lib_name} APPEND PROPERTY BUGLY_IOS_FORCE_LOADS "${IOS_FORCE_LOADS}")
# endif()

# if (DEFINED LIB_SHARED_DIRECTORY)
# 	file(COPY "${LIB_SHARED_DIRECTORY}/libBugly.so" DESTINATION ${CMAKE_LIBRARY_OUTPUT_DIRECTORY})
# endif()

if (NOT DEFINED COCOS2DX_ROOT_PATH)
	set(COCOS2DX_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR}/../../Cocos2dx@400)
endif()

target_include_directories(${lib_name}
	PUBLIC ${sdkBox_headers}
	PRIVATE ${COCOS2DX_ROOT_PATH}/cocos
    PRIVATE ${COCOS2DX_ROOT_PATH}/external
	PRIVATE ${COCOS2DX_ROOT_PATH}/external/lua
	PRIVATE ${COCOS2DX_ROOT_PATH}/external/lua/tolua
	PRIVATE ${COCOS2DX_ROOT_PATH}/external/lua/luajit/include
	)

set_target_properties(${lib_name}
    PROPERTIES
    ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
    FOLDER "Plugins"
)