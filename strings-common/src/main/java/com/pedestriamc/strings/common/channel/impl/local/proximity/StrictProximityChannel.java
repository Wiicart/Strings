package com.pedestriamc.strings.common.channel.impl.local.proximity;

import com.pedestriamc.strings.common.channel.base.AbstractLocalChannel;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.Locality;
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

    private double distance;

    // for more efficient calculations.
    @Range(from = 0, to = Integer.MAX_VALUE)
    private double distanceSquared;

    public StrictProximityChannel(@NotNull StringsPlatform strings, @NotNull LocalChannelBuilder<T> data) {
        super(strings, data);
        setProximity(data.getDistance());
    }

    @Override
    public @NotNull Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
        Set<StringsUser> recipients = new HashSet<>(getMonitors());

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

        recipients.add(sender);

        return filterMutesAndIgnores(sender, recipients);
    }

    @Override
    public boolean allows(@NotNull StringsUser user) {
        return containsInScope(user) && super.allows(user);
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
