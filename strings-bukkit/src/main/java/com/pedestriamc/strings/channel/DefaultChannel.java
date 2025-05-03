package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.chat.ChannelManager;
import com.pedestriamc.strings.channel.base.ProtectedChannel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Players are assigned to this Channel by default.
 * This channel cannot process any messages; it instead determines the proper channel for a message to be sent to.
 */
public final class DefaultChannel extends ProtectedChannel {

    private final ChannelManager channelManager;
    private final Set<Player> members;
    private final Strings strings;

    public DefaultChannel(Strings strings, @NotNull ChannelManager channelManager) {
        super("default");
        this.channelManager = channelManager;
        this.members = new HashSet<>();
        this.strings = strings;
    }

    @Override
    public void sendMessage(@NotNull Player player, @NotNull String message) {
        Channel channel = determineChannel(player);
        if (channel != null) {
            channel.sendMessage(player, message);
            return;
        }
        player.sendMessage(ChatColor.RED + "[Strings] You aren't a member of any channels.  Please contact a server operator for help.");
    }


    /**
     * Determines what Channel a message should be sent in.
     * @param player The player to determine a Channel for
     * @return The Channel with the highest numerical priority that the player is in scope of that allows the player.
     */
    @Nullable
    public Channel determineChannel(@NotNull Player player) {
        SortedSet<Channel> channels = channelManager.getSortedChannelSet();
        channels.removeIf(channel -> !channel.allows(player));
        channels.removeIf(channel -> channel instanceof LocalChannel local && !local.containsInScope(player));

        if(!channels.isEmpty()) {
            return channels.first();
        }

        User user = strings.getUserUtil().getUser(player);
        SortedSet<Channel> usersChannels = new TreeSet<>(user.getChannels());
        usersChannels.remove(this);
        if(!usersChannels.isEmpty()) {
            return usersChannels.first();
        }

        return null;
    }

    @Override
    public @NotNull Type getType() {
        return Type.DEFAULT;
    }

    @Override
    public boolean allows(@NotNull Permissible permissible) {
        return true;
    }

    @Override
    public void setName(@NotNull String name) { /* DefaultChannel instances are always named "default" */ }

    @Override
    public void addMember(@NotNull Player player) {
        members.add(player);
    }

    @Override
    public void removeMember(@NotNull Player player) {
        members.remove(player);
    }

    @Override
    public void addMember(@NotNull StringsUser user) {
        members.add(user.getPlayer());
    }

    @Override
    public void removeMember(@NotNull StringsUser user) {
        members.remove(user.getPlayer());
    }

    @Override
    public Set<Player> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public void saveChannel() {
        channelManager.saveChannel(this);
    }

}
