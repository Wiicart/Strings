package com.pedestriamc.strings.common.channels;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.common.channel.impl.StringChannel;

/**
 * Tests for {@link StringChannel} where <code>membership =</code> {@link Membership#DEFAULT}
 */
class DefaultStringChannelTest extends AbstractChannelTest {

    @Override
    Channel buildChannel(StringsPlatform strings) {
        ChannelBuilder builder = new ChannelBuilder("stringchannel", "format", Membership.DEFAULT);
        return new StringChannel(strings, builder);
    }

}
