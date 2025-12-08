package com.pedestriamc.strings.impl;

import com.pedestriamc.common.event.StringsEventDispatcher;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.event.strings.StringsEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class StringsBukkitEventDispatcher extends StringsEventDispatcher {

    private final Strings strings;

    public StringsBukkitEventDispatcher(@NotNull Strings strings) {
        super(strings);
        this.strings = strings;
    }

    @Override
    public void dispatch(@NotNull StringsEvent event) {
        if (event instanceof Event bukkitEvent) {
            strings.sync(() -> {
                strings.getServer().getPluginManager().callEvent(bukkitEvent);
                super.dispatch(event);
            });
        }
    }

}
