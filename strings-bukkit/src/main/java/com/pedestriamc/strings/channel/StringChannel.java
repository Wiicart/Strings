package com.pedestriamc.strings.channel;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.channel.base.AbstractChannel;
import com.pedestriamc.strings.user.util.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Standard Channel implementation
 */
public final class StringChannel extends AbstractChannel {

    public static final String IDENTIFIER = "stringchannel";

    public StringChannel(JavaPlugin plugin, ChannelBuilder builder) {
        this((Strings) plugin, builder);
    }

    public StringChannel(@NotNull Strings strings, @NotNull ChannelBuilder builder) {
        super(strings, builder);
    }

    @Override
    public @NotNull Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
        Set<StringsUser> recipients = new HashSet<>(getMembers());
        UserUtil userUtil = getUserUtil();
        recipients.addAll(getMonitors());

        if(getMembership() == Membership.DEFAULT) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                recipients.add(userUtil.getUser(p));
                if(p.hasPermission(CHANNEL_PERMISSION + getName() + ".receive")) {
                    recipients.add(userUtil.getUser(p));
                }
            }
        }

        return filterMutesAndIgnores(sender, recipients);
    }

    @Override
    public @NotNull Set<StringsUser> getPlayersInScope() {
        return switch (getMembership()) {
            case DEFAULT -> Bukkit.getOnlinePlayers()
                    .stream()
                    .map(getUserUtil()::getUser)
                    .collect(Collectors.toSet());
            case PROTECTED -> getMembers();
            case PERMISSION -> {
                HashSet<StringsUser> scoped = new HashSet<>(getMembers());
                scoped.addAll(getMonitors());
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(allows(p)) {
                        scoped.add(getUserUtil().getUser(p));
                    }
                }
                yield scoped;
            }
        };
    }

    @Override
    public @NotNull Type getType() {
        return Type.NORMAL;
    }

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }
}