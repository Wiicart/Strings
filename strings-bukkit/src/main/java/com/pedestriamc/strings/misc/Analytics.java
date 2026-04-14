package com.pedestriamc.strings.misc;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import org.jetbrains.annotations.NotNull;

/**
 * Class with methods for simplifying bStats analytics.
 */
public class Analytics {

    private final Strings strings;

    public Analytics(@NotNull Strings strings) {
        this.strings = strings;
    }

    public boolean isUsingLocalChannels() {
        for (Channel channel : strings.getChannelLoader().getChannels()) {
            if (channel instanceof LocalChannel<?>) {
                return true;
            }
        }

        return false;
    }

    public boolean isUsingProximityChannels() {
        for (Channel channel : strings.getChannelLoader().getChannels()) {
            if (channel.getType() == Type.PROXIMITY) {
                 return true;
            }
        }

        return false;
    }

    public boolean isUsingWorldChannels() {
        for (Channel channel : strings.getChannelLoader().getChannels()) {
            if (channel.getType() == Type.WORLD) {
                return true;
            }
        }

        return false;
    }
}
