package com.pedestriamc.strings.api.channels;

import org.bukkit.World;

import java.util.List;
import java.util.Set;

/**
 * A Channel interface for WorldChannels and ProximityChannels. This interface provides methods unique to these classes.
 * getProximity() and setProximity() are only supported by ProximityChannel instances. To check this, you can call getType() before calling these methods.
 */
public interface LocalChannel extends Channel {

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
     * @throws UnsupportedOperationException If this is an instance of a WorldChannel, this exception will be thrown. Before calling, check LocalChannel#getType();
     */
    double getProximity() throws UnsupportedOperationException;

    void setProximity(double proximity) throws UnsupportedOperationException;

}
