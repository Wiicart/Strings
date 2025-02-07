package com.pedestriamc.strings.user;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.channels.Channel;
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
 * The User class is used to store information about players for the purposes of this plugin.
 * It provides the data that the plugin stores on the player, and when available provides information from other plugins.
 */
public class User implements StringsUser {

    private final Strings strings;
    private final UUID uuid;
    private final Player player;
    private final String name;
    private final HashSet<Channel> channels;
    private String chatColor;
    private String prefix;
    private String suffix;
    private String displayName;
    private Channel activeChannel;
    private boolean mentionsEnabled;

    /**
     * The constructor for a User with no stored data.
     * All values are default or null.
     * @param uuid the User's UUID.  This should match the Player's UUID.
     */
    public User(UUID uuid){
        this(uuid, null, null, null, null, null, null, true);
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
    public User(UUID uuid, String chatColor, String prefix, String suffix, String displayName, HashSet<Channel> channels, Channel activeChannel, boolean mentionsEnabled){
        this.strings = Strings.getInstance();
        this.uuid = uuid;
        this.chatColor = chatColor;
        this.prefix = prefix;
        this.suffix = suffix;
        this.displayName = displayName;
        this.player = Bukkit.getPlayer(uuid);
        this.mentionsEnabled = mentionsEnabled;
        this.name = player != null ? player.getName() : null;
        this.activeChannel = activeChannel != null ? activeChannel : strings.getChannel("default");
        this.channels = Objects.requireNonNullElseGet(channels, HashSet::new);
        if(channels != null) {
            for(Channel channel : channels){
                channel.addPlayer(this.player);
            }
        } else {
            joinChannel(strings.getChannel("default"));
        }
        YamlUserUtil.saveUser(this);
        YamlUserUtil.UserMap.addUser(this);
    }

    /**
     * Provides a Map containing all the User's information.
     * @return The populated Map.
     */
    public Map<String, Object> getData(){
        HashMap<String, Object> infoMap = new HashMap<>();
        infoMap.put("chat-color", this.chatColor);
        infoMap.put("prefix", this.prefix);
        infoMap.put("suffix", this.suffix);
        infoMap.put("display-name", this.displayName);
        infoMap.put("active-channel", this.activeChannel.getName());
        infoMap.put("channels", this.getChannelNames());
        infoMap.put("mentions-enabled", this.mentionsEnabled);
        return infoMap;
    }

    /**
     * Provides the User's UUID.
     * @return The UUID.
     */
    public @NotNull UUID getUuid(){
        return uuid;
    }

    /**
     * Provides the User's chat color.
     * @return A chat color.
     */
    @Nullable
    public String getChatColor(){
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
    public String getChatColor(Channel channel){
        if(chatColor == null){
            return channel.getDefaultColor();
        }
        return ChatColor.translateAlternateColorCodes('&', chatColor);
    }

    /**
     * Provides the User's display name.
     * If no display name is set with this plugin, it falls back to the server.
     * @return The display name.
     */
    public @NotNull String getDisplayName(){
        if(displayName == null){
            return player.getDisplayName();
        }
        return ChatColor.translateAlternateColorCodes('&', displayName);
    }

    /**
     * Provides the User's prefix.
     * Uses Vault prefixes when available.
     * @return The prefix.
     */
    public String getPrefix(){
        if(strings.useVault()){
            return ChatColor.translateAlternateColorCodes('&', strings.getVaultChat().getPlayerPrefix(player));
        }else{
            if(prefix == null){
                return "";
            }
            return ChatColor.translateAlternateColorCodes('&', prefix);
        }
    }

    /**
     * Provides the User's suffix.
     * * Uses Vault suffixes when available.
     * @return The suffix.
     */
    public String getSuffix(){
        if(strings.useVault()){
            return ChatColor.translateAlternateColorCodes('&', strings.getVaultChat().getPlayerSuffix(player));
        }else{
            if(suffix == null){
                return "";
            }
            return ChatColor.translateAlternateColorCodes('&', suffix);
        }
    }

    /**
     * Provides the Player correlated to the User.
     * @return The player.
     */
    public Player getPlayer(){
        return player;
    }

    /**
     * Provides the username of the User.
     * @return The username.
     */
    public @NotNull String getName(){ return name; }

    /**
     * Sets the chat color of the User.
     * @param chatColor The new chat color.
     */
    public void setChatColor(@NotNull String chatColor){
        this.chatColor = chatColor;
        YamlUserUtil.saveUser(this);
    }

    /**
     * Sets the User's prefix.
     * @param prefix The new prefix.
     */
    public void setPrefix(@NotNull String prefix){
        this.prefix = prefix;
        if(strings.useVault()){
            strings.getVaultChat().setPlayerPrefix(player, prefix);
        }
        YamlUserUtil.saveUser(this);
    }

    /**
     * Sets the User's suffix.
     * @param suffix The new suffix.
     */
    public void setSuffix(@NotNull String suffix){
        this.suffix = suffix;
        if(strings.useVault()){
            strings.getVaultChat().setPlayerSuffix(player, suffix);
        }
        YamlUserUtil.saveUser(this);
    }

    /**
     * Sets the player's display name and updates the User, so it's saved.
     * @param displayName The new display name.
     */
    public void setDisplayName(@NotNull String displayName){
        this.displayName = displayName;
        this.player.setDisplayName(displayName);
        YamlUserUtil.saveUser(this);
    }

    /**
     * Provides a boolean of if the player wants to receive messages.
     * @return A boolean.
     */
    public boolean isMentionsEnabled(){
        return this.mentionsEnabled;
    }

    /**
     * Sets if this player will receive mentions
     * @param mentionsEnabled A boolean of if the mentions should be enabled.
     */
    public void setMentionsEnabled(boolean mentionsEnabled){
        this.mentionsEnabled = mentionsEnabled;
        YamlUserUtil.saveUser(this);
    }

    /**
     * Provides the player's active channel.
     * @return The active channel.
     */
    public @NotNull Channel getActiveChannel(){
        return activeChannel;
    }

    /**
     * Sets the User's active channel where the User will send messages to.
     * @param channel The channel to be set as the active channel.
     */
    public void setActiveChannel(@NotNull Channel channel){
        if(channel.getName().equals("helpop")){
            return;
        }
        this.activeChannel = channel;
        channels.add(channel);
        channel.addPlayer(this.getPlayer());
        YamlUserUtil.saveUser(this);
    }

    /**
     * Provides a Set of the channels the User is a member of.
     * @return A populated set of channels.
     */
    public Set<Channel> getChannels(){
        return channels;
    }

    /**
     * Adds the User to a channel.
     * @param channel The channel to join.
     */
    public void joinChannel(@NotNull Channel channel){
        channel.addPlayer(this.player);
        channels.add(channel);
        YamlUserUtil.saveUser(this);

    }

    /**
     * Removes the User from a channel by updating the channel and user.
     * @param channel the channel to leave
     */
    public void leaveChannel(@NotNull Channel channel){
        if(channel.equals(strings.getChannel("default"))){
            Bukkit.getLogger().info("[Strings] Player " + player.getName() + " just tried to leave channel global!  Cancelled leaving channel.");
            return;
        }
        channels.remove(channel);
        channel.removePlayer(this.getPlayer());
        if(activeChannel.equals(channel)){
            activeChannel = strings.getChannel("default");
        }
        YamlUserUtil.saveUser(this);
    }

    /**
     * Checks if a User is a member of a channel.
     * @param channel The channel to check
     * @return If the player is a member of the specified channel.
     */
    public boolean memberOf(Channel channel){
        return channels.contains(channel);
    }

    @Override
    public boolean mentionsEnabled() {
        return isMentionsEnabled();
    }

    /**
     * Provides an ArrayList of the names of the channels the User is a member of.
     * @return An {@code ArrayList} of {@code String} containing the names of the channels the user is a member of.
     */
    public ArrayList<String> getChannelNames(){
        ArrayList<String> names = new ArrayList<>();
        for(Channel channel : channels){
            names.add(channel.getName());
        }
        return names;
    }

    /**
     * Removes the User from channels because the player is going offline.
     * The User will rejoin the channels when they log back on.
     */
    public void logOff(){
        for(Channel channel : channels){
            channel.removePlayer(this.getPlayer());
        }
    }

    /**
     * Provides a String representation of the getData HashMap.
     * All data that the User has will be given.
     * @return String with information on this User.
     */
    @Override
    public String toString(){
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
