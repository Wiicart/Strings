package com.pedestriamc.strings.common.channels;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.ChannelBuilder;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.channel.impl.StringChannel;
import com.pedestriamc.strings.common.mock.environment.Environment;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link StringChannel} where <code>membership =</code> {@link Membership#PERMISSION}
 */
class PermissionStringChannelTest extends AbstractChannelTest {

    @Override
    Channel buildChannel(StringsPlatform strings) {
        ChannelBuilder builder = new ChannelBuilder("stringchannel", "format", Membership.PERMISSION);
        return new StringChannel(strings, builder);
    }

    @Test
    void getRecipients_userHasNoAffiliation_isExcluded() {
        StringsUser sender = mock(StringsUser.class);
        StringsUser unaffiliated = mock(StringsUser.class);

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, unaffiliated)
                .build();

        Channel channel = buildChannel(strings);

        assertEquals(Set.of(sender), channel.getRecipients(sender));
    }

}
