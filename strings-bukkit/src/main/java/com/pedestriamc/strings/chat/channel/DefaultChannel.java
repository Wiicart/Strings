package com.pedestriamc.strings.chat.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.channels.Channel;
import com.pedestriamc.strings.api.channels.ChannelLoader;
import com.pedestriamc.strings.chat.StringsChannelLoader;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.channels.Membership;
import com.pedestriamc.strings.api.channels.Type;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The channel that players are assigned to by default.
 * This channel cannot process any messages; it instead determines the proper channel to be used.
 */
public class DefaultChannel implements Channel {

    private final StringsChannelLoader channelLoader;
    private final Set<Player> members;
    private final Strings strings;

    public DefaultChannel(Strings strings, @NotNull ChannelLoader channelLoader){
        this.channelLoader = (StringsChannelLoader) channelLoader;
        this.members = ConcurrentHashMap.newKeySet();
        this.strings = strings;
    }

    @Override
    public void sendMessage(Player player, String message) {
        Set<Channel> worldChannels = channelLoader.getWorldPriorityChannels(player.getWorld());
        if(!worldChannels.isEmpty()) {
            for(Channel c : worldChannels) {
                if(c.allows(player)) {
                    c.sendMessage(player, message);
                    return;
                }
            }
        }

        List<Channel> defaultMembership = channelLoader.getChannelsByPriority();
        if(!defaultMembership.isEmpty()) {
            for(Channel c : defaultMembership) {
                if(c.allows(player)) {
                    c.sendMessage(player, message);
                    return;
                }
            }
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
    public void setName(String name) { /* DefaultChannel instances are always named "default" */ }

    @Override
    public void setDefaultColor(String defaultColor) {
        /* As DefaultChannel does not send out messages, there is no utilization of a default color. */
    }

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
    public void addPlayer(StringsUser user) { members.add(user.getPlayer()); }

    @Override
    public void removePlayer(StringsUser user) {
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
    public Map<String, Object> getData() {
        return Collections.emptyMap();
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
        channelLoader.saveChannel(this);
    }

    @Override
    public boolean isCallEvent() {
        return true;
    }

    @Override
    public boolean allows(Permissible permissible) {
        return true;
    }
}
