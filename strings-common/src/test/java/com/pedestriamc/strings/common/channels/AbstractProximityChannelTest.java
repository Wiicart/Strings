package com.pedestriamc.strings.common.channels;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.mock.MockLocality;
import com.pedestriamc.strings.common.mock.environment.Environment;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for ProximityChannel implementations.
 */
abstract class AbstractProximityChannelTest extends AbstractChannelTest {

    abstract LocalChannel<?> buildLocalChannel(StringsPlatform strings, LocalChannelBuilder<?> builder);

    @Test
    void getRecipients_userOutOfRange_isExcluded() {
        StringsUser sender = mock(StringsUser.class);
        StringsUser outOfRange = mock(StringsUser.class);

        MockLocality locality = new MockLocality(sender, outOfRange);
        when(sender.distanceSquared(outOfRange)).thenReturn(400.1);
        when(outOfRange.distanceSquared(sender)).thenReturn(400.1);

        when(sender.getLocality()).thenAnswer(i -> locality);
        when(outOfRange.getLocality()).thenAnswer(i -> locality);

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, outOfRange)
                .build();

        LocalChannelBuilder<?> builder =
                new LocalChannelBuilder<>("local", "", Membership.DEFAULT, Set.of(locality))
                        .setDistance(20);

        LocalChannel<?> proximity = buildLocalChannel(strings, builder);

        assertEquals(Set.of(sender), proximity.getRecipients(sender));
    }

    @Test
    void getRecipients_userInRange_isIncluded() {
        StringsUser sender = mock(StringsUser.class);
        StringsUser recipient = mock(StringsUser.class);

        MockLocality locality = new MockLocality(sender, recipient);
        when(sender.distanceSquared(recipient)).thenReturn(100.0);
        when(recipient.distanceSquared(sender)).thenReturn(100.0);

        when(sender.getLocality()).thenAnswer(i -> locality);
        when(recipient.getLocality()).thenAnswer(i -> locality);

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, recipient)
                .build();

        LocalChannelBuilder<?> builder =
                new LocalChannelBuilder<>("local", "", Membership.DEFAULT, Set.of(locality))
                        .setDistance(20);

        LocalChannel<?> proximity = buildLocalChannel(strings, builder);

        assertEquals(Set.of(sender, recipient), proximity.getRecipients(sender));
    }

    @Test
    void getRecipients_userInNumericalRangeButDifferentLocality_isExcluded() {
        StringsUser sender = mock(StringsUser.class);
        StringsUser otherUser = mock(StringsUser.class);

        MockLocality senderLocality = new MockLocality(sender);
        MockLocality otherLocality = new MockLocality(otherUser);

        when(sender.distanceSquared(otherUser)).thenReturn(100.0);
        when(otherUser.distanceSquared(sender)).thenReturn(100.0);

        when(sender.getLocality()).thenAnswer(i -> senderLocality);
        when(otherUser.getLocality()).thenAnswer(i -> otherLocality);

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, otherUser)
                .build();

        LocalChannelBuilder<?> builder =
                new LocalChannelBuilder<>("local", "", Membership.DEFAULT, Set.of(senderLocality, otherLocality))
                        .setDistance(20);

        LocalChannel<?> proximity = buildLocalChannel(strings, builder);

        assertEquals(Set.of(sender), proximity.getRecipients(sender));
        assertEquals(Set.of(otherUser), proximity.getRecipients(otherUser));
    }

    @Override
    @Test
    void getRecipients_userHasSenderIgnored_isExcluded() {
        StringsUser sender = mock(StringsUser.class);
        StringsUser ignorer = mock(StringsUser.class);

        MockLocality locality = new MockLocality(sender, ignorer);
        when(sender.distanceSquared(ignorer)).thenReturn(100.0);
        when(ignorer.distanceSquared(sender)).thenReturn(100.0);
        when(sender.getLocality()).thenAnswer(i -> locality);
        when(ignorer.getLocality()).thenAnswer(i -> locality);

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, ignorer)
                .build();

        LocalChannelBuilder<?> builder =
                new LocalChannelBuilder<>("local", "", Membership.DEFAULT, Set.of(locality))
                        .setDistance(20);
        LocalChannel<?> proximity = buildLocalChannel(strings, builder);

        proximity.addMember(ignorer);

        UUID senderUUID = UUID.randomUUID();
        when(sender.getUniqueId()).thenReturn(senderUUID);

        when(ignorer.getIgnoredPlayers()).thenReturn(Set.of(senderUUID));
        when(ignorer.isIgnoring(sender)).thenReturn(true);

        assertEquals(Set.of(sender), proximity.getRecipients(sender));
    }

    @Override
    @Test
    void getRecipients_userHasChannelMuted_isExcluded() {
        StringsUser sender = mock(StringsUser.class);
        StringsUser muter = mock(StringsUser.class);

        MockLocality locality = new MockLocality(sender, muter);
        when(sender.distanceSquared(muter)).thenReturn(100.0);
        when(muter.distanceSquared(sender)).thenReturn(100.0);
        when(sender.getLocality()).thenAnswer(i -> locality);
        when(muter.getLocality()).thenAnswer(i -> locality);

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, muter)
                .build();

        Channel channel = buildChannel(strings);
        channel.addMember(muter);

        when(muter.getMutedChannels()).thenReturn(Set.of(channel));
        when(muter.hasChannelMuted(channel)).thenReturn(true);

        assertEquals(Set.of(sender), channel.getRecipients(sender));
    }

    @Override
    @Disabled("Not applicable for proximity channels")
    void getRecipients_userIsChannelMember_isIncluded() {}

    @Override
    @Disabled("Not applicable for proximity channels")
    void getRecipients_userHasReceivingPermission_isIncluded() {}

    @Override
    @Disabled("Not applicable for proximity channels")
    void getRecipients_senderNotInScope_includesSender() {}
}
