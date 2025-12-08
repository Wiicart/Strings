package com.pedestriamc.common.channel.impl.local;

import com.pedestriamc.common.channel.base.AbstractLocalChannel;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.message.Message;
import com.pedestriamc.strings.api.message.Messenger;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import static com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A WorldChannel that does not send player messages to all members of the Channel.
 * Players must either be in the scope (one of the Worlds of the Channel) or be a monitor.
 */
public class StrictWorldChannel<T> extends AbstractLocalChannel<T> {

    public static final Identifier IDENTIFIER = Identifier.WORLD_STRICT;

    private final Channel defaultChannel;
    private final Messenger messenger;

    public StrictWorldChannel(StringsPlatform strings, LocalChannelBuilder<T> builder) {
        super(strings, builder);
        defaultChannel = strings.getChannelLoader().getChannel("default");
        messenger = strings.getMessenger();
    }

    @Override
    public void sendMessage(@NotNull StringsUser user, @NotNull String message) {
        if (containsInScope(user)) {
            super.sendMessage(user, message); // Super class logic references this class's getRecipients impl
        } else {
            defaultChannel.sendMessage(user, message);
            messenger.sendMessage(Message.INELIGIBLE_SENDER, user, getPlaceholders());
        }
    }

    @Override
    public @NotNull Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
        HashSet<StringsUser> recipients = new HashSet<>(getMonitors());
        for (Locality<T> locality : getWorlds()) {
            recipients.addAll(locality.getUsers());
        }

        for (StringsUser user : getUserManager().getUsers()) {
            if (user.hasPermission("strings.channels." + this.getName() + ".receive")) {
                recipients.add(user);
            }
        }

        return filterMutesAndIgnores(sender, recipients);
    }

    @Override
    public @NotNull Type getType() {
        return Type.WORLD;
    }

    @Contract(" -> new")
    private @NotNull @Unmodifiable Map<String, String> getPlaceholders() {
        return Map.of("{channel}", getName());
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
