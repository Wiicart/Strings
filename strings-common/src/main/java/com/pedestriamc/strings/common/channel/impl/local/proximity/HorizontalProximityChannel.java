package com.pedestriamc.strings.common.channel.impl.local.proximity;

import com.pedestriamc.strings.common.channel.base.AbstractLocalChannel;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.Locality;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import static com.pedestriamc.strings.api.channel.data.IChannelBuilder.Identifier;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Channel implementation that sends messages based off players in proximity in the sender's World.
 * Worlds this Channel is effective in must be defined.
 * Expected to be used with the DefaultChannel
 */
public class HorizontalProximityChannel<T> extends AbstractLocalChannel<T> {

    public static final Identifier IDENTIFIER = Identifier.HORIZONTAL_PROXIMITY;

    @Range(from = -1, to = Integer.MAX_VALUE)
    private double distance;

    // for more efficient calculations.
    private double distanceSquared;

    public HorizontalProximityChannel(StringsPlatform strings, LocalChannelBuilder<T> builder) {
        super(strings, builder);
        distance = builder.getDistance();
        distanceSquared = distance * distance;
    }

    @Override
    public @NotNull Set<StringsUser> getRecipients(@NotNull StringsUser sender) {
        Set<StringsUser> members = getMembers();
        if (members.contains(sender)) {
            return filterMutesAndIgnores(sender, universalSet());
        }

        Locality<?> senderWorld = sender.getLocality();
        HashSet<StringsUser> recipients = new HashSet<>(members);
        recipients.addAll(getMonitors());

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
    public @NotNull Type getType() {
        return Type.PROXIMITY;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> map = super.getData();
        map.put("distance", String.valueOf(distance));
        return map;
    }

    @Override
    public double getProximity() {
        return distance;
    }

    @Override
    public void setProximity(@Range(from = 0, to = Integer.MAX_VALUE) double proximity) {
        this.distance = proximity;
        distanceSquared = distance * distance;
    }

}
