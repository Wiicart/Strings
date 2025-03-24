package com.pedestriamc.strings.chat.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.LocalChannel;
import com.pedestriamc.strings.chat.StringsChannelLoader;
import com.pedestriamc.strings.chat.channel.base.ProtectedChannel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * The channel that players are assigned to by default.
 * This channel cannot process any messages; it instead determines the proper channel for a message to be sent to.
 */
public class DefaultChannel extends ProtectedChannel {

    private final StringsChannelLoader channelLoader;
    private final Set<Player> members;
    private final Strings strings;

    public DefaultChannel(Strings strings, @NotNull StringsChannelLoader channelLoader) {
        super("default");
        this.channelLoader = channelLoader;
        this.members = new HashSet<>();
        this.strings = strings;
    }

    @Override
    public void sendMessage(Player player, String message) {
        SortedSet<Channel> channels = channelLoader.getSortedChannelSet();
        channels.removeIf(channel -> !channel.allows(player));
        channels.removeIf(channel -> channel instanceof LocalChannel local && !local.containsInScope(player));

        if(!channels.isEmpty()) {
            channels.first().sendMessage(player, message);
            return;
        }

        User user = strings.getUser(player);
        SortedSet<Channel> usersChannels = new TreeSet<>(user.getChannels());
        usersChannels.remove(this);
        if(!usersChannels.isEmpty()){
            usersChannels.first().sendMessage(player, message);
            return;
        }

        player.sendMessage(ChatColor.RED + "[Strings] You aren't a member of any channels.  Please contact a server operator for help.");
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
    public void addMember(StringsUser user) {
        members.add(user.getPlayer());
    }

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
