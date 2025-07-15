package com.pedestriamc.strings.discord.manager;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.manager.member.MemberDirectory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Implementation that makes no consideration to Channels.
public class GlobalDiscordManager extends AbstractDiscordManager {

    private final StringsDiscord strings;

    private final Set<MessageChannel> discordChannels;

    private final MemberDirectory directory;

    public GlobalDiscordManager(@NotNull StringsDiscord strings) {
        super(strings);
        this.strings = strings;
        discordChannels = loadDiscordChannels();
        directory = new MemberDirectory(this, strings.getJda());
    }

    @NotNull
    private Set<MessageChannel> loadDiscordChannels() {
        final Set<MessageChannel> channels = new HashSet<>();
        final JDA jda = strings.getJda();
        final FileConfiguration config = strings.getConfig();

        final List<String> channelIds = config.getStringList("global-channels");
        for(String id : channelIds) {
            GuildChannel channel = jda.getGuildChannelById(id);
            if(channel instanceof MessageChannel messageChannel) {
                channels.add(messageChannel);
                strings.getLogger().info("Loaded Discord Channel " + channel.getName());
            } else {
                if(channel == null) {
                    strings.getLogger().info("Could not load Discord Channel " + id);
                } else {
                    strings.getLogger().info("Failed to load Discord Channel " + channel.getName() +
                            " (is it a text Channel)?");
                }
            }
        }

        return channels;
    }

    @Override
    public void sendCraftMessageFromEvent(@NotNull MessageReceivedEvent event) {
        if (!hasDiscordChannel(event.getChannel())) {
            return;
        }

        String formatted = super.processCraftPlaceholders(event);

        Member sender = event.getMember();
        if(sender != null) { // don't need to check if enabled, method will.
            handleMentionsFromDiscord(formatted, sender);
        }

        formatted = sanitizeMentions(formatted, directory.getMembers(event.getGuild()));

        String finalString = formatted; // final for lambda
        strings.sync(() -> broadcastCraftMessage(finalString));
    }

    @Override
    public void sendDiscordMessageFromEvent(@NotNull ChannelChatEvent event) {
        String message = ChatColor.stripColor(super.processDiscordPlaceholders(event));
        Player player = event.getPlayer();

        for(MessageChannel channel : discordChannels) {
            final String finalMessage = processMentionsIfNecessary(message, player, channel);
            try {
                channel.sendMessage(finalMessage).queue();
            } catch(Exception e) {
                strings.getLogger().warning("Failed to pass message to Channel " + channel.getName());
                strings.getLogger().warning(e.getMessage());
            }
        }
    }

    @Override
    public void broadcastDiscordMessage(@NotNull String message) {
        for(MessageChannel channel : discordChannels) {
            try {
                channel.sendMessage(message).queue();
            } catch(Exception ignored) {}
        }
    }

    @Override
    public void broadcastCraftMessage(@NotNull String message) {
        strings.getServer().broadcastMessage(message);
    }

    @Override
    public void sendDiscordEmbed(@NotNull MessageEmbed embed) {
        for (MessageChannel channel : discordChannels) {
            channel.sendMessageEmbeds(embed).queue();
        }
    }

    private @NotNull String processMentionsIfNecessary(@NotNull String msg, @NotNull Player p, @NotNull MessageChannel c) {
        if(!(c instanceof GuildChannel gc)) {
            return msg;
        }

        if(playerHasBasicMentionPermission(p)) {
            Guild guild = gc.getGuild();
            msg = convertMentionsToDiscordFormat(msg, guild, directory.getMembers(guild));
        }

        // u200B is a zero-width space, looks the same but will no longer match
        if(playerCannotMentionEveryone(p)) {
            msg = msg
                    .replaceAll("(?i)@everyone", "@\u200Beveryone")
                    .replaceAll("(?i)@here", "@\u200Bhere");
        }

        return msg;
    }

    @Override
    public boolean hasDiscordChannel(@NotNull MessageChannelUnion channel) {
        return discordChannels.contains(channel);
    }

    @Override
    public boolean hasStringsChannel(@NotNull Channel channel) {
        return channel.callsEvents();
    }

    @Override
    public Set<MessageChannel> getDiscordChannels() {
        return new HashSet<>(discordChannels);
    }
}
