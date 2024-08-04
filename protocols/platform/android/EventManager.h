//
// Created by Lee on 2024/4/3.
//

#ifndef EVENT_MANAGER_H
#define EVENT_MANAGER_H

#include <functional>
#include <map>
#include <vector>
#include <string>
#include "PluginMacros.h"
#include "application/ApplicationManager.h"

NS_PLUGIN_X_BEGIN

    template<typename EventId, typename ... Args>
    class EventManager {
    public:
        using EventHandler = std::function<void(Args...)>;

        static EventManager &Instance() {
            static EventManager instance;
            return instance;
        }

        void addListener(const EventId &eventId, const EventHandler &handler) {
            subscriptionHandlers[eventId].push_back(handler);
        }

        void addOnceListener(const EventId &eventId, const EventHandler &handler) {
            consumptionHandlers[eventId].push_back(handler);
        }

        void removeListener(const EventId &eventId, const EventHandler &handler) {
            auto it = subscriptionHandlers.find(eventId);
            if (it != subscriptionHandlers.end()) {
                auto &callbacks = it->second;
                callbacks.erase(std::remove(callbacks.begin(), callbacks.end(), handler),
                                callbacks.end());
            }
            if (consumptionHandlers.find(eventId) != consumptionHandlers.end()) {
                auto &handlers = consumptionHandlers[eventId];
                handlers.erase(std::remove(handlers.begin(), handlers.end(), handler),
                               handlers.end());
            }
        }

        void removeListener(const EventId &eventId) {
            auto it = subscriptionHandlers.find(eventId);
            if (it != subscriptionHandlers.end()) {
                auto &callbacks = it->second;
                callbacks.clear();
            }
            if (consumptionHandlers.find(eventId) != consumptionHandlers.end()) {
                consumptionHandlers.erase(eventId);
            }
        }

        void removeAllListener() {
            subscriptionHandlers.clear();
            consumptionHandlers.clear();
        }

        // 触发事件
        void emit(const EventId &eventId, Args... args) {
            if (subscriptionHandlers.find(eventId) != subscriptionHandlers.end()) {
                auto &handlers = subscriptionHandlers[eventId];
                CC_CURRENT_ENGINE()->getScheduler()->performFunctionInCocosThread(
                        [handlers, args...]() {
                            for (auto &handler: handlers) {
                                handler(args...);
                            }
                        });
            }
            if (consumptionHandlers.find(eventId) != consumptionHandlers.end()) {
                auto &handlers = consumptionHandlers[eventId];
                CC_CURRENT_ENGINE()->getScheduler()->performFunctionInCocosThread(
                        [handlers, args...]() {
                            for (auto &handler: handlers) {
                                handler(args...);
                            }
                        });
                consumptionHandlers.erase(eventId);
            }
        }

    private:
        std::map<EventId, std::vector<EventHandler>> consumptionHandlers;
        std::map<EventId, std::vector<EventHandler>> subscriptionHandlers;

        EventManager() {}

        EventManager(const EventManager &) = delete; // 禁止拷贝构造
        EventManager &operator=(const EventManager &) = delete; // 禁止赋值运算符
    };

NS_PLUGIN_X_END

#endif //EVENT_MANAGER_H
