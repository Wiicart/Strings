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
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import static com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A ProximityChannel that only sends messages to players close enough to the sender.
 * Members are not sent the message by default; to moderate, this Channel can be monitored.
 */
public class StrictProximityChannel<T> extends AbstractLocalChannel<T> {

    public static final Identifier IDENTIFIER = Identifier.PROXIMITY_STRICT;

    @Range(from = 0, to = Integer.MAX_VALUE)
    private double distance;

    // for more efficient calculations.
    private double distanceSquared;

    private final Channel defaultChannel;
    private final Messenger messenger;

    public StrictProximityChannel(StringsPlatform strings, LocalChannelBuilder<T> builder) {
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
        Locality<?> senderWorld = sender.getLocality();

        for (StringsUser user : senderWorld.getUsers()) {
            if (sender.distanceSquared(user) < distanceSquared) {
                recipients.add(user);
            }
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
        return Type.PROXIMITY;
    }

    @Contract(" -> new")
    private @NotNull @Unmodifiable Map<String, String> getPlaceholders() {
        return Map.of("{channel}", getName());
    }

    @Override
    public @Range(from = -1, to = Integer.MAX_VALUE) double getProximity() throws UnsupportedOperationException {
        return distance;
    }

    @Override
    public void setProximity(@Range(from = -1, to = Integer.MAX_VALUE) double proximity) throws UnsupportedOperationException {
        distance = proximity;
        distanceSquared = proximity * proximity;
    }
}
