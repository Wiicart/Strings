package com.pedestriamc.strings.bukkit.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

final class LegacySchedulerAdapter implements SchedulerAdapter {

    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    @Override
    public void async(Plugin owner, Runnable task) {
        scheduler.runTaskAsynchronously(owner, task);
    }

    @Override
    public void global(Plugin owner, Runnable task) {
        scheduler.runTask(owner, task);
    }

    @Override
    public void globalLater(Plugin owner, Runnable task, long delayTicks) {
        scheduler.runTaskLater(owner, task, delayTicks);
    }

    @Override
    public void globalRepeating(Plugin owner, Runnable task, long initialDelayTicks, long periodTicks) {
        scheduler.runTaskTimer(owner, task, initialDelayTicks, periodTicks);
    }

    @Override
    public void entity(Plugin owner, Entity entity, Runnable task) {
        global(owner, task);
    }

    @Override
    public void entityLater(Plugin owner, Entity entity, Runnable task, long delayTicks) {
        globalLater(owner, task, delayTicks);
    }

    @Override
    public void region(Plugin owner, Location location, Runnable task) {
        global(owner, task);
    }

    @Override
    public void cancel(Plugin owner) {
        scheduler.cancelTasks(owner);
    }

    @Override
    public boolean regionAware() {
        return false;
    }
}
