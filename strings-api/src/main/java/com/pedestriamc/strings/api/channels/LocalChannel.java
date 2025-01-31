package com.pedestriamc.strings.api.channels;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * A Channel interface for WorldChannels and ProximityChannels. This interface provides methods unique to these classes.
 * getProximity() and setProximity() are only supported by ProximityChannel instances. To check this, you can call getType() before calling these methods.
 */
public interface LocalChannel extends Channel {

    /**
     * Casts a Channel to a LocalChannel if it implements LocalChannel
     * @param channel The Channel to be cast
     * @return A LocalChannel if possible, otherwise null
     */
    @Nullable
    static LocalChannel of(Channel channel) {
        if(channel instanceof LocalChannel localChannel) {
            return localChannel;
        }
        return null;
    }

    /**
     * Provides a Set of all the worlds the Channel is used in.
     * @return A populated Set.
     */
    Set<World> getWorlds();

    /**
     * Provides a List containing the names of all the worlds from getWorlds();
     * @return A populated List.
     */
    List<String> getWorldNames();

    /**
     * If this is a instance of a ProximityChannel, this will provide the proximity the Channel is set to.
     * @return A double of the proximity (max distance for players to receive a message).
     * @throws UnsupportedOperationException If this is an instance of a WorldChannel, this exception will be thrown. Before calling, check LocalChannel.getType();
     */
    @SuppressWarnings("unused")
    double getProximity() throws UnsupportedOperationException;

    @SuppressWarnings("unused")
    void setProximity(double proximity) throws UnsupportedOperationException;

}
