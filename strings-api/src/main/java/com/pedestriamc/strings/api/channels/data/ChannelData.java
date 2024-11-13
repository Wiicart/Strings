package com.pedestriamc.strings.api.channels.data;

import com.pedestriamc.strings.api.Membership;

/**
 * A class that holds data to load channels.
 * Enter all fields before using, otherwise unexpected behavior may occur.
 */
public class ChannelData
{

    private String name;
    private String defaultColor;
    private String format;
    private Membership membership;
    private boolean doCooldown;
    private boolean doProfanityFilter;
    private boolean doUrlFilter;
    private boolean callEvent;
    private int priority;

    public ChannelData(){}

    public ChannelData(String name, String defaultColor, String format, Membership membership, boolean doCooldown, boolean doUrlFilter, boolean doProfanityFilter, boolean callEvent, int priority) {
        this.name = name;
        this.defaultColor = defaultColor;
        this.format = format;
        this.membership = membership;
        this.doCooldown = doCooldown;
        this.doUrlFilter = doUrlFilter;
        this.doProfanityFilter = doProfanityFilter;
        this.callEvent = callEvent;
        this.priority = priority;
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

}
