package com.pedestriamc.strings.user;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.platform.Platform;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.chat.ChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * The User class is used to store information about players for this plugin.
 * It provides the data that the plugin stores on the player, and when available provides information from other plugins.
 */
public final class User implements StringsUser {

    private final Strings strings;
    private final ChannelManager channelLoader;

    private final UUID uuid;
    private final Player player;
    private final String name;
    private final Set<Channel> channels;
    private final Set<Channel> monitoredChannels;
    private String chatColor;
    private String prefix;
    private String suffix;
    private String displayName;
    private Channel activeChannel;
    private boolean mentionsEnabled;
    private final boolean retain;

    @Nullable
    public static User of(StringsUser user) {
        if(user instanceof User u) {
            return u;
        }
        return null;
    }

    /**
     * The constructor for a User with no stored data.
     * All values are default or null.
     * @param uuid the User's UUID.  This should match the Player's UUID.
     */
    public User(Strings strings, UUID uuid, boolean retained) {
        this(strings, uuid, null, null, null, null, null, null, true, null, retained);
    }

    /**
     * The constructor for a User with values.
     * @param uuid The User's UUID.
     * @param chatColor The User's chat color.
     * @param prefix The User's prefix.
     * @param suffix The User's suffix.
     * @param displayName The User's display name.
     * @param channels The channels the User is a member of.
     * @param activeChannel The User's active channel.
     * @param mentionsEnabled If the user receives mentions or not.
     */
    public User(Strings strings, UUID uuid, String chatColor, String prefix, String suffix, String displayName, Set<Channel> channels, Channel activeChannel, boolean mentionsEnabled, Set<Channel> monitoredChannels,  boolean retained) {
        this.strings = strings;
        channelLoader = strings.getChannelLoader();
        this.uuid = uuid;
        this.player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
        this.chatColor = chatColor;
        this.prefix = prefix;
        this.suffix = suffix;
        this.displayName = displayName;
        this.mentionsEnabled = mentionsEnabled;
        this.name = player.getName();
        this.activeChannel = activeChannel != null ? activeChannel : channelLoader.getDefaultChannel();
        this.channels = Objects.requireNonNullElseGet(channels, HashSet::new);
        this.monitoredChannels = Objects.requireNonNullElseGet(monitoredChannels, HashSet::new);
        retain = retained;

        if(channels != null) {
            for(Channel channel : channels) {
                channel.addMember(this.player);
            }
        } else {
            joinChannel(channelLoader.getDefaultChannel());
        }
    }

    public void message(@NotNull String message) {
        getPlayer().sendMessage(message);
    }

    /**
     * Provides a Map containing all the User's information.
     * @return The populated Map.
     */
    public Map<String, Object> getData() {
        synchronized(this) {
            Map<String, Object> map = new HashMap<>();
            map.put("chat-color", Objects.requireNonNullElse(chatColor, ""));
            map.put("prefix", Objects.requireNonNullElse(prefix, ""));
            map.put("suffix", Objects.requireNonNullElse(suffix, ""));
            map.put("display-name", Objects.requireNonNullElse(displayName, ""));
            map.put("active-channel", activeChannel == null ? "default" : activeChannel.getName());
            map.put("channels", new ArrayList<>(getChannelNames()));
            map.put("monitored-channels", new ArrayList<>(getMonitoredChannelNames()));
            map.put("mentions-enabled", mentionsEnabled);
            return map;
        }
    }

    /**
     * Provides the User's UUID.
     * @return The UUID.
     */
    public @NotNull UUID getUuid() {
        return uuid;
    }

    /**
     * Provides the User's chat color.
     * @return A chat color.
     */
    @NotNull
    public String getChatColor() {
        if(chatColor != null && !chatColor.isEmpty()) {
            return ChatColor.translateAlternateColorCodes('&', chatColor);
        }
        return "";
    }

    /**
     * Provides the User's chat color.
     * If the User's chat color is null, the chat color of the passed in channel is returned.
     * @param channel The channel to get the fallback chat color from.
     * @return A chat color.
     */
    public String getChatColor(Channel channel) {
        if(chatColor == null || chatColor.isEmpty()) {
            return channel.getDefaultColor();
        }
        return ChatColor.translateAlternateColorCodes('&', chatColor);
    }

    /**
     * Provides the User's display name.
     * If no display name is set with this plugin, it falls back to the server.
     * @return The display name.
     */
    public @NotNull String getDisplayName() {
        if(displayName == null || displayName.isEmpty()) {
            return player.getDisplayName();
        }
        return ChatColor.translateAlternateColorCodes('&', displayName);
    }

    /**
     * Provides the User's prefix.
     * Uses Vault prefixes when available.
     * @return The prefix.
     */
    public String getPrefix() {
        if(strings.usingVault()) {
            return ChatColor.translateAlternateColorCodes('&', strings.getVaultChat().getPlayerPrefix(player));
        } else {
            if(prefix == null || prefix.isEmpty()) {
                return "";
            }
            return ChatColor.translateAlternateColorCodes('&', prefix);
        }
    }

    /**
     * Provides the User's suffix.
     * Uses Vault suffixes when available.
     * @return The suffix.
     */
    public String getSuffix() {
        if(strings.usingVault()) {
            return ChatColor.translateAlternateColorCodes('&', strings.getVaultChat().getPlayerSuffix(player));
        } else {
            if(suffix == null || suffix.isEmpty()) {
                return "";
            }
            return ChatColor.translateAlternateColorCodes('&', suffix);
        }
    }

