package com.pedestriamc.strings.bukkit.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/** Uses public Paper/Folia scheduler methods without linking Spigot to them. */
final class RegionSchedulerAdapter implements SchedulerAdapter {

    private final Object async;
    private final Object global;
    private final Object region;

    RegionSchedulerAdapter() {
        Object server = Bukkit.getServer();
        async = invoke(server, "getAsyncScheduler");
        global = invoke(server, "getGlobalRegionScheduler");
        region = invoke(server, "getRegionScheduler");
    }

    @Override
    public void async(Plugin owner, Runnable task) {
        invoke(async, "runNow", owner, consumer(task));
    }

    @Override
    public void global(Plugin owner, Runnable task) {
        invoke(global, "execute", owner, task);
    }

    @Override
    public void globalLater(Plugin owner, Runnable task, long delayTicks) {
        invoke(global, "runDelayed", owner, consumer(task), delayTicks);
    }

    @Override
    public void globalRepeating(Plugin owner, Runnable task, long initialDelayTicks, long periodTicks) {
        invoke(global, "runAtFixedRate", owner, consumer(task), initialDelayTicks, periodTicks);
    }

    @Override
    public void entity(Plugin owner, Entity entity, Runnable task) {
        Object scheduler = invoke(entity, "getScheduler");
        invoke(scheduler, "run", owner, consumer(task), (Runnable) () -> { });
    }

    @Override
    public void entityLater(Plugin owner, Entity entity, Runnable task, long delayTicks) {
        Object scheduler = invoke(entity, "getScheduler");
        invoke(scheduler, "runDelayed", owner, consumer(task), (Runnable) () -> { }, delayTicks);
    }

    @Override
    public void region(Plugin owner, Location location, Runnable task) {
        invoke(region, "execute", owner, location, task);
    }

    @Override
    public void cancel(Plugin owner) {
        invoke(async, "cancelTasks", owner);
        invoke(global, "cancelTasks", owner);
    }

    @Override
    public boolean regionAware() {
        return true;
    }

    private static Consumer<Object> consumer(Runnable task) {
        return ignored -> task.run();
    }

    private static Object invoke(Object target, String methodName, Object... arguments) {
        if (target == null) {
            throw new IllegalStateException("Scheduler target is unavailable: " + methodName);
        }
        Method selected = null;
        for (Method method : target.getClass().getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == arguments.length) {
                selected = method;
                break;
            }
        }
        if (selected == null) {
            throw new IllegalStateException("Scheduler method is unavailable: " + methodName);
        }
        try {
            return selected.invoke(target, arguments);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to access scheduler method: " + methodName, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new IllegalStateException("Scheduler method failed: " + methodName, cause);
        }
    }
}
