package com.pedestriamc.strings.event;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.StringsChatEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import com.pedestriamc.strings.user.User;
import net.kyori.adventure.chat.SignedMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ChannelChatEvent2 extends AsyncPlayerChatEvent implements StringsChatEvent {

    private static Set<Player> convertToPlayers(@NotNull Set<StringsUser> users) {
        return users.stream()
                .map(User::playerOf)
                .collect(Collectors.toSet());
    }

    private final StringsUser sender;
    private final Set<StringsUser> recipients;
    private final Channel channel;
    private final SignedMessage signedMessage;

    public ChannelChatEvent2(
            boolean async,
            boolean cancellable,
            @NotNull StringsUser sender,
            @NotNull String message,
            @NotNull Set<StringsUser> recipients,
            @NotNull Channel channel
    ) {
        super(async, User.playerOf(sender), message, convertToPlayers(recipients));
        this.sender = sender;
        this.recipients = recipients;
        this.channel = channel;
        signedMessage = null;
    }

    public ChannelChatEvent2(
            boolean async,
            boolean cancellable,
            @NotNull StringsUser sender,
            @NotNull String message,
            @NotNull Set<StringsUser> recipients,
            @NotNull Channel channel,
            SignedMessage signedMessage
    ) {
        super(async, User.playerOf(sender), message, convertToPlayers(recipients));
        this.sender = sender;
        this.recipients = recipients;
        this.channel = channel;
        this.signedMessage = signedMessage;
    }

    @NotNull
    @Override
    public Channel getChannel() {
        return channel;
    }

    @NotNull
    @Override
    public Set<StringsUser> getMessageRecipients() {
        return recipients;
    }

    @NotNull
    @Override
    public StringsUser getSender() {
        return sender;
    }

    @NotNull
    @Override
    public Optional<SignedMessage> getSignedMessage() {
        return Optional.ofNullable(signedMessage);
    }
}
