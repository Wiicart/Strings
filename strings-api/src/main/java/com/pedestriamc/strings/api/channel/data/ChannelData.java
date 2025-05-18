package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.api.channel.Membership;
import org.bukkit.World;

import java.util.Set;

/**
 * A class that holds data to create a new Channel.
 * Enter all data by using all the set methods, with the exceptions of setDistance() and setWorld() if not applicable.
 */
public final class ChannelData
{
    private String name;
    private String defaultColor;
    private String format;
    private String broadcastFormat;
    private Membership membership;
    private boolean doCooldown;
    private boolean doProfanityFilter;
    private boolean doUrlFilter;
    private boolean callEvent;
    private int priority;
    private double distance;
    private Set<World> worlds;

    /**
     * Default constructor for this class.
     * All values are null when using this constructor, you must later define them with methods.
     */
    public ChannelData() {}

    /**
     * Constructor with a param for the name of the Channel.
     * @param name The Channel name
     */
    public ChannelData(String name) {
        this.name = name;
    }

    /**
     * Provides the Channel name.
     * @return The Chanel name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Channel's name.
     * @param name The new Channel name.
     */
    public ChannelData setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Provides the Channel's default color.
     * @return A String representation of the default color.
     */
    public String getDefaultColor() {
        return defaultColor;
    }

    /**
     * Sets the Channel's default color.
     * @param defaultColor The new default color.
     */
    public ChannelData setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
        return this;
    }

    /**
     * Provides the Channel's format when messages are sent in chat.
     * @return A String containing the format.
     */
    public String getFormat() {
        return format;
    }

    public ChannelData setFormat(String format) {
        this.format = format;
        return this;
    }

    public Membership getMembership() {
        return membership;
    }

    public ChannelData setMembership(Membership membership) {
        this.membership = membership;
        return this;
    }

    public boolean isDoCooldown() {
        return doCooldown;
    }

    public ChannelData setDoCooldown(boolean doCooldown) {
        this.doCooldown = doCooldown;
        return this;
    }

    public boolean isDoProfanityFilter() {
        return doProfanityFilter;
    }

    public ChannelData setDoProfanityFilter(boolean doProfanityFilter) {
        this.doProfanityFilter = doProfanityFilter;
        return this;
    }

    public boolean isDoUrlFilter() {
        return doUrlFilter;
    }

    public ChannelData setDoUrlFilter(boolean doUrlFilter) {
        this.doUrlFilter = doUrlFilter;
        return this;
    }

    public boolean isCallEvent() {
        return callEvent;
    }

    public ChannelData setCallEvent(boolean callEvent) {
        this.callEvent = callEvent;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public ChannelData setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public Set<World> getWorlds() {
        return worlds;
    }

    public ChannelData setWorlds(Set<World> worlds) {
        this.worlds = worlds;
        return this;
    }

    /**
     * Provides the proximity that messages will be sent around a player.
     * @return A double.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the proximity that messages will be sent around a player.
     * Only applicable to ProximityChannel.
     * @param distance The double value of the distance.
     */
    public ChannelData setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public String getBroadcastFormat() {
        return broadcastFormat;
    }

    public ChannelData setBroadcastFormat(String broadcastFormat) {
        this.broadcastFormat = broadcastFormat;
        return this;
    }
}
