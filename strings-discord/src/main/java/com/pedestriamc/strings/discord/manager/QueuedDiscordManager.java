package com.pedestriamc.strings.discord.manager;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.kyori.adventure.chat.SignedMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public final class QueuedDiscordManager implements DiscordManager {

    private DiscordManager impl;

    private final Queue<Consumer<DiscordManager>> queue;

    public QueuedDiscordManager() {
        queue = new ConcurrentLinkedQueue<>();
    }

    // Marks this class as "ready", and from this point on methods will be passed directly to the manager.
    public void complete(@NotNull DiscordManager manager) throws IllegalStateException {
        if (manager instanceof QueuedDiscordManager) {
            throw new IllegalStateException("QueuedDiscordManager cannot be completed with another QueuedDiscordManager");
        }

        if (impl != null) {
            throw new IllegalStateException("QueuedDiscordManager already completed.");
        }

        impl = manager;

        while (!queue.isEmpty()) {
            queue.poll().accept(impl);
        }
    }

    @Override
    public void shutdown() {
        if (impl != null) {
            impl.shutdown();
        } else {
            queue.offer(DiscordManager::shutdown);
        }
    }

    @Override
    public boolean hasDiscordChannel(@NotNull MessageChannelUnion channel) {
        return false;
    }

    @Override
    public boolean hasStringsChannel(@NotNull Channel channel) {
        return false;
    }

    @Override
    public void sendCraftMessageFromEvent(@NotNull MessageReceivedEvent event) {
        if (impl != null) {
            impl.sendCraftMessageFromEvent(event);
        } else {
            queue.offer(manager -> manager.sendCraftMessageFromEvent(event));
        }
    }

    @Override
    public void sendDiscordMessageFromEvent(@NotNull ChannelChatEvent event) {
        if (impl != null) {
            impl.sendDiscordMessageFromEvent(event);
        } else {
            queue.offer(manager -> manager.sendDiscordMessageFromEvent(event));
        }
    }

    @Override
    public void broadcastDiscordMessage(@NotNull String message) {
        if (impl != null) {
            impl.broadcastDiscordMessage(message);
        } else {
            queue.offer(manager -> manager.broadcastDiscordMessage(message));
        }
    }

    @Override
    public void broadcastCraftMessage(@NotNull String message) {
        if (impl != null) {
            impl.broadcastCraftMessage(message);
        } else {
            queue.offer(manager -> manager.broadcastCraftMessage(message));
        }

    }

    @Override
    public void sendDiscordEmbed(@NotNull MessageEmbed embed) {
        if (impl != null) {
            impl.sendDiscordEmbed(embed);
        } else {
            queue.offer(manager -> manager.sendDiscordEmbed(embed));
        }
    }

    @Override
    public Set<MessageChannel> getDiscordChannels() {
        if (impl != null) {
            return impl.getDiscordChannels();
        } else {
            return Set.of();
        }
    }

    @Override
    public void deleteMessage(@NotNull SignedMessage signedMessage) {
        if (impl != null) {
            impl.deleteMessage(signedMessage);
        } else {
            queue.offer(manager -> manager.deleteMessage(signedMessage));
        }
    }
}
