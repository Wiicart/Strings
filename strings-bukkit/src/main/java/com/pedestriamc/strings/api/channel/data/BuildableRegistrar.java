package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.common.channel.impl.HelpOPChannel;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.common.channel.impl.StringChannel;
import com.pedestriamc.strings.common.channel.impl.local.proximity.HorizontalProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.proximity.ProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.proximity.StrictHorizontalProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.proximity.StrictProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.world.StrictWorldChannel;
import com.pedestriamc.strings.common.channel.impl.local.world.WorldChannel;
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
        BuilderRegistry.registerLocalBuildable(StrictHorizontalProximityChannel.IDENTIFIER, StrictHorizontalProximityChannel::new);
        BuilderRegistry.registerLocalBuildable(HorizontalProximityChannel.IDENTIFIER, HorizontalProximityChannel::new);
        BuilderRegistry.registerBuildable(HelpOPChannel.IDENTIFIER, HelpOPChannel::new);
    }
}
