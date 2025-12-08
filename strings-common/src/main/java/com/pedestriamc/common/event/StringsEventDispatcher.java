package com.pedestriamc.common.event;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.event.strings.EventDispatcher;
import com.pedestriamc.strings.api.event.strings.Listener;
import com.pedestriamc.strings.api.event.strings.StringsEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StringsEventDispatcher implements EventDispatcher {

    private final StringsPlatform strings;
    private final Map<Class<? extends StringsEvent>, Set<Subscription>> subscriptionMap = new ConcurrentHashMap<>();
    private final Map<Class<? extends StringsEvent>, Set<Class<?>>> typeMap = new ConcurrentHashMap<>();

    public StringsEventDispatcher(@NotNull StringsPlatform strings) {
        this.strings = strings;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void subscribe(@NotNull Object... listener) {
        for (Object o : listener) {
            Set<Method> methods = extractListenerMethods(o.getClass());
            for (Method method : methods) {
                Class<?> clazz = method.getParameters()[0].getType();
                Set<Subscription> set = subscriptionMap.computeIfAbsent(
                        (Class<? extends StringsEvent>) clazz,
                        t -> ConcurrentHashMap.newKeySet()
                );

                set.add(new Subscription(o, method));
            }
        }
    }

    @Override
    public void unsubscribe(@NotNull Object... listeners) {
        for (Object o : listeners) {
            for (Map.Entry<Class<? extends StringsEvent>, Set<Subscription>> entry : subscriptionMap.entrySet()) {
                entry.getValue().removeIf(subscription -> subscription.listener == o);
            }
        }
    }

    @Override
    public void dispatch(@NotNull StringsEvent event) {
        Class<? extends StringsEvent> clazz = event.getClass();
        for (Class<?> c : getAllClassTypes(clazz)) {
            Set<Subscription> subscriptions = subscriptionMap.get(c);
            if (subscriptions != null) {
                for (Subscription subscription : subscriptions) {
                    Method method = subscription.method();
                    Object object = subscription.listener();

                    try {
                        method.invoke(object, event);
                    } catch (Exception e) {
                        strings.warning(String.format("Failed to pass event to listener: %s", object.getClass().getSimpleName()));
                        strings.warning(e.getMessage());
                    }
                }
            }
        }
    }

    @NotNull
    private Set<Method> extractListenerMethods(@NotNull Class<?> clazz) {
        Set<Method> methods = new HashSet<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Listener.class)
                    && method.getParameterCount() == 1
                    && StringsEvent.class.isAssignableFrom(method.getParameterTypes()[0])
            ) {
                try {
                    method.setAccessible(true);
                    methods.add(method);
                } catch (Exception ignored) {
                    String classAndMethod = clazz.getSimpleName() + "#" + method.getName();
                    strings.warning(String.format(
                            "Warning: Strings is unable to pass events to listener method %s",
                            classAndMethod)
                    );
                }
            }
        }

        return methods;
    }

    private Set<Class<?>> getAllClassTypes(@NotNull Class<? extends StringsEvent> clazz) {
        return typeMap.computeIfAbsent(clazz, c -> {
            Set<Class<?>> classes = new HashSet<>();
            Class<?> current = clazz;
            while (current != null && !current.equals(Object.class)) {
                classes.add(current);
                classes.addAll(List.of(current.getInterfaces()));
                current = current.getSuperclass();
            }

            return classes;
        });
    }

    private record Subscription(Object listener, Method method) {}

}
