package com.pedestriamc.strings.chat.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.chat.StringsChannelLoader;
import com.pedestriamc.strings.chat.channel.base.ProtectedChannel;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class LocalDefaultChannel extends ProtectedChannel {

    private final Set<Player> members;
    private final StringsChannelLoader channelLoader;

    public LocalDefaultChannel(Strings strings) {
        super("local");
        members = new HashSet<>();
        channelLoader = (StringsChannelLoader) strings.getChannelLoader();
    }

    @Override
    public void sendMessage(Player player, String message) {
        World world = player.getWorld();
    }

    @Override
    public Type getType() {
        return Type.DEFAULT;
    }

    @Override
    public Set<Player> getMembers() {
        return new HashSet<>(members);
    }

    @Override
    public void addMember(Player player) {
        members.add(player);
    }

    @Override
    public void removeMember(Player player) {
        members.remove(player);
    }
}
