package com.pedestriamc.strings.user;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.event.channel.UserChannelEvent;
import com.pedestriamc.strings.api.text.format.StringsComponent;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.chat.ChannelManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
@SuppressWarnings("unused")
public final class User implements StringsUser {

    // should almost always be true, see YamlUserUtil for usage.
    private final boolean retain;

    private final @NotNull Strings strings;
    private final @NotNull ChannelManager channelLoader;

    private final @NotNull UUID uuid;
    private final @NotNull Player player;
    private final @NotNull String name;
    private final @NotNull Set<Channel> channels;
    private final @NotNull Set<Monitorable> monitored;
    private final @NotNull Set<UUID> ignored;

    private @NotNull Channel activeChannel;
    private @NotNull StringsComponent chatColorComponent;

    private String prefix;
    private String suffix;
    private String displayName;
    private boolean mentionsEnabled;

    @Nullable
    public static User of(final @NotNull StringsUser user) {
        if(user instanceof User u) {
            return u;
        }
        return null;
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull UserBuilder builder(Strings strings, UUID uuid, boolean retained) {
        return new UserBuilder(strings, uuid, retained);
    }

    User(final @NotNull UserBuilder builder) {
        this.strings = builder.strings;
        this.channelLoader = strings.getChannelLoader();
        this.uuid = builder.uuid;
        this.retain = builder.retained;
        this.player = Objects.requireNonNull(strings.getServer().getPlayer(uuid));
        this.name = player.getName();
        this.chatColorComponent = StringsComponent.fromString(color(Objects.requireNonNullElse(builder.chatColor, "")));
        this.prefix = Objects.requireNonNullElse(builder.prefix, "");
        this.suffix = Objects.requireNonNullElse(builder.suffix, "");
        this.displayName = Objects.requireNonNullElse(builder.displayName, "");
        this.activeChannel = builder.activeChannel != null ? builder.activeChannel : channelLoader.getDefaultChannel();
        this.mentionsEnabled = builder.mentionsEnabled;
        this.channels = Objects.requireNonNullElseGet(builder.channels, HashSet::new);
        this.monitored = Objects.requireNonNullElseGet(builder.monitoredChannels, HashSet::new);
        this.ignored = Objects.requireNonNullElseGet(builder.ignored, HashSet::new);

        if(channels.isEmpty()) {
            joinChannel(channelLoader.getDefaultChannel());
        }

        joinChannels();
    }

    /**
     * Removes the User from channels because the player is going offline.
     * The User will rejoin the channels when they log back on.
     */
    public void logOff() {
        for(Channel channel : channels) {
            channel.removeMember(getPlayer());
        }
        for(Monitorable monitorable : monitored) {
            monitorable.removeMonitor(getPlayer());
        }
    }

    /**
     * Joins all Channels in the channels Set. Called only on initialization.
     */
    private void joinChannels() {
        for(Channel channel : channels) {
            channel.addMember(this);
        }
        for(Monitorable monitorable : monitored) {
            monitorable.addMonitor(this);
        }
    }

    /**
     * Sends a message to the User.
     * @param message The message.
     */
    @Override
    public void message(@NotNull String message) {
        getPlayer().sendMessage(message);
    }



    // signifies data has changed for the data cache.
    private volatile boolean dirty = true;
    // cache for getData
    private Map<String, Object> data;

    /**
     * Provides a Map containing all the User's information.
     * @return The populated Map.
     */
    public @NotNull Map<String, Object> getData() {
        synchronized(this) {
            if(dirty || data == null) {
                Map<String, Object> map = new HashMap<>();
                map.put("chat-color", getChatColorComponent().toString());
                map.put("prefix", Objects.requireNonNullElse(prefix, ""));
                map.put("suffix", Objects.requireNonNullElse(suffix, ""));
                map.put("display-name", Objects.requireNonNullElse(displayName, ""));
                map.put("active-channel", activeChannel.getName());
                map.put("channels", getNames(channels));
                map.put("monitored-channels", getNames(monitored));
                map.put("ignored-players", new ArrayList<>(ignored));
                map.put("mentions-enabled", mentionsEnabled);
                data = map;
            }
            return data;
        }
    }



    /**
     * Provides the User's UUID.
     * @return The UUID.
     */
    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    /**
     * Provides the Player aligned to the User.
     * @return The player.
     */
    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Provides the username of the User.
     * @return The username.
     */
    @Override
    public @NotNull String getName() {
        return name;
    }



    @Override
    public @NotNull StringsComponent getChatColorComponent() {
        return chatColorComponent;
    }

    @Override
    public void setChatColorComponent(final StringsComponent chatColor) {
        Objects.requireNonNull(chatColor);
        if(!chatColorComponent.equals(chatColor)) {
            chatColorComponent = chatColor;
            dirty = true;
        }
    }

    /**
     * Sets the chat color of the User.
     * @param chatColor The new chat color.
     */
    @Override
    @ApiStatus.Obsolete
    public void setChatColor(final String chatColor) {
        setChatColorComponent(StringsComponent.fromString(chatColor));
    }

    /**
     * Provides the User's chat color.
     * @return A chat color.
     */
    @NotNull
    @Override
    @ApiStatus.Obsolete
    public String getChatColor() {
        return getChatColorComponent().toString();
    }

    /**
     * Provides the User's chat color.
     * If the User's chat color is null, the chat color of the passed in channel is returned.
     * @param channel The channel to get the fallback chat color from.
     * @return A chat color.
     */
    @SuppressWarnings("java:S1874")
    @ApiStatus.Obsolete
    public String getChatColor(final @NotNull Channel channel) {
        final String chatColor = getChatColor();
        if(chatColor.isEmpty()) {
            return channel.getDefaultColor();
        }
        return chatColor;
    }


    /**
     * Sets the player's display name and updates the User, so it's saved.
     * @param displayName The new display name.
     */
    @Override
    public void setDisplayName(final @NotNull String displayName) {
        this.displayName = displayName;
        this.player.setDisplayName(displayName);
        dirty = true;
    }

    /**
     * Provides the User's display name.
     * If no display name is set with this plugin, it falls back to the server.
     * @return The display name.
     */
    @Override
    public @NotNull String getDisplayName() {
        if(displayName == null || displayName.isEmpty()) {
            return player.getDisplayName();
        }
        return color(displayName);
    }


    /**
     * Sets the User's prefix.
     * @param prefix The new prefix.
     */
    @Override
    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
        if(strings.isUsingVault()) {
            strings.getVaultChat().setPlayerPrefix(player, prefix);
        }
        dirty = true;
    }

