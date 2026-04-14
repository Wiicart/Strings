package com.pedestriamc.strings.common.channels;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.data.LocalChannelBuilder;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.common.channel.impl.local.world.WorldChannel;
import com.pedestriamc.strings.common.mock.MockLocality;
import com.pedestriamc.strings.common.mock.environment.Environment;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link WorldChannel}
 */
abstract class AbstractWorldChannelTest extends AbstractChannelTest {

    abstract LocalChannel<?> buildLocalChannel(StringsPlatform strings, LocalChannelBuilder<?> builder);

    /**
     * Tests that a user not in scope of a world channel, without overriding circumstances
     * like permission, monitoring or membership is excluded when a message is sent.
     */
    @Test
    void getRecipients_userNotInScopeShouldNotBeIncluded_isExcluded() {
        StringsUser sender = mock(StringsUser.class);
        StringsUser recipient = mock(StringsUser.class);

        MockLocality channelLocality = new MockLocality(sender);
        MockLocality recipientLocality = new MockLocality(recipient);

        when(sender.getLocality()).thenAnswer(i -> channelLocality);
        when(recipient.getLocality()).thenAnswer(i -> recipientLocality);

        LocalChannelBuilder<?> builder = new LocalChannelBuilder<>(
                "local",
                "format",
                Membership.DEFAULT,
                Set.of(channelLocality)
        );

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, recipient)
                .build();

        LocalChannel<?> channel = buildLocalChannel(strings, builder);
        assertEquals(Set.of(sender), channel.getRecipients(sender));
    }

    /**
     * Tests that a user outside a Channel's scope with the permission<br/>
     * <code>strings.channels.&lt;channel-name&gt;.receive</code><br/> receives a message,
     * no matter if the user is in scope or not.
     */
    @Test
    void getRecipients_userHasReceivingPermissionButNotInScope_isIncluded() {
        StringsUser sender = mock(StringsUser.class);
        StringsUser recipient = mock(StringsUser.class);

        MockLocality channelLocality = new MockLocality(sender);
        MockLocality recipientLocality = new MockLocality(recipient);

        when(sender.getLocality()).thenAnswer(i -> channelLocality);
        when(recipient.getLocality()).thenAnswer(i -> recipientLocality);
        when(recipient.hasPermission("strings.channels.local.receive")).thenReturn(true);

        LocalChannelBuilder<?> builder = new LocalChannelBuilder<>(
                "local",
                "format",
                Membership.DEFAULT,
                Set.of(channelLocality)
        );

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, recipient)
                .build();

        LocalChannel<?> channel = buildLocalChannel(strings, builder);
        assertEquals(Set.of(sender, recipient), channel.getRecipients(sender));
    }

    /**
     * Tests that if the sender of a message is not a member of the Channel's localities,
     * getRecipients returns all players in the Channel's localities.
     * <p>
     * <code>user1</code> is the sender, <code>user2</code> and <code>user3</code> are in channel localities.
     */
    @Test
    void getRecipients_senderNotInAnyChannelLocality_returnsAllUsersInChannelLocalitiesAndSender() {
        StringsUser sender = mock(StringsUser.class);
        StringsUser recipient1 = mock(StringsUser.class);
        StringsUser recipient2 = mock(StringsUser.class);

        MockLocality senderLocality = new MockLocality(sender);
        MockLocality channelLocality1 = new MockLocality(recipient1);
        MockLocality channelLocality2 = new MockLocality(recipient2);

        when(sender.getLocality()).thenAnswer(i -> senderLocality);
        when(recipient1.getLocality()).thenAnswer(i -> channelLocality1);
        when(recipient2.getLocality()).thenAnswer(i -> channelLocality2);

        LocalChannelBuilder<?> builder = new LocalChannelBuilder<>(
                "local",
                "format",
                Membership.DEFAULT,
                Set.of(channelLocality1, channelLocality2)
        );

        StringsPlatform strings = Environment.builder()
                .withUsers(sender, recipient1, recipient2)
                .build();

        LocalChannel<?> channel = buildLocalChannel(strings, builder);
        assertEquals(Set.of(sender, recipient1, recipient2), channel.getRecipients(sender));
    }

    /**
     * Tests {@link WorldChannel} recipient resolution.
     * Users 1 and 2 are in locality A, while User 3 is in locality B.
     */
    @Test
    void getRecipients_usersInSameLocality_returnsUsersInLocality() {
        StringsUser user1 = mock(StringsUser.class);
        StringsUser user2 = mock(StringsUser.class);
        StringsUser user3 = mock(StringsUser.class);

        MockLocality localityA = new MockLocality(user1, user2);
        MockLocality localityB = new MockLocality(user3);

        when(user1.getLocality()).thenAnswer(i -> localityA);
        when(user2.getLocality()).thenAnswer(i -> localityA);
        when(user3.getLocality()).thenAnswer(i -> localityB);

        LocalChannelBuilder<?> builder = new LocalChannelBuilder<>(
                "world1",
                "format",
                Membership.DEFAULT,
                Set.of(localityA)
        );

        StringsPlatform strings = Environment.builder()
                .withUsers(user1, user2, user3)
                .build();

        LocalChannel<?> channel = buildLocalChannel(strings, builder);
        assertEquals(Set.of(user1, user2), channel.getRecipients(user1));
    }

}
