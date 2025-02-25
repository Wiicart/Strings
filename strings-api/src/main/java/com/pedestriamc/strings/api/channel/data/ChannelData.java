package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.api.channel.Membership;
import org.bukkit.World;

import java.util.Set;

/**
 * A class that holds data to load channels.
 * None of the constructors will create an object sufficient to create a Channel.
 * Enter all data by using all the set methods, with the exceptions of setDistance() and setWorld() if not applicable.
 */
public class ChannelData
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

    public ChannelData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public boolean isDoCooldown() {
        return doCooldown;
    }

    public void setDoCooldown(boolean doCooldown) {
        this.doCooldown = doCooldown;
    }

    public boolean isDoProfanityFilter() {
        return doProfanityFilter;
    }

    public void setDoProfanityFilter(boolean doProfanityFilter) {
        this.doProfanityFilter = doProfanityFilter;
    }

    public boolean isDoUrlFilter() {
        return doUrlFilter;
    }

    public void setDoUrlFilter(boolean doUrlFilter) {
        this.doUrlFilter = doUrlFilter;
    }

    public boolean isCallEvent() {
        return callEvent;
    }

    public void setCallEvent(boolean callEvent) {
        this.callEvent = callEvent;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Set<World> getWorlds() {
        return worlds;
    }

    public void setWorlds(Set<World> worlds) {
        this.worlds = worlds;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getBroadcastFormat() {
        return broadcastFormat;
    }

    public void setBroadcastFormat(String broadcastFormat) {
        this.broadcastFormat = broadcastFormat;
    }
}
