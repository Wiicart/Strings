package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.channel.PartyChannel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.channel.base.AbstractChannel;
import com.pedestriamc.strings.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A Channel used for Parties, these channels cannot be saved and are deleted on reboot or when all players leave.
 */
@SuppressWarnings("all")
public class StringsPartyChannel extends AbstractChannel implements PartyChannel {

    private String name;
    
    private User leader;
    
    private ChannelLoader channelLoader;
    private Set<Player> monitors;


    @Override
    public @NotNull StringsUser getLeader() {
        return leader;
    }

    @Override
    public void setLeader(@NotNull StringsUser user) {
        leader = (User) user;
    }

    protected StringsPartyChannel(Strings strings, IChannelBuilder<?> builder) {
        super(strings, builder);
    }
    
    @Override
    public @NotNull Type getType() {
        return Type.PARTY;
    }

    @Override
    public Set<StringsUser> getRecipients(@NotNull StringsUser user) {
        return Set.of();
    }

    @Override
    public Set<StringsUser> getPlayersInScope() {
        return Set.of();
    }

    @NotNull
    private Set<Player> combine(Collection<Player> @NotNull ... collections) {
        Set<Player> set = new HashSet<>();
        for (Collection<Player> collection : collections) {
            set.addAll(collection);
        }
        return set;
    }

}
