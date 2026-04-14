package com.pedestriamc.strings.common.channel.impl.local.world;

import com.pedestriamc.strings.common.channel.base.AbstractLocalChannel;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Channel implementation that focuses on one or more Worlds(s) on the server.
 * Expected to be used with the DefaultChannel
 */
public class WorldChannel<T> extends AbstractLocalChannel<T> {

    public static final Identifier IDENTIFIER = Identifier.WORLD;

    public WorldChannel(StringsPlatform strings, LocalChannelBuilder<T> builder) {
        super(strings, builder);
    }

    @Override
    public @NotNull Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
        Set<StringsUser> recipients = new HashSet<>(getMembers());
        recipients.addAll(getMonitors());

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
    public @NotNull Type getType() {
        return Type.WORLD;
    }

    // N/A to this implementation, UnsupportedOperationException always thrown.
    @Override
    public double getProximity() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getProximity() called on WorldChannel instance, which is unsupported.");
    }

    // N/A to this implementation, UnsupportedOperationException always thrown.
    @Override
    public void setProximity(double proximity) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("setProximity() called on WorldChannel instance, which is unsupported.");
    }

}

