package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.common.channel.impl.HelpOPChannel;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.common.channel.impl.StringChannel;
import com.pedestriamc.strings.common.channel.impl.local.ProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.StrictProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.StrictWorldChannel;
import com.pedestriamc.strings.common.channel.impl.local.WorldChannel;
import org.jetbrains.annotations.NotNull;

/**
 * Registers BiFunctions to create Channel implementations.
 */
public final class BuildableRegistrar {

    private BuildableRegistrar() {}

    public static void register(@NotNull Strings strings) {
        BuilderRegistry.registerPlatform(strings);
        BuilderRegistry.registerBuildable(StringChannel.IDENTIFIER, StringChannel::new);
        BuilderRegistry.registerLocalBuildable(ProximityChannel.IDENTIFIER, ProximityChannel::new);
        BuilderRegistry.registerLocalBuildable(StrictProximityChannel.IDENTIFIER, StrictProximityChannel::new);
        BuilderRegistry.registerLocalBuildable(WorldChannel.IDENTIFIER, WorldChannel::new);
        BuilderRegistry.registerLocalBuildable(StrictWorldChannel.IDENTIFIER, StrictWorldChannel::new);
        BuilderRegistry.registerBuildable(HelpOPChannel.IDENTIFIER, HelpOPChannel::new);
    }
}
