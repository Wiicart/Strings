package com.pedestriamc.strings.misc;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Type;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Class with methods for simplifying bStats analytics.
 */
public class Analytics {

    private boolean isUsingLocalChannels = false;

    private boolean isUsingProximityChannels = false;

    private boolean isUsingWorldChannels = false;


    public Analytics(@NotNull Strings strings) {
        Set<Channel> channels = strings.getChannelLoader().getChannels();

        for (Channel channel : channels) {
            if (channel instanceof LocalChannel<?>) {
                isUsingLocalChannels = true;
                break;
            }
        }

        for (Channel channel : channels) {
            if (channel.getType() == Type.PROXIMITY) {
                isUsingProximityChannels = true;
                break;
            }
        }

        for (Channel channel : channels) {
            if (channel.getType() == Type.WORLD) {
                isUsingWorldChannels = true;
            }
        }
    }

    public boolean isUsingLocalChannels() {
        return isUsingLocalChannels;
    }

    public boolean isUsingProximityChannels() {
        return isUsingProximityChannels;
    }

    public boolean isUsingWorldChannels() {
        return isUsingWorldChannels;
    }
}
