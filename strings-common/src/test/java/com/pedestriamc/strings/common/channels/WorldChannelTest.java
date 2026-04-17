package com.pedestriamc.strings.common.channels;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.common.channel.impl.local.world.StandardWorldChannel;
import com.pedestriamc.strings.common.mock.MockLocality;

import java.util.Set;

/**
 * Tests for {@link StandardWorldChannel}
 */
class WorldChannelTest extends AbstractWorldChannelTest {

    @Override
    Channel buildChannel(StringsPlatform strings) {
        LocalChannelBuilder<?> builder = new LocalChannelBuilder<>(
                "world_channel",
                "format",
                Membership.DEFAULT,
                Set.of(new MockLocality())
        );

        return new StandardWorldChannel<>(strings, builder);
    }

    @Override
    LocalChannel<?> buildLocalChannel(StringsPlatform strings, LocalChannelBuilder<?> builder) {
        return new StandardWorldChannel<>(strings, builder);
    }
}
