package com.pedestriamc.strings.chat.channels;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.User;
import com.pedestriamc.strings.api.Membership;
import com.pedestriamc.strings.api.StringsChannel;
import com.pedestriamc.strings.api.Type;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The channel that players are assigned to by default.
 * This channel cannot process any messages, it instead determines the proper channel to be used.
 */
public class DefaultChannel implements Channel{

    private final ChannelManager channelManager;
    private final Set<Player> members;
    private final Strings strings;

    public DefaultChannel(Strings strings, @NotNull ChannelManager channelManager){
        this.channelManager = channelManager;
        this.members = ConcurrentHashMap.newKeySet();
        this.strings = strings;
        channelManager.registerChannel(this);
    }

    @Override
    public void sendMessage(Player player, String message) {
        Channel[] worldChannels = channelManager.getWorldPriorityChannels(player.getWorld());
        if(worldChannels.length > 0){
            worldChannels[0].sendMessage(player, message);
            return;
        }
        Channel[] defaultMembership = channelManager.getPriorityChannels();
        if(defaultMembership.length > 0){
            defaultMembership[0].sendMessage(player, message);
            return;
        }
        User user = strings.getUser(player);
        Set<Channel> usersChannels = user.getChannels();
        if(!usersChannels.isEmpty()){
            Optional<Channel> optional =  usersChannels.stream().max(Comparator.comparingInt(Channel::getPriority));
            optional.get().sendMessage(player, message);
        }
        player.sendMessage(ChatColor.RED + "[Strings] You aren't a member of any channels.  Please contact staff for help.");
    }

    @Override
    public void broadcastMessage(String message) {}

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
    public void setName(String name) {}

    @Override
    public void setDefaultColor(String defaultColor) {}

    @Override
    public void setFormat(String format) {}

    @Override
    public void addPlayer(Player player) {
        members.add(player);
    }

    @Override
    public void removePlayer(Player player) {
        members.remove(player);
    }

    @Override
    public void addPlayer(User user) { members.add(user.getPlayer()); }

    @Override
    public void removePlayer(User user) {
        members.remove(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public boolean doUrlFilter() {
        return false;
    }

    @Override
    public void setUrlFilter(boolean doUrlFilter) {}

    @Override
    public boolean doProfanityFilter() {
        return false;
    }

    @Override
    public void setProfanityFilter(boolean doProfanityFilter) {}

    @Override
    public boolean doCooldown() {
        return false;
    }

    @Override
    public void setDoCooldown(boolean doCooldown) {}

    @Override
    public Type getType() {
        return Type.DEFAULT;
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

    @Override
    public boolean hasPermission(Player player) {
        return true;
    }
}
