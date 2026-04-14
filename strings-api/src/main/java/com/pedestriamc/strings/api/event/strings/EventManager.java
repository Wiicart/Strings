package com.pedestriamc.strings.api.event.strings;

import org.jetbrains.annotations.NotNull;

public interface EventManager {

    /**
     * Registers an object as a listener.
     * All methods in the object annotated with {@link Listener}
     * will be called when an event of the appropriate type is called.
     * @param listeners The Objects listening
     */
    void subscribe(@NotNull Object... listeners);

    /**
     * Unregisters a listener Object
     * @param listeners The listeners to unregister
     */
    void unsubscribe(@NotNull Object... listeners);

    /**
     * Dispatches a {@link StringsEvent}
     * @param event The event to dispatch
     */
    void dispatch(@NotNull StringsEvent event);
}
