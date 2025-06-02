package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.channel.HelpOPChannel;
import com.pedestriamc.strings.channel.StringChannel;
import com.pedestriamc.strings.channel.local.ProximityChannel;
import com.pedestriamc.strings.channel.local.StrictProximityChannel;
import com.pedestriamc.strings.channel.local.StrictWorldChannel;
import com.pedestriamc.strings.channel.local.WorldChannel;

/**
 * Registers BiFunctions to create Channel implementations.
 */
public final class BuildableRegistrar {

    private BuildableRegistrar() {}

    public static void register() {
        ChannelBuilder.registerBuildable(StringChannel.IDENTIFIER, StringChannel::new);
        ChannelBuilder.registerBuildable(ProximityChannel.IDENTIFIER, ProximityChannel::new);
        ChannelBuilder.registerBuildable(StrictProximityChannel.IDENTIFIER, StrictProximityChannel::new);
        ChannelBuilder.registerBuildable(WorldChannel.IDENTIFIER, WorldChannel::new);
        ChannelBuilder.registerBuildable(StrictWorldChannel.IDENTIFIER, StrictWorldChannel::new);
        ChannelBuilder.registerBuildable(HelpOPChannel.IDENTIFIER, HelpOPChannel::new);
    }
}