    /**
     * Provides the Player correlated to the User.
     * @return The player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Provides the username of the User.
     * @return The username.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Sets the chat color of the User.
     * @param chatColor The new chat color.
     */
    public void setChatColor(String chatColor) {
        this.chatColor = chatColor;
    }

    /**
     * Sets the User's prefix.
     * @param prefix The new prefix.
     */
    public void setPrefix(@NotNull String prefix) {
        this.prefix = prefix;
        if(strings.usingVault()) {
            strings.getVaultChat().setPlayerPrefix(player, prefix);
        }
    }

    /**
     * Sets the User's suffix.
     * @param suffix The new suffix.
     */
    public void setSuffix(@NotNull String suffix) {
        this.suffix = suffix;
        if(strings.usingVault()) {
            strings.getVaultChat().setPlayerSuffix(player, suffix);
        }
    }

    /**
     * Sets the player's display name and updates the User, so it's saved.
     * @param displayName The new display name.
     */
    public void setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
        this.player.setDisplayName(displayName);
    }

    /**
     * Provides a boolean of if the player wants to receive messages.
     * @return A boolean.
     */
    public boolean isMentionsEnabled() {
        return this.mentionsEnabled;
    }

    /**
     * Sets if this player receives mentions
     * @param mentionsEnabled A boolean of if the mentions should be enabled.
     */
    public void setMentionsEnabled(boolean mentionsEnabled) {
        this.mentionsEnabled = mentionsEnabled;
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUKKIT;
    }

    @Override
    public Locality getLocality() {
        return null;
    }

    /**
     * Provides the player's active channel.
     * @return The active channel.
     */
    public Channel getActiveChannel() {
        return activeChannel;
    }

    /**
     * Sets the User's active channel where the User will send messages to.
     * @param channel The channel to be set as the active channel.
     */
    public void setActiveChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel);
        if(channel.getName().equals("helpop")) {
            return;
        }
        this.activeChannel = channel;
        channels.add(channel);
        channel.addMember(this.getPlayer());
    }

    /**
     * Provides a Set of the channels the User is a member of.
     * @return A populated set of channels.
     */
    public Set<Channel> getChannels() {
        return channels;
    }

    /**
     * Adds the User to a channel.
     * @param channel The channel to join.
     */
    public void joinChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel);
        channel.addMember(this.player);
        channels.add(channel);
    }

    /**
     * Removes the User from a channel by updating the channel and user.
     * @param channel the channel to leave
     */
    public void leaveChannel(@NotNull Channel channel) {
        Objects.requireNonNull(channel);
        if(channel.equals(channelLoader.getChannel("default"))) {
            Bukkit.getLogger().info("[Strings] Player " + player.getName() + " just tried to leave channel global!  Cancelled leaving channel.");
            return;
        }
        channels.remove(channel);
        channel.removeMember(this.getPlayer());
        if(activeChannel.equals(channel)) {
            activeChannel = channelLoader.getChannel("default");
        }
    }

    /**
     * Checks if a User is a member of a channel.
     * @param channel The channel to check
     * @return If the player is a member of the specified channel.
     */
    public boolean memberOf(Channel channel) {
        return channels.contains(channel);
    }

    @Override
    public boolean mentionsEnabled() {
        return isMentionsEnabled();
    }

    /**
     * Provides an ArrayList of all names of the channels the User is a member of.
     * @return An {@code ArrayList} of {@code String} containing the names of the channels the user is a member of.
     */
    public Set<String> getChannelNames() {
        Set<String> names = new HashSet<>();
        for(Channel channel : getChannels()) {
            if(channel != null) {
                String channelName = channel.getName();
                names.add(channelName);
            }
        }
        return names;
    }

    /**
     * Removes the User from channels because the player is going offline.
     * The User will rejoin the channels when they log back on.
     */
    public void logOff() {
        for(Channel channel : channels) {
            channel.removeMember(getPlayer());
        }

        for(Channel channel : monitoredChannels) {
            Monitorable monitorable = Monitorable.of(channel);
            monitorable.removeMonitor(getPlayer());
        }
    }

    public void monitor(@NotNull Monitorable monitorable) {
        Objects.requireNonNull(monitorable);
        monitoredChannels.add(monitorable);
        monitorable.addMonitor(this);
    }

    public void unmonitor(@NotNull Monitorable monitorable) {
        Objects.requireNonNull(monitorable);
        monitoredChannels.remove(monitorable);
        monitorable.removeMonitor(this);
    }

    public Set<String> getMonitoredChannelNames() {
        Set<String> names = new HashSet<>();
        for(Channel channel : getMonitoredChannels()) {
            if(channel != null) {
                String channelName = channel.getName();
                names.add(channelName);
            }
        }
        return names;
    }

    public Set<Channel> getMonitoredChannels() {
        return new HashSet<>(monitoredChannels);
    }

    /**
     * Returns if this User instance is reattained.
     * @return True if the user is retained, false otherwise.
     */
    @SuppressWarnings("unused")
    public boolean isRetained() {
        return retain;
    }

    /**
     * Provides a String representation of the getData HashMap.
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
        return mentionsEnabled == user.mentionsEnabled && Objects.equals(strings, user.strings) && Objects.equals(uuid, user.uuid) && Objects.equals(player, user.player) && Objects.equals(name, user.name) && Objects.equals(channels, user.channels) && Objects.equals(chatColor, user.chatColor) && Objects.equals(prefix, user.prefix) && Objects.equals(suffix, user.suffix) && Objects.equals(displayName, user.displayName) && Objects.equals(activeChannel, user.activeChannel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strings, uuid, player, name, channels, chatColor, prefix, suffix, displayName, activeChannel, mentionsEnabled);
    }
}
