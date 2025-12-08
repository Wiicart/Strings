package com.pedestriamc.common.channel.impl;

import com.pedestriamc.common.channel.base.AbstractChannel;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.IChannelBuilder;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.api.user.UserManager;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Standard Channel implementation
 */
public final class StringChannel extends AbstractChannel {

    public static final Identifier IDENTIFIER = Identifier.NORMAL;

    public StringChannel(StringsPlatform plugin, IChannelBuilder<?> builder) {
        super(plugin, builder);
    }

    @Override
    public @NotNull Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
        Set<StringsUser> recipients = new HashSet<>(getMembers());
        UserManager userManager = getUserManager();
        recipients.addAll(getMonitors());

        if (getMembership() == Membership.DEFAULT) {
            recipients.addAll(userManager.getUsers());
        }

        return filterMutesAndIgnores(sender, recipients);
    }

    @Override
    public @NotNull Set<StringsUser> getPlayersInScope() {
        return switch (getMembership()) {
            case DEFAULT -> new HashSet<>(getUserManager().getUsers());
            case PROTECTED -> getMembers();
            case PERMISSION -> {
                HashSet<StringsUser> scoped = new HashSet<>(getMembers());
                scoped.addAll(getMonitors());
                for (StringsUser user : getUserManager().getUsers()) {
                    if (allows(user)) {
                        scoped.add(user);
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

}