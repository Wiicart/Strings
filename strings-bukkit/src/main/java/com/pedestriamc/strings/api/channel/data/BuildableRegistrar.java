package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.channel.HelpOPChannel;
import com.pedestriamc.strings.channel.StringChannel;
import com.pedestriamc.strings.channel.local.ProximityChannel;
import com.pedestriamc.strings.channel.local.StrictProximityChannel;
import com.pedestriamc.strings.channel.local.StrictWorldChannel;
import com.pedestriamc.strings.channel.local.WorldChannel;

public final class BuildableRegistrar {

    private BuildableRegistrar() {}

    public static void register() {
        ChannelBuilder.registerBuildable("stringchannel", StringChannel::new);
        ChannelBuilder.registerBuildable("proximity", ProximityChannel::new);
        ChannelBuilder.registerBuildable("proximity_strict", StrictProximityChannel::new);
        ChannelBuilder.registerBuildable("world", WorldChannel::new);
        ChannelBuilder.registerBuildable("world_strict", StrictWorldChannel::new);
        ChannelBuilder.registerBuildable("helpop", HelpOPChannel::new);
    }

}
