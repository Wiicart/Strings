package com.pedestriamc.strings.api.channels;

import org.bukkit.entity.Player;

import java.util.Set;

/**
 * This signifies a Channel can be Monitored using /channel monitor <channel>.
 * Monitors are not members of the Channel, but will receive messages from the Channel.
 */
public interface Monitorable extends Channel {

    /**
     * Adds a monitor
     * @param player The Player to be added
     */
    void addMonitor(Player player);

    /**
     * Removes a monitor
     * @param player The Player to be removed
     */
    void removeMonitor(Player player);

    /**
     * Provides a Set of all monitors
     * @return A populated Set of Players
     */
    Set<Player> getMonitors();
}
