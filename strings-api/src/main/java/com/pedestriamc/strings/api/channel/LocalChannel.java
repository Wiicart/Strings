package com.pedestriamc.strings.api.channel;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * A Channel interface for WorldChannels and ProximityChannels. This interface provides methods unique to these classes.
 * getProximity() and setProximity() are only supported by ProximityChannel instances. To check this, you can call getType() before calling these methods.
 */
@SuppressWarnings("unused")
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
     * Checks if a Player is in the scope of the Channel.
     * A player not being in scope does not necessarily mean they can't send or receive messages from the Channel.
     * @param player The Player to check
     * @return If the Player is in scope
     */
    default boolean isWithinScope(Player player) {
        World senderWorld = player.getWorld();
        for(World world : getWorlds()) {
            if(senderWorld.equals(world)) {
                return true;
            }
        }
        return false;
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
     * If this is an instance of a ProximityChannel, this will provide the proximity the Channel is set to.
     * Check if this is an instance of ProximityChannel with LocalChannel.getType()
     * @return A double of the proximity (max distance for players to receive a message).
     * @throws UnsupportedOperationException If this is an instance of a WorldChannel, this exception will be thrown. Before calling, check LocalChannel.getType();
     */
    double getProximity() throws UnsupportedOperationException;

    /**
     * Sets the Proximity of the Channel if it's an instance of ProximityChannel.
     * Check if this is an instance of ProximityChannel with LocalChannel.getType()
     * @param proximity The proximity to set the Channel to
     * @throws UnsupportedOperationException If the Channel is not an instance of ProximityChannel
     */
    void setProximity(double proximity) throws UnsupportedOperationException;

}
