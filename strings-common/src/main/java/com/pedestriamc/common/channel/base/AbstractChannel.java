package com.pedestriamc.common.channel.base;

import com.pedestriamc.common.chat.MessageProcessor;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.event.StringsChatEvent;
import com.pedestriamc.strings.api.platform.PlatformAdapter;
import com.pedestriamc.strings.api.text.format.ComponentConverter;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.user.UserManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractChannel implements Channel, Monitorable {

    private final StringsPlatform strings;
    private final PlatformAdapter adapter;
    private final MessageProcessor messageProcessor;

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
        this.strings = strings;
        adapter = strings.getAdapter();
        messageProcessor = new MessageProcessor(strings, this);
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

        String permission = CHANNEL_PERMISSION + getName();
        adapter.addPermission(permission, permission + ".broadcast");
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        Set<StringsUser> recipients = getRecipients(user);
        String template = messageProcessor.generateTemplate(user);

        if (callsEvents()) {
            sendEventMessage(user, message, template, recipients);
        } else {
            sendNonEventMessage(user, message, template, recipients);
        }
    }

    private void sendEventMessage(@NotNull StringsUser user, @NotNull String message, @NotNull String template, @NotNull Set<StringsUser> recipients) {
        StringsChatEvent event = strings.getEventFactory().chatEvent(
                false,
                true,
                user,
                message,
                recipients,
                this,
                null
        );

        strings.getEventDispatcher().dispatch(event);
        if (!event.isCancelled()) {
            sendNonEventMessage(user, event.getMessage(), template, recipients);
        }
    }

    private void sendNonEventMessage(@NotNull StringsUser user, @NotNull String message, @NotNull String template, @NotNull Set<StringsUser> recipients) {
        message = messageProcessor.processMessage(user, message);
        String completeMessage = template.replace(MESSAGE_PLACEHOLDER, message);
        Component component = ComponentConverter.fromString(completeMessage);
        for (StringsUser recipient : recipients) {
            recipient.sendMessage(component);
        }

        if (!recipients.contains(user)) {
           user.sendMessage(component);
        }

        adapter.print(adapter.stripBukkitColor(completeMessage));
    }

    /**
     * Base-line getData() implementation.
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
        if (strings.getChannelLoader().getChannel(name) != null) {
            throw new IllegalArgumentException("Channel " + name + " already exists");
        }

        String oldPermission = CHANNEL_PERMISSION + getName();
        adapter.removePermission(oldPermission, oldPermission + ".broadcast");

        String newPermission = CHANNEL_PERMISSION + name;
        adapter.addPermission(newPermission, newPermission + ".broadcast");

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
    public void broadcast(@NotNull Component message) {
        Audience recipients = getPlayersInScopeAsAudience();
        recipients.sendMessage(message);
        Sound sound = getBroadcastSound();
        if (sound != null) {
            recipients.playSound(sound);
        }
    }

    @Override
    public void broadcast(@NotNull String message) {
        String finalString = getBroadcastFormat().replace(MESSAGE_PLACEHOLDER, message);
        finalString = adapter.translateBukkitColor(finalString);

        Audience recipients = getPlayersInScopeAsAudience();
        recipients.sendMessage(ComponentConverter.fromString(finalString));
        Sound sound = getBroadcastSound();
        if (sound != null) {
            recipients.playSound(sound);
        }
    }

    @Override
    public void broadcastPlain(@NotNull String message) {
        getPlayersInScopeAsAudience().sendMessage(ComponentConverter.fromString(message));
    }

    @Override
    public void broadcastPlain(@NotNull Component message) {
        getPlayersInScopeAsAudience().sendMessage(message);
    }

    @Override
    public boolean allows(@NotNull StringsUser user) {
        if (getMembers().contains(user)) {
            return true;
        }

        return user.isOperator() ||
                user.hasPermission("*") ||
                user.hasPermission("strings.*") ||
                user.hasPermission(CHANNEL_PERMISSION + getName()) ||
                user.hasPermission(CHANNEL_PERMISSION + getName() + "*");
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

    protected Audience getPlayersInScopeAsAudience() {
        Set<Audience> set = getPlayersInScope()
                .stream()
                .map(StringsUser::audience)
                .collect(Collectors.toSet());

        return Audience.audience(set);
    }

}