    /**
     * Provides the User's prefix.
     * Uses Vault prefixes when available.
     * @return The prefix.
     */
    @Override
    public @NotNull String getPrefix() {
        if(strings.isUsingVault()) {
            return color(strings.getVaultChat().getPlayerPrefix(player));
        } else {
            if(prefix == null || prefix.isEmpty()) {
                return "";
            }
            return color(prefix);
        }
    }


    /**
     * Sets the User's suffix.
     * @param suffix The new suffix.
     */
    @Override
    public void setSuffix(@NotNull String suffix) {
        this.suffix = suffix;
        if(strings.isUsingVault()) {
            strings.getVaultChat().setPlayerSuffix(player, suffix);
        }
        dirty = true;
    }

    /**
     * Provides the User's suffix.
     * Uses Vault suffixes when available.
     * @return The suffix.
     */
    @Override
    public @NotNull String getSuffix() {
        if(strings.isUsingVault()) {
            return color(strings.getVaultChat().getPlayerSuffix(player));
        } else {
            if(suffix == null || suffix.isEmpty()) {
                return "";
            }
            return color(suffix);
        }
    }


    /**
     * Sets if this player receives mentions
     * @param mentionsEnabled A boolean of if the mentions should be enabled.
     */
    @Override
    public void setMentionsEnabled(boolean mentionsEnabled) {
        if(this.mentionsEnabled != mentionsEnabled) {
            this.mentionsEnabled = mentionsEnabled;
            dirty = true;
        }
    }

    /**
     * Provides a boolean of if the player wants to receive messages.
     * @return A boolean.
     */
    @Override
    public boolean isMentionsEnabled() {
        return this.mentionsEnabled;
    }


    /**
     * Ignores a Player.
     * @param player The Player to ignore.
     */
    public void ignore(@NotNull Player player) {
        Objects.requireNonNull(player);
        ignored.add(player.getUniqueId());
        dirty = true;
    }

