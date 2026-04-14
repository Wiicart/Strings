package com.pedestriamc.strings.common.channels;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.common.channel.impl.local.proximity.HorizontalProximityChannel;
import com.pedestriamc.strings.common.mock.MockLocality;

import java.util.Set;

public class HorizontalProximityChannelTest extends AbstractHorizontalProximityChannelTest {

    @Override
    LocalChannel<?> buildLocalChannel(StringsPlatform strings, LocalChannelBuilder<?> builder) {
        return new HorizontalProximityChannel<>(strings, builder);
    }

    @Override
    Channel buildChannel(StringsPlatform strings) {
        LocalChannelBuilder<?> builder = new LocalChannelBuilder<>(
                "proximity_channel",
                "format",
                Membership.DEFAULT,
                Set.of(new MockLocality())
        );

        return new HorizontalProximityChannel<>(strings, builder);
    }

}
