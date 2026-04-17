package com.pedestriamc.strings.common.channel.impl.local.proximity;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.channel.local.ProximityChannel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.channel.base.AbstractLocalChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;

/**
 * A ProximityChannel that only sends messages to players close enough to the sender.
 * Members are not sent the message by default; to moderate, this Channel can be monitored.
 */
public class StrictHorizontalProximityChannel<T> extends AbstractLocalChannel<T> implements ProximityChannel<T> {

    public static final Identifier IDENTIFIER = Identifier.HORIZONTAL_PROXIMITY_STRICT;

    private double distance;

    // for more efficient calculations.
    @Range(from = 0, to = Integer.MAX_VALUE)
    private double distanceSquared;

    public StrictHorizontalProximityChannel(@NotNull StringsPlatform strings, @NotNull LocalChannelBuilder<T> data) {
        super(strings, data);
        setProximity(data.getDistance());
    }

    @Override
    public @NotNull Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
        Set<StringsUser> recipients = new HashSet<>(getMonitors());

        Locality<?> senderWorld = sender.getLocality();
        for (StringsUser user : senderWorld.getUsers()) {
            if (sender.horizontalDistanceSquared(user) < distanceSquared) {
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

    @Range(from = -1, to = Integer.MAX_VALUE)
    @Override
    public double getProximity() {
        return distance;
    }


    @Override
    public void setProximity(@Range(from = -1, to = Integer.MAX_VALUE) double proximity) {
        distance = proximity;
        distanceSquared = proximity * proximity;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> map = super.getData();
        map.put("distance", String.valueOf(distance));
        return map;
    }

    @Override
    public @NotNull Proximity proximityType() {
        return Proximity.HORIZONTAL;
    }

    @Override
    public boolean isStrict() {
        return true;
    }
}
