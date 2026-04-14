package com.pedestriamc.strings.bukkit;

import com.pedestriamc.strings.common.event.StringsEventManager;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.event.strings.StringsEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class StringsBukkitEventManager extends StringsEventManager {

    private final Strings strings;

    public StringsBukkitEventManager(@NotNull Strings strings) {
        super(strings);
        this.strings = strings;
    }

    @Override
    public void dispatch(@NotNull StringsEvent event) {
        strings.sync(() -> {
            if (event instanceof Event bukkitEvent) {
                strings.getServer().getPluginManager().callEvent(bukkitEvent);
            }
            super.dispatch(event);
        });
    }

}
