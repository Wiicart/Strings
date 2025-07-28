package com.pedestriamc.strings.api.channel;

import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This signifies a {@link Channel} can be Monitored using /channel monitor <channel>.
 * Monitors are not members of the Channel, but will receive all messages from the Channel.
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
    @NotNull
    Set<StringsUser> getMonitors();

    /**
     * For internal usage - Use {@link StringsUser#monitor(Monitorable)}
     * Adds a monitor
     * @param stringsUser The User to be added
     */
    @ApiStatus.Internal
    void addMonitor(@NotNull StringsUser stringsUser);

    /**
     * For internal usage - Use {@link StringsUser#unmonitor(Monitorable)}
     * Removes a monitor
     * @param stringsUser The User to be removed
     */
    @ApiStatus.Internal
    void removeMonitor(@NotNull StringsUser stringsUser);

}
