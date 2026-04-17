package com.pedestriamc.strings.common.channel.impl.local.world;

import com.pedestriamc.strings.api.channel.local.WorldChannel;
import com.pedestriamc.strings.common.channel.base.AbstractLocalChannel;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * A stricter WorldChannel that requires members to be within the channel's scope to receive messages.
 * Players must either be in the scope (one of the Worlds of the Channel)
 * or be a monitor to be eligible via {@link Channel#allows(StringsUser)}
 */
public class StrictWorldChannel<T> extends AbstractLocalChannel<T> implements WorldChannel<T> {

    public static final Identifier IDENTIFIER = Identifier.WORLD_STRICT;

    public StrictWorldChannel(StringsPlatform strings, LocalChannelBuilder<T> builder) {
        super(strings, builder);
    }

    @Override
    public @NotNull Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
        Set<StringsUser> recipients = new HashSet<>(getMonitors());
        for (Locality<T> locality : getWorlds()) {
            recipients.addAll(locality.getUsers());
        }

        for (StringsUser user : getUserManager().getUsers()) {
            if (user.hasPermission("strings.channels." + this.getName() + ".receive")) {
                recipients.add(user);
            }
        }

        recipients.add(sender);

        return filterMutesAndIgnores(sender, recipients);
    }

    @Override
    public boolean allows(@NotNull StringsUser user) {
        return containsInScope(user) && super.allows(user);
    }

    @Override
    public boolean isStrict() {
        return true;
    }

}
