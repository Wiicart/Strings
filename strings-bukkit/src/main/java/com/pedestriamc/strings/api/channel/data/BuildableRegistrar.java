package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.common.channel.impl.HelpOPChannel;
import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.common.channel.impl.StringChannel;
import com.pedestriamc.strings.common.channel.impl.local.proximity.HorizontalProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.proximity.SphericalProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.proximity.StrictHorizontalProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.proximity.StrictSphericalProximityChannel;
import com.pedestriamc.strings.common.channel.impl.local.world.StrictWorldChannel;
import com.pedestriamc.strings.common.channel.impl.local.world.StandardWorldChannel;
import org.jetbrains.annotations.NotNull;

/**
 * Registers BiFunctions to create Channel implementations.
 */
public final class BuildableRegistrar {

    private BuildableRegistrar() {}

    public static void register(@NotNull Strings strings) {
        BuilderRegistry.registerPlatform(strings);
        BuilderRegistry.registerBuildable(StringChannel.IDENTIFIER, StringChannel::new);
        BuilderRegistry.registerLocalBuildable(SphericalProximityChannel.IDENTIFIER, SphericalProximityChannel::new);
        BuilderRegistry.registerLocalBuildable(StrictSphericalProximityChannel.IDENTIFIER, StrictSphericalProximityChannel::new);
        BuilderRegistry.registerLocalBuildable(StandardWorldChannel.IDENTIFIER, StandardWorldChannel::new);
        BuilderRegistry.registerLocalBuildable(StrictWorldChannel.IDENTIFIER, StrictWorldChannel::new);
        BuilderRegistry.registerLocalBuildable(StrictHorizontalProximityChannel.IDENTIFIER, StrictHorizontalProximityChannel::new);
        BuilderRegistry.registerLocalBuildable(HorizontalProximityChannel.IDENTIFIER, HorizontalProximityChannel::new);
        BuilderRegistry.registerBuildable(HelpOPChannel.IDENTIFIER, HelpOPChannel::new);
    }
}
