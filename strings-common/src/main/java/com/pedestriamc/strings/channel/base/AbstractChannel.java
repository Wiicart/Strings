package com.pedestriamc.strings.channel.base;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.user.UserManager;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractChannel implements Channel, Monitorable {

    protected static final String CHANNEL_PERMISSION = "strings.channels.";
    protected static final String MESSAGE_PLACEHOLDER = "{message}";
    protected static final String DEFAULT_BROADCAST_FORMAT = "&8[&3Broadcast&8] &f{message}";

    private final UserManager userManager;

    private String name;
    private String defaultColor;
    private String format;

    private final String broadcastFormat;
    private final Membership membership;

    private boolean doCooldown;
    private boolean doProfanityFilter;
    private boolean doUrlFilter;
    private boolean allowMessageDeletion;

    private @Nullable Sound broadcastSound;

    private final boolean callEvent;

    private final int priority;

    private final Set<StringsUser> monitors = new HashSet<>();
    private final Set<StringsUser> members = new HashSet<>();

    protected AbstractChannel(@NotNull StringsPlatform strings, @NotNull IChannelBuilder<?> data) {
        userManager = strings.users();
        name = data.getName();
        defaultColor = data.getDefaultColor();
        format = data.getFormat();
        broadcastFormat = data.getBroadcastFormat();
        membership = data.getMembership();
        doCooldown = data.isDoCooldown();
        doProfanityFilter = data.isDoProfanityFilter();
        doUrlFilter = data.isDoUrlFilter();
        callEvent = data.isCallEvent();
        allowMessageDeletion = data.allowsMessageDeletion();
        priority = data.getPriority();
        broadcastSound = data.getBroadcastSound();
    }

    /**
     * Base-line getData() implementation. Most subclasses should override this.
     * @return A populated LinkedHashMap containing channel data
     */
    @Override
    public Map<String, Object> getData() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("format", getFormat());
        map.put("default-color", getDefaultColor());
        map.put("call-event", String.valueOf(callsEvents()));
        map.put("filter-profanity", String.valueOf(isProfanityFiltering()));
        map.put("block-urls", String.valueOf(isUrlFiltering()));
        map.put("cooldown", String.valueOf(isCooldownEnabled()));
        map.put("type", String.valueOf(getType()));
        map.put("membership", String.valueOf(getMembership()));
        map.put("priority", String.valueOf(getPriority()));
        return map;
    }

    @NotNull
    protected Set<StringsUser> filterMutes(@NotNull Set<StringsUser> players) {
        return players.stream()
                .filter(user -> !user.hasChannelMuted(this))
                .collect(Collectors.toSet());
    }

    protected Set<StringsUser> filterIgnores(@NotNull StringsUser sender, @NotNull Set<StringsUser> players) {
        return players.stream()
                .filter(user -> !user.isIgnoring(sender))
                .collect(Collectors.toSet());
    }

    protected Set<StringsUser> filterMutesAndIgnores(@NotNull StringsUser sender, @NotNull Set<StringsUser> players) {
        players = filterIgnores(sender, players);
        players = filterMutes(players);

        return players;
    }

    protected UserManager getUserManager() {
        return userManager;
    }

    @Override
    public @NotNull String getBroadcastFormat() {
        return Objects.requireNonNullElse(broadcastFormat, DEFAULT_BROADCAST_FORMAT);
    }

    @Override
    public String getDefaultColor() {
        return defaultColor;
    }

    @Override
    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

    public boolean callsEvents() {
        return callEvent;
    }

    @Override
    public void addMember(@NotNull StringsUser user) {
        members.add(user);
    }

    @Override
    public void removeMember(@NotNull StringsUser user) {
        members.remove(user);
    }

    @Override
    public Set<StringsUser> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public void addMonitor(@NotNull StringsUser user) {
        monitors.add(user);
    }

    public void removeMonitor(@NotNull StringsUser user) {
        monitors.remove(user);
    }

    @Override
    public @NotNull Set<StringsUser> getMonitors() {
        return new HashSet<>(monitors);
    }

    @Override
    public boolean isUrlFiltering() {
        return doUrlFilter;
    }

    @Override
    public void setUrlFilter(boolean doUrlFilter) {
        this.doUrlFilter = doUrlFilter;
    }

    @Override
    public boolean isProfanityFiltering() {
        return doProfanityFilter;
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {
        this.doProfanityFilter = doProfanityFilter;
    }

    @Override
    public boolean isCooldownEnabled() {
        return this.doCooldown;
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {
        this.doCooldown = doCooldown;
    }

    @Override
    public void setAllowMessageDeletion(boolean allowMessageDeletion) {
        this.allowMessageDeletion = allowMessageDeletion;
    }

    @Override
    public boolean allowsMessageDeletion() {
        return allowMessageDeletion;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setFormat(@NotNull String format) {
        Objects.requireNonNull(format);
        this.format = format;
    }

    @Override
    public void setBroadcastSound(@Nullable Sound sound) {
        broadcastSound = sound;
    }

    @Override
    @Nullable
    public Sound getBroadcastSound() {
        return broadcastSound;
    }


    @Override
    public @NotNull String getFormat() {
        return format;
    }

    @Override
    public Membership getMembership() {
        return membership;
    }

    @Override
    public @NotNull Channel resolve(@NotNull StringsUser user) {
        return this;
    }

    @Override
    public int compareTo(@NotNull Channel o) {
        return Integer.compare(o.getPriority(), getPriority());
    }

}

