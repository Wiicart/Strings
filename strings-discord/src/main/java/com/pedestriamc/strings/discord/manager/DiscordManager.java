package com.pedestriamc.strings.discord.manager;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface DiscordManager {

    void shutdown();

    /**
     * Tells if the provided Discord Channel is configured for StringsDiscord.
     * @param channel The Discord Channel to check.
     * @return True/false
     */
    boolean hasDiscordChannel(@NotNull MessageChannelUnion channel);

    /**
     * Tells if the provided StringsChannel is used with StringsDiscord.
     * @param channel The Channel to check.
     * @return True/false
     */
    @SuppressWarnings("unused")
    boolean hasStringsChannel(@NotNull Channel channel);

    /**
     * Accepts a Discord MessageReceivedEvent, and sends it to the appropriate players in chat, if necessary.
     * @param event The Discord Event
     */
    void sendCraftMessageFromEvent(@NotNull MessageReceivedEvent event);

    /**
     * Accepts a Strings ChannelChatEvent, and sends it to the appropriate Discord Channels.
     * @param event The Event
     */
    void sendDiscordMessageFromEvent(@NotNull ChannelChatEvent event);

    /**
     * Sends a message to Discord
     * @param message The message to send
     */
    void broadcastDiscordMessage(@NotNull String message);

    /**
     * Sends a message to Minecraft
     * @param message The message to send
     */
    void broadcastCraftMessage(@NotNull String message);

    void sendDiscordEmbed(@NotNull MessageEmbed embed);

    Set<MessageChannel> getDiscordChannels();

}
