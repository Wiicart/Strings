package com.pedestriamc.strings.bukkit.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * Small platform boundary for tasks which must run on a particular Bukkit
 * execution context. Implementations deliberately hide the Paper/Folia API
 * from the rest of the plugin.
 */
public interface SchedulerAdapter {

    void async(Plugin owner, Runnable task);

    void global(Plugin owner, Runnable task);

    void globalLater(Plugin owner, Runnable task, long delayTicks);

    void globalRepeating(Plugin owner, Runnable task, long initialDelayTicks, long periodTicks);

    void entity(Plugin owner, Entity entity, Runnable task);

    void entityLater(Plugin owner, Entity entity, Runnable task, long delayTicks);

    void region(Plugin owner, Location location, Runnable task);

    void cancel(Plugin owner);

    boolean regionAware();
}