    /**
     * Stops ignoring a Player.
     * @param player The Player to stop ignoring.
     */
    public void stopIgnoring(@NotNull Player player) {
        Objects.requireNonNull(player);
        ignored.remove(player.getUniqueId());
        dirty = true;
    }

    /**
     * Provides a Set of all ignored Players.
     * @return A populated Set.
     */
    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Set<UUID> getIgnoredPlayers() {
        return new HashSet<>(ignored);
    }



    /**
     * Checks if a User is a member of a channel.
     * @param channel The channel to check
     * @return If the player is a member of the specified channel.
     */
    @Override
    public boolean memberOf(Channel channel) {
        return channels.contains(channel);
    }



    /**
     * Sets the User's active channel where the User will send messages to.
     * @param channel The channel to be set as the active channel.
     */
    @Override
    public void setActiveChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel);
        if(!activeChannel.equals(channel)) {
            this.activeChannel = channel;
            dirty = true;
            if(!channels.contains(channel)) {
                joinChannel(channel);
            }
        }
    }

    /**
     * Provides the player's active channel.
     * @return The active channel.
     */
    @NotNull
    @Override
    public Channel getActiveChannel() {
        return activeChannel;
    }

    /**
     * Adds the User to a channel.
     * @param channel The channel to join.
     */
    @Override
    public void joinChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel);
        if(!channels.contains(channel)) {
            channel.addMember(this);
            channels.add(channel);
            callEvent(new UserChannelEvent(channel, this, UserChannelEvent.Type.JOIN));
            dirty = true;
        }
    }

    /**
     * Removes the User from a channel by updating the channel and user.
     * @param channel the channel to leave
     */
    @Override
    public void leaveChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel);
        if(channel.equals(channelLoader.getDefaultChannel())) {
            strings.warning("[Strings] Player " + player.getName() + " just tried to leave channel global. This is not permitted.");
            return;
        }

        if(channels.contains(channel)) {
            channels.remove(channel);
            channel.removeMember(this);
            if(activeChannel.equals(channel)) {
                activeChannel = channelLoader.getDefaultChannel();
            }
            callEvent(new UserChannelEvent(channel, this, UserChannelEvent.Type.LEAVE));
            dirty = true;
        }
    }

    /**
     * Provides a Set of the Channels this User is a member of.
     * @return A populated set of channels.
     */
    @Override
    public @NotNull Set<Channel> getChannels() {
        return new HashSet<>(channels);
    }

    private List<String> getNames(@NotNull Collection<? extends Channel> collection) {
        return collection.stream()
                .filter(Objects::nonNull)
                .map(Channel::getName)
                .toList();
    }



    /**
     * Monitors a Channel and adds this Player to the Channel's list of monitoring players.
     * @param monitorable The Monitorable Channel
     */
    @Override
    public void monitor(@NotNull Monitorable monitorable) {
        Objects.requireNonNull(monitorable);
        if(!monitored.contains(monitorable)) {
            monitored.add(monitorable);
            monitorable.addMonitor(this);
            callEvent(new UserChannelEvent(monitorable, this, UserChannelEvent.Type.MONITOR));
            dirty = true;
        }
    }

    /**
     * Unmonitors a Channel and removes this Player from the Channel's list of monitoring players.
     * @param monitorable The Monitorable Channel
     */
    @Override
    public void unmonitor(@NotNull Monitorable monitorable) {
        Objects.requireNonNull(monitorable);
        if(monitored.contains(monitorable)) {
            monitored.remove(monitorable);
            monitorable.removeMonitor(this);
            callEvent(new UserChannelEvent(monitorable, this, UserChannelEvent.Type.UNMONITOR));
            dirty = true;
        }
    }

    /**
     * Provides a Set of channels that the User is monitoring.
     * @return A populated Set
     */
    @Contract(value = " -> new", pure = true)
    public @NotNull Set<Channel> getMonitoredChannels() {
        return new HashSet<>(monitored);
    }


    /**
     * Provides if this User instance is retained.
     * @return True if the user is retained, false otherwise.
     */
    @SuppressWarnings("unused")
    public boolean isRetained() {
        return retain;
    }

    @Contract("_ -> new")
    private @NotNull String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private void callEvent(@NotNull Event event) {
        strings.getServer().getScheduler().runTask(strings,
                () -> strings.getServer().getPluginManager().callEvent(event));
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
        User user = (User) o;
        return this.uuid.equals(user.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

}
