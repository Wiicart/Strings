package com.pedestriamc.strings.discord.manager.console;

import com.pedestriamc.strings.discord.StringsDiscord;
import com.pedestriamc.strings.discord.configuration.Option;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class ConsoleDiscordManager extends ListenerAdapter {

    private final StringsDiscord strings;

    private final LogAppender appender;

    private final MessageChannel consoleChannel;

    private final ConsoleCommandSender consoleSender;

    /**
     * Constructs a ConsoleDiscordManager
     * @param strings The StringsDiscord instance
     * @throws IllegalArgumentException If the console channel ID is invalid
     */
    public ConsoleDiscordManager(@NotNull StringsDiscord strings) {
        this.strings = strings;
        appender = new LogAppender(this);
        consoleChannel = getConsoleChannel();
        consoleSender = strings.getServer().getConsoleSender();
        strings.getJda().addEventListener(this);
        appender.start();
    }

    public void shutdown() {
        appender.stop();
    }

    @NotNull
    private MessageChannel getConsoleChannel() {
        String consoleId = strings.getSettings().getString(Option.Text.DISCORD_CONSOLE_ID);
        JDA jda = strings.getJda();

        GuildChannel guildChannel = jda.getGuildChannelById(consoleId);
        if (guildChannel instanceof MessageChannel channel) {
            return channel;
        }

        throw new IllegalArgumentException("Failed to load console Channel.");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        executeCommand(event.getMessage().getContentRaw());
    }

    void postMessage(@NotNull String message) {
        try {
            consoleChannel.sendMessage(message).queue();
        } catch(Exception ignored) {}
    }

    private void executeCommand(@NotNull String command) {
        strings.getServer().dispatchCommand(consoleSender, command);
    }
}
