package com.pedestriamc.strings.common.user;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.discord.Snowflake;
import com.pedestriamc.strings.api.event.strings.EventManager;
import com.pedestriamc.strings.api.platform.EventFactory;
import com.pedestriamc.strings.api.text.format.StringsComponent;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Stores information about players for Strings.
 * Defaults to Vault values where available.
 */
public abstract class AbstractUser implements StringsUser {

    private final boolean isNew;

    private final StringsPlatform strings;
    private final ChannelLoader channelLoader;

    private final EventFactory eventFactory;
    private final EventManager eventManager;

    private final UUID uuid;

    private final Set<Channel> channels;
    private final Set<Monitorable> monitored;
    private final Set<UUID> ignored;
    private final Set<Channel> mutes;

    private Channel activeChannel;
    private StringsComponent chatColor;
    private Snowflake discordId;

    private boolean mentionsEnabled;
    private boolean msgEnabled;

    public static AbstractUser of(@NotNull StringsUser user) {
        if (user instanceof AbstractUser u) {
            return u;
        }

        throw new RuntimeException("Provided User does not implement Common User.");
    }

    protected AbstractUser(@NotNull UserBuilder<?> builder) {
        this.strings = builder.getStrings();
        eventFactory = strings.eventFactory();
        eventManager = strings.eventManager();

        this.channelLoader = strings.getChannelLoader();
        this.uuid = builder.getUuid();
        this.isNew = builder.isNew();
        this.activeChannel = builder.getActiveChannel() != null ? builder.getActiveChannel() : channelLoader.getDefaultChannel();
        this.mentionsEnabled = builder.isMentionsEnabled();
        this.msgEnabled = builder.isMsgEnabled();
        this.channels = Objects.requireNonNullElseGet(builder.getChannels(), HashSet::new);
        this.monitored = Objects.requireNonNullElseGet(builder.getMonitoredChannels(), HashSet::new);
        this.ignored = Objects.requireNonNullElseGet(builder.getIgnores(), HashSet::new);
        this.mutes = Objects.requireNonNullElseGet(builder.getMutes(), HashSet::new);
        this.discordId = builder.getDiscordId();
        this.chatColor = StringsComponent.fromString(Objects.requireNonNullElse(builder.getChatColor(), ""));

        // Join the DefaultChannel if not a member of any other Channels
        if (channels.isEmpty()) {
            joinChannel(channelLoader.getDefaultChannel());
        }

        joinChannels();
    }

    /**
     * Removes the User from channels because the player is going offline.
     * The User will rejoin the channels when they log back on.
     */
    public void logOff() {
        for (Channel channel : channels) {
            channel.removeMember(this);
        }

        for (Monitorable monitorable : monitored) {
            monitorable.removeMonitor(this);
        }
    }

    /**
     * Joins all Channels in the channels Set. Called only on initialization.
     */
    private void joinChannels() {
        for (Channel channel : channels) {
            channel.addMember(this);
        }

        for (Monitorable monitorable : monitored) {
            monitorable.addMonitor(this);
        }
    }

    /**
     * Provides a Map containing all the User's information.
     * @return The populated Map.
     */
    @NotNull
    @Unmodifiable
    public Map<String, Object> getData() {
        synchronized(this) {
            return Map.ofEntries(
                    Map.entry("chat-color", getChatColorComponent().toString()),
                    Map.entry("prefix", getPrefix()),
                    Map.entry("suffix", getSuffix()),
                    Map.entry("display-name", getDisplayName()),
                    Map.entry("active-channel", getActiveChannel().getName()),
                    Map.entry("channels", getNames(getChannels())),
                    Map.entry("monitored-channels", getNames(getMonitoredChannels())),
                    Map.entry("muted-channels", getNames(getMutedChannels())),
                    Map.entry("ignored-players", new ArrayList<>(getIgnoredPlayers())),
                    Map.entry("mentions-enabled", hasMentionsEnabled()),
                    Map.entry("msg-enabled", hasDirectMessagesEnabled()),
                    Map.entry("discord-id", getDiscordId().get())
            );
        }
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    @NotNull
    public StringsComponent getChatColorComponent() {
        return chatColor;
    }

    @Override
    public void setChatColorComponent(@NotNull StringsComponent chatColor) {
        Objects.requireNonNull(chatColor);
        if (!this.chatColor.equals(chatColor)) {
            this.chatColor = chatColor;
        }
    }

    /**
     * Sets the chat color of the User.
     * @param chatColor The new chat color.
     */
    @Override
    @ApiStatus.Obsolete
    public void setChatColor(@NotNull String chatColor) {
        setChatColorComponent(StringsComponent.fromString(chatColor));
    }

    @Override
    public void setMentionsEnabled(boolean mentionsEnabled) {
        if (this.mentionsEnabled != mentionsEnabled) {
            this.mentionsEnabled = mentionsEnabled;
        }
    }

    @Override
    public boolean hasMentionsEnabled() {
        return this.mentionsEnabled;
    }

    public boolean isIgnoring(@NotNull StringsUser other) {
        return getIgnoredPlayers().contains(other.getUniqueId());
    }

    public void ignore(@NotNull StringsUser user) {
        Objects.requireNonNull(user);
        if (user.equals(this)) {
            return;
        }

        ignored.add(user.getUniqueId());
    }

    @Override
    public void stopIgnoring(@NotNull StringsUser user) {
        Objects.requireNonNull(user);
        ignored.remove(user.getUniqueId());
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Set<UUID> getIgnoredPlayers() {
        return new HashSet<>(ignored);
    }


    @Override
    public boolean memberOf(@NotNull Channel channel) {
        return channels.contains(channel);
    }

    @Override
    public void setActiveChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel);
        if (!activeChannel.equals(channel)) {
            this.activeChannel = channel;
            if (!channels.contains(channel)) {
                joinChannel(channel);
            }

            eventManager.dispatch(eventFactory.channelActive(this, channel));
        }
    }

