package com.pedestriamc.strings.channels;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.Type;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The channel that players are assigned to by default.
 * This channel cannot process any messages, it instead determines the proper default channel to be used.
 */
public class DefaultChannel implements Channel{

    private final ChannelManager channelManager;
    private final Set<Player> members;

    public DefaultChannel(Strings strings, @NotNull ChannelManager channelManager){
        this.channelManager = channelManager;
        this.members = ConcurrentHashMap.newKeySet();
        channelManager.registerChannel(this);
    }

    @Override
    public void sendMessage(Player player, String message) {
        Channel[] worldChannels = channelManager.getWorldChannels(player.getWorld());
        if(worldChannels.length > 0){
            worldChannels[0].sendMessage(player, message);
            return;
        }
        Channel[] defaultMembership = channelManager.getPriorityChannels();
        if(defaultMembership.length > 0){
            defaultMembership[0].sendMessage(player, message);
            return;
        }
        player.sendMessage(ChatColor.RED + "[Strings] You aren't a member of any channels.  Please contact staff for help.");
    }

    @Override
    public void broadcastMessage(String message) {

    }

    @Override
    public void closeChannel() {

    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public String getDefaultColor() {
        return null;
    }

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setDefaultColor(String defaultColor) {

    }

    @Override
    public void setFormat(String format) {

    }

    @Override
    public void addPlayer(Player player) {
        members.add(player);
    }

    @Override
    public void removePlayer(Player player) {
        members.remove(player);
    }

    @Override
    public void addPlayer(User user) {
        members.add(user.getPlayer());

    }

    @Override
    public void removePlayer(User user) {
        members.remove(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public boolean doURLFilter() {
        return false;
    }

    @Override
    public void setURLFilter(boolean doURLFilter) {

    }

    @Override
    public boolean doProfanityFilter() {
        return false;
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {

    }

    @Override
    public boolean doCooldown() {
        return false;
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {

    }

    @Override
    public Type getType() {
        return Type.DEFAULT;
    }

    @Override
    public void setEnabled(boolean isEnabled) {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public Map<String, String> getData() {
        return null;
    }

    @Override
    public Membership getMembership() {
        return Membership.PROTECTED;
    }

    @Override
    public int getPriority() {
        return -1;
    }

    @Override
    public void saveChannel() {
        channelManager.saveChannel(this);
    }

    @Override
    public StringsChannel getStringsChannel() {
        return null;
    }
}
