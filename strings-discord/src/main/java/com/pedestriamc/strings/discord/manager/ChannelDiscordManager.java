package com.pedestriamc.strings.discord.manager;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.ChannelLoader;
import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.manager.member.MemberDirectory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public final class ChannelDiscordManager extends AbstractDiscordManager {

    private final StringsDiscord strings;

    private final BidiMap<Channel, MessageChannel> channels = new DualHashBidiMap<>();

    private final MemberDirectory directory;

    public ChannelDiscordManager(@NotNull StringsDiscord strings) {
        super(strings);
        this.strings = strings;

        loadChannels();
        directory = new MemberDirectory(this, strings.getJda());
    }

    public void loadChannels() {
        try {
            StringsAPI api = StringsProvider.get();
            ChannelLoader loader = api.getChannelLoader();

            ConfigurationSection section = strings.getConfig().getConfigurationSection("channels");
            if (section == null) {
                return;
            }

            for (String key : section.getKeys(false)) {
                strings.getLogger().info("Loading channel: " + key);
                Channel channel = loader.getChannel(key);
                if (channel == null) {
                    strings.getLogger().info("Strings didnt provide Channel: " + key);
                    continue;
                }

                String discordId = section.getString(key);
                strings.getLogger().info("discord id from section " + key +" is " + discordId);
                if (discordId != null) {
                    strings.getLogger().info("discord key not null");
                    MessageChannel messageChannel = getMessageChannelById(discordId);
                    if (messageChannel != null) {
                        channels.put(channel, messageChannel);
                        strings.getLogger().info("Loaded channel " + messageChannel.getName());
                    }
                }
            }

        } catch(Exception e) {
            strings.getLogger().severe("Failed to load channels");
            strings.getLogger().severe(e.getMessage());
            strings.getLogger().severe("Shutting down...");
            strings.getServer().getPluginManager().disablePlugin(strings);
        }
    }

    private @Nullable MessageChannel getMessageChannelById(@NotNull String id) {
        GuildChannel guildChannel = strings.getJda().getGuildChannelById(id);
        if (guildChannel == null) {
            return null;
        }

        if (guildChannel instanceof MessageChannel messageChannel) {
            return messageChannel;
        }

        return null;
    }

    public boolean hasDiscordChannel(@NotNull MessageChannelUnion channel) {
        return channels.containsValue(channel);
    }

    public boolean hasStringsChannel(@NotNull Channel channel) {
        return channels.containsKey(channel);
    }

    public void sendCraftMessageFromEvent(@NotNull MessageReceivedEvent event) {
        MessageChannelUnion union = event.getChannel();
        Channel channel = channels.getKey(union);
        if (channel == null) {
            return;
        }

        String formatted = processCraftPlaceholders(event);

        Member sender = event.getMember();
        if (sender != null) {
            handleMentionsFromDiscord(formatted, sender);
        }

        formatted = sanitizeMentions(formatted, directory.getMembers(event.getGuild()));

        final String finalString = formatted;
        strings.synchronous(() -> channel.broadcastPlain(finalString));
    }

    public void sendDiscordMessageFromEvent(@NotNull ChannelChatEvent event) {
        MessageChannel channel = channels.get(event.getChannel());
        if (channel == null) {
            return;
        }

        String message = ChatColor.stripColor(processDiscordPlaceholders(event));

        String finalMessage = processMentionsIfNecessary(message, event.getPlayer(), channel);

        try {
            channel.sendMessage(finalMessage).queue();
        } catch(Exception e) {
            strings.getLogger().warning("Failed to pass message to Channel " + channel.getName());
            strings.getLogger().warning(e.getMessage());
        }
    }

    private @NotNull String processMentionsIfNecessary(@NotNull String msg, @NotNull Player p, @NotNull MessageChannel c) {
        if (!(c instanceof GuildChannel gc)) {
            return msg;
        }

        if(playerHasBasicMentionPermission(p)) {
            Guild guild = gc.getGuild();
            msg = convertMentionsToDiscordFormat(msg, guild, directory.getMembers(guild));
        }

        if(playerCannotMentionEveryone(p)) {
            msg = msg
                    .replaceAll("(?i)@everyone", "@\u200Beveryone")
                    .replaceAll("(?i)@here", "@\u200Bhere");
        }

        return msg;
    }

    @Override
    public void broadcastDiscordMessage(@NotNull String message) {
        for (MessageChannel channel : channels.values()) {
            try {
                channel.sendMessage(message).queue();
            } catch(Exception e) {
                strings.getLogger().warning("Failed to pass message to Channel " + channel.getName());
                strings.getLogger().warning(e.getMessage());
            }
        }
    }

    @Override
    public void broadcastCraftMessage(@NotNull String message) {
        strings.getServer().broadcastMessage(message);
    }

    @Override
    public void sendDiscordEmbed(@NotNull MessageEmbed embed) {
        for (MessageChannel channel : channels.values()) {
            try {
                channel.sendMessageEmbeds(embed).queue();
            } catch(Exception ignored) {}
        }
    }

    @Contract(" -> new")
    @Override
    public @NotNull Set<MessageChannel> getDiscordChannels() {
        return new HashSet<>(channels.values());
    }

}
