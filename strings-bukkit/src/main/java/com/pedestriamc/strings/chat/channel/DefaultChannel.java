package com.pedestriamc.strings.chat.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.chat.StringsChannelLoader;
import com.pedestriamc.strings.chat.channel.base.ProtectedChannel;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.api.channel.Type;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The channel that players are assigned to by default.
 * This channel cannot process any messages; it instead determines the proper channel to be used.
 */
public class DefaultChannel extends ProtectedChannel {

    private final StringsChannelLoader channelLoader;
    private final Set<Player> members;
    private final Strings strings;

    public DefaultChannel(Strings strings, @NotNull ChannelLoader channelLoader) {
        super("default");
        this.channelLoader = (StringsChannelLoader) channelLoader;
        this.members = new HashSet<>();
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
            optional.ifPresent(channel -> channel.sendMessage(player, message));
        }

        player.sendMessage(ChatColor.RED + "[Strings] You aren't a member of any channels.  Please contact staff for help.");
    }

    @Override
    public final Type getType() {
        return Type.DEFAULT;
    }

    @Override
    public boolean allows(Permissible permissible) {
        return true;
    }

    @Override
    public final void setName(String name) { /* DefaultChannel instances are always named "default" */ }

    @Override
    public void addMember(Player player) {
        members.add(player);
    }

    @Override
    public void removeMember(Player player) {
        members.remove(player);
    }

    @Override
    public void addMember(StringsUser user) { members.add(user.getPlayer()); }

    @Override
    public void removeMember(StringsUser user) {
        members.remove(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public void saveChannel() {
        channelLoader.saveChannel(this);
    }

}
