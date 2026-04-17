package com.pedestriamc.strings.api.channel.local;

import com.pedestriamc.strings.api.channel.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * ProximityChannels determine message recipients by selecting players within a defined range.
 * While ProximityChannels can contain multiple worlds in scope,
 * distance calculations only account for players in the same world as the sender.
 * There are two methods of determining proximity, defined in {@link Proximity}.
 * @param <T> The platform "locality" (world) type.
 * @see LocalChannel
 * @see Locality
 * @see Proximity
 */
public interface ProximityChannel<T> extends LocalChannel<T> {

    /**
     * Provides the current proximity range for players to receive messages sent.
     * @return A double representing the maximum allowed distance.
     */
    @Range(from = -1, to = Integer.MAX_VALUE)
    double getProximity();

    /**
     * Updates this channel's proximity range,
     * which represents the maximum distance a player can be from a message sender and still receive the message.
     * @param proximity The proximity to set the channel to
     */
    void setProximity(@Range(from = -1, to = Integer.MAX_VALUE) double proximity);

    /**
     * Tells how proximity is calculated when determining recipients.
     * @see Proximity
     * @return The {@link Proximity} type.
     */
    @NotNull
    Proximity proximityType();

    @NotNull
    @Override
    default Type getType() {
        return Type.PROXIMITY;
    }

    /**
     * Represents how proximity is calculated when determining message recipients.
     */
    enum Proximity {
        /**
         * Proximity calculations only factor the X and Z axis's, disregarding the Y-axis/height.
         */
        HORIZONTAL,
        /**
         * Proximity calculations factor X, Y, and Z axis's, reducing the overall horizontal range.
         */
        SPHERICAL
    }
}
