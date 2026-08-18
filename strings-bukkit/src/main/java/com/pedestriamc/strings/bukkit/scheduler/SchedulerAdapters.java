package com.pedestriamc.strings.bukkit.scheduler;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public final class SchedulerAdapters {

    private SchedulerAdapters() { }

    /**
     * Selects by public server capability, rather than by a fork name or an
     * implementation class. This also keeps the plugin loadable on Spigot.
     */
    public static SchedulerAdapter create(Plugin plugin) {
        Server server = plugin.getServer();
        if (hasMethod(server, "getAsyncScheduler")
                && hasMethod(server, "getGlobalRegionScheduler")
                && hasMethod(server, "getRegionScheduler")) {
            try {
                return new RegionSchedulerAdapter();
            } catch (LinkageError | RuntimeException ignored) {
                plugin.getLogger().warning("Paper region schedulers are unavailable; using Bukkit scheduler.");
            }
        }
        return new LegacySchedulerAdapter();
    }

    private static boolean hasMethod(Server server, String name) {
        try {
            Method method = server.getClass().getMethod(name);
            return method.getParameterCount() == 0;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }
}
