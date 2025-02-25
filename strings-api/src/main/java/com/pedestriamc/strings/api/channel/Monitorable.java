package com.pedestriamc.strings.api.channel;

import com.pedestriamc.strings.api.StringsUser;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * This signifies a Channel can be Monitored using /channel monitor <channel>.
 * Monitors are not members of the Channel, but will receive messages from the Channel.
 */
public interface Monitorable extends Channel {

    /**
     * Casts the Channel to a Monitorable if compatible.
     * @param channel The Channel to be cast
     * @return A Monitorable or null if not compatible.
     */
    static Monitorable of(Channel channel) {
        if(channel instanceof Monitorable monitorable) {
            return monitorable;
        }
        return null;
    }

    /**
     * Provides a Set of all monitors
     * @return A populated Set of Players
     */
    Set<Player> getMonitors();

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

    @SuppressWarnings("unused")
    default void addMonitor(StringsUser stringsUser) {
        addMonitor(stringsUser.getPlayer());
    }

    @SuppressWarnings("unused")
    default void removeMonitor(StringsUser stringsUser) {
        removeMonitor(stringsUser.getPlayer());
    }

}
