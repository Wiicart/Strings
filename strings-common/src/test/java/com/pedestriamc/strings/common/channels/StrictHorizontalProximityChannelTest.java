package com.pedestriamc.strings.common.channels;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.channel.impl.local.proximity.StrictHorizontalProximityChannel;
import com.pedestriamc.strings.common.mock.MockLocality;
import com.pedestriamc.strings.common.mock.environment.Environment;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StrictHorizontalProximityChannelTest extends AbstractHorizontalProximityChannelTest {

    @Override
    LocalChannel<?> buildLocalChannel(StringsPlatform strings, LocalChannelBuilder<?> builder) {
        return new StrictHorizontalProximityChannel<>(strings, builder);
    }

    @Override
    Channel buildChannel(StringsPlatform strings) {
        LocalChannelBuilder<?> builder = new LocalChannelBuilder<>(
                "proximity_channel",
                "format",
                Membership.DEFAULT,
                Set.of(new MockLocality())
        );

        return new StrictHorizontalProximityChannel<>(strings, builder);
    }

    @Test
    void allows_channelMemberNotInScope_false() {
        StringsUser member = mock(StringsUser.class);

        MockLocality memberLocality = new MockLocality(member);
        MockLocality channelLocality = new MockLocality();

        when(member.getLocality()).thenAnswer(i -> memberLocality);

        LocalChannelBuilder<?> builder = new LocalChannelBuilder<>(
                "world_channel",
                "format",
                Membership.DEFAULT,
                Set.of(channelLocality)
        );

        StringsPlatform strings = Environment.builder()
                .withUsers(member)
                .build();

        StrictHorizontalProximityChannel<?> channel = new StrictHorizontalProximityChannel<>(strings, builder);
        channel.addMember(member);
        when(member.getActiveChannel()).thenReturn(channel);
        when(member.getChannels()).thenReturn(Set.of(channel));

        assertFalse(channel.allows(member));
    }

    @Test
    void getRecipients_memberNotInScope_isExcluded() {
        StringsUser member = mock(StringsUser.class);
        StringsUser sender = mock(StringsUser.class);

        MockLocality memberLocality = new MockLocality(member);
        MockLocality channelLocality = new MockLocality(sender);

        when(member.getLocality()).thenAnswer(i -> memberLocality);
        when(sender.getLocality()).thenAnswer(i -> channelLocality);

        LocalChannelBuilder<?> builder = new LocalChannelBuilder<>(
                "world_channel",
                "format",
                Membership.DEFAULT,
                Set.of(channelLocality)
        );

        StringsPlatform strings = Environment.builder()
                .withUsers(member, sender)
                .build();

        StrictHorizontalProximityChannel<?> channel = new StrictHorizontalProximityChannel<>(strings, builder);
        channel.addMember(member);

        assertEquals(Set.of(sender), channel.getRecipients(sender));
    }

    @Override
    @Disabled("Not applicable for strict local channels")
    void getRecipients_userIsChannelMember_isIncluded() {}
}
