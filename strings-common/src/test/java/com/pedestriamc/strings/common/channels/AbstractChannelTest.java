package com.pedestriamc.strings.common.channels;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.mock.MockLocality;
import com.pedestriamc.strings.common.mock.environment.Environment;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * General tests that can be applied to all non-protected included Channel types.
 * <br/>
 * Most tests check {@link Channel#getRecipients(StringsUser)}
 */
abstract class AbstractChannelTest {

    /**
     * Provides a new instance of the Channel implementation being tested.
     * @param strings A configured mock StringsPlatform
     * @return A new Channel instance
     */
    abstract Channel buildChannel(StringsPlatform strings);

    /**
     * Tests muting functionality in getRecipients.
     */
    @Test
    void getRecipients_userHasSenderIgnored_isExcluded() {
        StringsUser sender = mockUser();
        StringsUser ignorer = mockUser();

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, ignorer)
                .build();

        Channel channel = buildChannel(strings);
        channel.addMember(ignorer);

        UUID senderUUID = UUID.randomUUID();
        when(sender.getUniqueId()).thenReturn(senderUUID);

        when(ignorer.getIgnoredPlayers()).thenReturn(Set.of(senderUUID));
        when(ignorer.isIgnoring(sender)).thenReturn(true);

        assertEquals(Set.of(sender), channel.getRecipients(sender));
    }

    /**
     * Tests muting functionality in getRecipients.
     */
    @Test
    void getRecipients_userHasChannelMuted_isExcluded() {
        StringsUser sender = mockUser();
        StringsUser muter = mockUser();

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, muter)
                .build();

        Channel channel = buildChannel(strings);
        channel.addMember(muter);

        // Channels expected to check if any recipient has the channel muted.
        when(muter.getMutedChannels()).thenReturn(Set.of(channel));
        when(muter.hasChannelMuted(channel)).thenReturn(true);

        assertEquals(Set.of(sender), channel.getRecipients(sender));
    }

    /**
     * Tests that users with permission to receive messages from the Channel receive messages.
     */
    @Test
    void getRecipients_userHasReceivingPermission_isIncluded() {
        StringsUser sender = mockUser();
        StringsUser recipient = mockUser();

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, recipient)
                .build();

        Channel channel = buildChannel(strings);
        when(recipient.hasPermission("strings.channels." + channel.getName() + ".receive")).thenReturn(true);

        assertEquals(Set.of(sender, recipient), channel.getRecipients(sender));
    }

    /**
     * Tests that a user that's a member of a Channel receives messages.
     */
    @Test
    void getRecipients_userIsChannelMember_isIncluded() {
        StringsUser member = mockUser();
        StringsUser sender = mockUser();

        StringsPlatform strings = Environment.builder()
                .withUsers(member, sender)
                .build();

        Channel channel = buildChannel(strings);

        channel.addMember(member);
        when(member.memberOf(channel)).thenReturn(true);

        assertEquals(Set.of(member, sender), channel.getRecipients(sender));
    }

    /**
     * Tests that senders of messages always receive their message as well.
     * The sender has no affiliation with the Channel in this test.
     */
    @Test
    void getRecipients_senderNotInScope_includesSender() {
        StringsUser sender = mockUser();

        StringsPlatform strings = Environment.builder()
                .withUsers(sender)
                .build();

        Channel channel = buildChannel(strings);

        assertEquals(Set.of(sender), channel.getRecipients(sender));
    }

    /**
     * Tests that a user monitoring a Channel, with no other channel affiliation still receives messages.
     * Silently skips if tested Channel does not implement {@link Monitorable}
     */
    @Test
    void getRecipients_userIsMonitoringChannel_isIncluded() {
        StringsUser monitor = mockUser();
        StringsUser sender = mockUser();

        StringsPlatform strings = Environment.builder()
                .withUsers(monitor, sender)
                .build();

        Channel channel = buildChannel(strings);

        // Most Channels are monitorable, but monitoring support isn't required, so this test will skip if N/A
        if (channel instanceof Monitorable monitorable) {
            monitorable.addMonitor(monitor);
            when(monitor.isMonitoring(monitorable)).thenReturn(true);
        } else {
            return;
        }

        assertEquals(Set.of(monitor, sender), channel.getRecipients(sender));
    }

    StringsUser mockUser() {
        StringsUser user = mock(StringsUser.class);
        when(user.getLocality()).thenAnswer(i -> new MockLocality(user));
        return user;
    }
}
