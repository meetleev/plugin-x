//
// Created by Lee on 2025/11/20.
//

#ifndef PLUGIN_CONSOLE_H
#define PLUGIN_CONSOLE_H

#include <vector>
#include <string>

#define  LOG_TAG    "PluginX"

//#define CONSOLE_ENABLED 1

inline std::string format_log(const char* fmt, ...) {
    // 线程局部 buffer，可复用，避免频繁分配
    thread_local std::vector<char> buffer(512);

    va_list args;
    va_start(args, fmt);

    // 尝试写入当前 buffer
    int needed = vsnprintf(buffer.data(), buffer.size(), fmt, args);

    if (needed < 0) {
        va_end(args);
        return ""; // 格式化错误
    }

    // buffer 不够大，扩容并重试
    if ((size_t)needed >= buffer.size()) {
        buffer.resize(needed + 1);
        va_end(args);
        va_start(args, fmt);
        vsnprintf(buffer.data(), buffer.size(), fmt, args);
    }

    va_end(args);

    return std::string{buffer.data(), static_cast<size_t>(needed)};
}

#ifdef  ANDROID
#include <android/log.h>

template<typename... Args>
inline void log_write_fmt(int priority, const char* tag, const char* fmt, Args&&... args) {
#ifdef CONSOLE_ENABLED
    std::string s_log = format_log(fmt, args...);
    __android_log_write(priority, tag, s_log.c_str());
#endif
}
#define LOGV(...) log_write_fmt(ANDROID_LOG_VERBOSE,  LOG_TAG,  __VA_ARGS__)
#define LOGD(...) log_write_fmt(ANDROID_LOG_DEBUG,    LOG_TAG,  __VA_ARGS__)
#define LOGI(...) log_write_fmt(ANDROID_LOG_INFO,     LOG_TAG,  __VA_ARGS__)
#define LOGW(...) log_write_fmt(ANDROID_LOG_WARN,     LOG_TAG,  __VA_ARGS__)
#define LOGE(...) log_write_fmt(ANDROID_LOG_ERROR,    LOG_TAG,  __VA_ARGS__)
#else
#define LOGV(...) ((void)0)
#define LOGD(...) ((void)0)
#define LOGI(...) ((void)0)
#define LOGW(...) ((void)0)
#define LOGE(...) ((void)0)
#endif

NS_PLUGIN_X_BEGIN
    class Console {
    public:
        template<typename... Args>
        static void log(Args &&... args) {
            LOGV("%s", _makeString(std::forward<Args>(args)...).c_str());
        }

        template<typename... Args>
        static void info(Args &&... args) {
            LOGI("%s", _makeString(std::forward<Args>(args)...).c_str());
        }

        template<typename... Args>
        static void debug(Args &&... args) {
            LOGD("%s", _makeString(std::forward<Args>(args)...).c_str());
        }

        template<typename... Args>
        static void warn(Args &&... args) {
            LOGW("%s", _makeString(std::forward<Args>(args)...).c_str());
        }

        template<typename... Args>
        static void error(Args &&... args) {
            LOGE("%s", _makeString(std::forward<Args>(args)...).c_str());
        }

    private:
        template<typename T>
        static void _append(std::ostringstream &oss, T &&value) {
            oss << value;
        }

        template<typename T, typename... Args>
        static void _append(std::ostringstream &oss, T &&first, Args &&... rest) {
            oss << first << " ";
            _append(oss, std::forward<Args>(rest)...);
        }

        template<typename... Args>
        static std::string _makeString(Args &&... args) {
            std::ostringstream oss;
            _append(oss, std::forward<Args>(args)...);
            return oss.str();
        }
    };
NS_PLUGIN_X_END

#endif //PLUGIN_CONSOLE_H
