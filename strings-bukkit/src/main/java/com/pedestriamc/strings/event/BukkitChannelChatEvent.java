package com.pedestriamc.strings.event;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.ChannelChatEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public class BukkitChannelChatEvent implements ChannelChatEvent {

    private final StringsUser sender;
    private final Set<StringsUser> recipients;
    private final Channel channel;
    private final SignedMessage signedMessage;

    private String message;
    private boolean isCancelled = false;

    public BukkitChannelChatEvent(
            boolean async,
            boolean cancellable,
            @NotNull StringsUser sender,
            @NotNull String message,
            @NotNull Set<StringsUser> recipients,
            @NotNull Channel channel
    ) {
        this.sender = sender;
        this.recipients = recipients;
        this.message = message;
        this.channel = channel;
        signedMessage = null;
    }

    public BukkitChannelChatEvent(
            boolean async,
            boolean cancellable,
            @NotNull StringsUser sender,
            @NotNull String message,
            @NotNull Set<StringsUser> recipients,
            @NotNull Channel channel,
            SignedMessage signedMessage
    ) {
        this.sender = sender;
        this.message = message;
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

    @Override
    public @NotNull String getMessage() {
        return message;
    }

    @Override
    public void setMessage(@NotNull String message) {
        this.message = message;
    }

    @NotNull
    @Override
    public Optional<SignedMessage> getSignedMessage() {
        return Optional.ofNullable(signedMessage);
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
}