    @NotNull
    @Override
    public Channel getActiveChannel() {
        return activeChannel;
    }

    @Override
    public void joinChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel);
        if (!channels.contains(channel)) {
            channel.addMember(this);
            channels.add(channel);
            eventManager.dispatch(eventFactory.channelJoin(this, channel));
        }
    }

    /**
     * Removes the User from a channel by updating the channel and user.
     * @param channel the channel to leave
     */
    @Override
    public void leaveChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel);
        if (channel.equals(channelLoader.getDefaultChannel())) {
            strings.warning("[Strings] Player " + getName() + " just attempted to leave the default channel.");
            return;
        }

        if (channels.contains(channel)) {
            channels.remove(channel);
            channel.removeMember(this);
            if(activeChannel.equals(channel)) {
                activeChannel = channelLoader.getDefaultChannel();
            }
            eventManager.dispatch(eventFactory.channelLeave(this, channel));
        }
    }

    @Override
    @NotNull
    public Set<Channel> getChannels() {
        return new HashSet<>(channels);
    }

    private List<String> getNames(@NotNull Collection<? extends Channel> collection) {
        return collection.stream()
                .filter(Objects::nonNull)
                .map(Channel::getName)
                .toList();
    }



    @Override
    public void monitor(@NotNull Monitorable monitorable) {
        Objects.requireNonNull(monitorable);
        if (!monitored.contains(monitorable)) {
            monitored.add(monitorable);
            monitorable.addMonitor(this);
            eventManager.dispatch(eventFactory.channelMonitor(this, monitorable));
        }
    }

    @Override
    public void unmonitor(@NotNull Monitorable monitorable) {
        Objects.requireNonNull(monitorable);
        if (monitored.contains(monitorable)) {
            monitored.remove(monitorable);
            monitorable.removeMonitor(this);
            eventManager.dispatch(eventFactory.channelUnmonitor(this, monitorable));
        }
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Set<Channel> getMonitoredChannels() {
        return new HashSet<>(monitored);
    }

    @Override
    public boolean isMonitoring(@NotNull Monitorable monitorable) {
        return monitored.contains(monitorable);
    }

    @Override
    public void muteChannel(@NotNull Channel channel) {
        if (channel instanceof Monitorable monitorable) {
            unmonitor(monitorable);
        }

        mutes.add(channel);
    }

    @Override
    public void unmuteChannel(@NotNull Channel channel) {
        mutes.remove(channel);
    }

    @Override
    public @NotNull Set<Channel> getMutedChannels() {
        return new HashSet<>(mutes);
    }

    @Override
    public boolean hasChannelMuted(@NotNull Channel channel) {
        return mutes.contains(channel);
    }

    @Override
    public boolean hasDirectMessagesEnabled() {
        return msgEnabled;
    }

    @Override
    public void setDirectMessagesEnabled(boolean msgEnabled) {
        this.msgEnabled = msgEnabled;
    }

    @Override
    public boolean isDiscordLinked() {
        return discordId.isPresent();
    }

    @Override
    @NotNull
    public Snowflake getDiscordId() {
        return discordId;
    }

    @Override
    public void setDiscordId(@NotNull Snowflake snowflake) {
        this.discordId = snowflake;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * Provides a String representation of the User.getData() Map.
     * All data that the User has will be given.
     * @return String with information on this User.
     */
    @Override
    public String toString() {
        return getData().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractUser user = (AbstractUser) o;
        return this.uuid.equals(user.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Contract("_ -> new")
    private @NotNull String color(String string) {
        return strings.getAdapter().translateBukkitColor(string);
    }
}